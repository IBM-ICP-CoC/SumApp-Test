package com.ibm.icp.coc.sumapp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SumTest {
	
	private WebDriver webDriver;
	private String targetUrl;
	
	public SumTest(WebDriver webDriver, String targetUrl) {
		this.webDriver = webDriver;
		this.targetUrl = targetUrl;
	}

	public TestResult testSum( int a, int b ) throws InterruptedException {
		
		TestResult testRun = new TestResult();
		testRun.setOp1(a);
		testRun.setOp2(b);

		webDriver.navigate().to(targetUrl);

		String titleElm = webDriver.findElement(By.cssSelector("H1")).getText();
		boolean titleFound = titleElm.contains("Sum App");
		testRun.setTitleFound( titleFound );
		
		WebElement op1 = webDriver.findElement(By.id("op1"));
		WebElement op2 = webDriver.findElement(By.id("op2"));
		WebElement btnSum = webDriver.findElement(By.className("btn"));
		
		String op1Str = Integer.toString(a);		
		op1.sendKeys(op1Str);
		
		String op2Str = Integer.toString(b);		
		op2.sendKeys(op2Str);
		
		int expectedSum = a + b;
		String expected = op1Str + " + " + op2Str + " = " + Integer.toString(expectedSum);
		
		WebElement result = webDriver.findElement(By.id("result"));
		
		WebDriverWait wait = new WebDriverWait(webDriver, 1); 
		
		btnSum.click();
		
		try {
			wait.until(ExpectedConditions.attributeToBe(By.id("result"), "value", expected));
		} catch( org.openqa.selenium.TimeoutException toe ) {
			
		}
		
		String val = result.getText();
		
		testRun.setResultString(val);
		
		boolean passed = val.equals(expected) && titleFound;
		testRun.setPassed(passed);
		
		return testRun;
	}

}
