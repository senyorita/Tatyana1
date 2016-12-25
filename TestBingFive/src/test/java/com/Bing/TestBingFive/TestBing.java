package com.Bing.TestBingFive;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestBing {

	RemoteWebDriver RemoteDriver;

	@Test()
	public void Test1() {
		RemoteDriver.get("http://www.bing.com/");

		WebElement Logotip = RemoteDriver.findElement(By.xpath("//div[@class='hp_sw_logo hpcLogoWhite']"));
		Assert.assertTrue(Logotip.isDisplayed(), "Логотип сайта не отображается");

		WebElement ButtonSearch = (new WebDriverWait(RemoteDriver, 10)).until(
				ExpectedConditions.visibilityOf(RemoteDriver.findElement(By.xpath("//input[@id='sb_form_go']"))));
		Assert.assertTrue(ButtonSearch.isDisplayed(), "Кнопка для поиска запросов не отображается");
	}

	@Test(dataProvider = "Select", dependsOnMethods = "Test1")
	public void Test2(String Select) {

		WebElement FieldInput = (new WebDriverWait(RemoteDriver, 5))
				.until(ExpectedConditions.visibilityOf(RemoteDriver.findElement(By.xpath("//input[@id='sb_form_q']"))));
		Assert.assertTrue(FieldInput.isDisplayed(), "Поле для ввода запросов не отображается");
		FieldInput.sendKeys(Select);

		List<WebElement> ListSelect = (new WebDriverWait(RemoteDriver, 10))
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//ul[@id='sa_ul']/li[@id]")));

		for (int i = 0; i < ListSelect.size(); i++) {
			switch (ListSelect.get(i).getText()) {
			case "automation": {
				ListSelect.get(i).click();
				Parsing();
				ListSelect.clear();
				break;
			}
			case "car tesla": {
				ListSelect.get(i).click();
				Parsing();
				ListSelect.clear();
				break;
			}
			default:
				continue;
			}
		}
	}

	public void Parsing() {
		List<WebElement> ListSelect = RemoteDriver
				.findElements(By.xpath("//ol[@id='b_results']/li[@class='b_algo']//h2/a"));

		List<String> ListUrlSearch = new ArrayList<String>();

		for (WebElement a : ListSelect) {
			System.out.println(a.getAttribute("href"));
			ListUrlSearch.add(a.getAttribute("href"));
		}
		List<String> ListUrlOpenSite = new ArrayList<String>();
		for (int i = 0; i < ListSelect.size(); i++) {
			ListSelect.get(i).click();
			ListUrlOpenSite.add(RemoteDriver.getCurrentUrl());
			RemoteDriver.navigate().back();
			ListSelect = RemoteDriver.findElements(By.xpath("//ol[@id='b_results']/li[@class='b_algo']//h2/a"));
		}
		ListSelect.clear();
		System.out.println("\n");
		for (int i = 0; i < ListUrlSearch.size(); i++) {
			Assert.assertTrue(ListUrlSearch.get(i).equals(ListUrlOpenSite.get(i)),
					"Ссылки из списка поиска не совпадают с ссылками открытых сайтов");
		}
		RemoteDriver.navigate().back();
	}

	@DataProvider
	public Object[][] Select() {
		return new Object[][] { { "automatio" }, { "car tesl" } };
	}

	@BeforeTest
	@Parameters({ "Browser" })
	public void beforeTest(String Browser) {
		switch (Browser) {
		case "Firefox": {
			try {
				RemoteDriver = new RemoteWebDriver(new URL("http://192.168.0.105:4444/wd/hub"),
						DesiredCapabilities.firefox());
				RemoteDriver.manage().window().maximize();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		case "Chrome": {
			try {
				RemoteDriver = new RemoteWebDriver(new URL("http://192.168.0.105:4444/wd/hub"),
						DesiredCapabilities.chrome());
				RemoteDriver.manage().window().maximize();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		case "Android": {
			try {
				RemoteDriver = new RemoteWebDriver(new URL("http://192.168.0.105:4444/wd/hub"),
						DesiredCapabilities.android());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		}
	}

	@AfterTest
	public void afterTest() {
		RemoteDriver.quit();
	}

}
