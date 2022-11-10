package com.idera.xray.tutorials;

import app.getxray.xray.junit.customjunitxml.XrayTestReporter;
import app.getxray.xray.junit.customjunitxml.XrayTestReporterParameterResolver;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(XrayTestReporterParameterResolver.class)
public class DBNssConnectionTests {
    // Connection object
    static Connection con = null;
    // Statement object
    private static Statement stmt;
    // Constant for Database URL
    public static String DB_URL = "jdbc:postgresql://10.230.47.32:5432/azqa01db";
    //Database Username
    public static String DB_USER = "nss@azqa01-db01";
    // Database Password
    public static String DB_PASSWORD = "nss";
    // Repository
    public static RepositoryParser repo;
    // Webdriver
    public static WebDriver driver;
    // Wait Obj
    public static WebDriverWait wait;

    @BeforeEach
    public void setup() {
        try {
            // Get connection to DB
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // Statement object to send the SQL statement to the Database
            stmt = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--no-sandbox"); // Bypass OS security model, to run in Docker
//        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        repo = new RepositoryParser("./src/configs/object.properties");
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterEach
    public void tearDown() throws Exception {
        driver.quit();
        driver = null;
        repo = null;
        con.close();
    }

    //For this test the skin code is stored in configs -> objects.properties (Issue id JIRA-PROD = RGSEINST-3093)
    @Test
    //@XrayTest(key = "RGSEINST-3093")
    public void verifyNssSsectionFilledOnGuiIsSavedOnDB(XrayTestReporter xrayReporter) throws SQLException {
        //Go to RGS
        driver.get("http://10.230.47.75:10190/ng/#/newlogin");
        xrayReporter.addComment("Open RGS Page");

        //Login
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='uname1']"))).sendKeys("super");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='pwd1']"))).sendKeys("password");
        driver.findElement(By.xpath("//*[@id='btnLogin']")).click();
        xrayReporter.addComment("Sing Up button clicked");
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='sideMenuList']")));
        Boolean LoginSuccess = menu.isDisplayed();
        assertEquals(LoginSuccess, true);
        xrayReporter.addComment("Login Success");

        //Operators Settings section opening
        driver.findElement(By.xpath("//*[@id='sideMenuList']/div/div[4]/div[1]/a")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='operatorsSettings']/span[2]"))).click();

        //Skin Code searching
        String skinCode = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='QA1001']"))).getText();
        assertEquals(skinCode, repo.getBy("nss.expected.skincode"));
        xrayReporter.addComment("Skin Code " + repo.getBy("nss.expected.skincode") + " found");

        //DB Creating statment
        xrayReporter.addComment("Creating SQL statment");
        String query = "select * from nss.skin_settings where skin_code = '" + repo.getBy("nss.expected.skincode") + "'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        ResultSet rs = stmt.executeQuery(query);

        //DB
        if (rs.next()) {
            xrayReporter.addComment("Skin Code extract from DB " + rs.getString(1));
            assertEquals(rs.getString(1), repo.getBy("nss.expected.skincode"));
            xrayReporter.addComment("Skin Code extract from DB matched");
        }
        xrayReporter.addComment("Test Finished successfully");
    }

    @Test
    //@XrayTest(key = "RGSEINST-3093")
    public void verifyNssSsectionUpdatedOnDbPresentOnGUI(XrayTestReporter xrayReporter) throws SQLException {
        //DB Creating statment for update PLUGIN_NAME directly on DB
        //In future need to change SQL statement to swagger API call

//        xrayReporter.addComment("Creating SQL statment");
        String config_value = "test_plugin_name";
//        String query = "UPDATE nss.skin_settings\n" +
//                "SET config_value='"+config_value+"'\n" +
//                "WHERE skin_code='" + repo.getBy("nss.expected.skincode") + "' AND config_id='PLUGIN_NAME'";
//        stmt = con.createStatement();
//        xrayReporter.addComment("Statment Executed");
//        ResultSet rs = stmt.executeQuery(query);

        //Go to RGS
        driver.get("http://10.230.47.75:10190/ng/#/newlogin");
        xrayReporter.addComment("Open RGS Page");

        //Login
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='uname1']"))).sendKeys("super");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='pwd1']"))).sendKeys("password");
        driver.findElement(By.xpath("//*[@id='btnLogin']")).click();
        xrayReporter.addComment("Sing Up button clicked");
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='sideMenuList']")));
        Boolean LoginSuccess = menu.isDisplayed();
        assertEquals(LoginSuccess, true);
        xrayReporter.addComment("Login Success");

        //Operators Settings section opening
        driver.findElement(By.xpath("//*[@id='sideMenuList']/div/div[4]/div[1]/a")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='operatorsSettings']/span[2]"))).click();
        xrayReporter.addComment("Operators Settings opened");

        //Open expected Skin Code
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='QA1001']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-accordiontab-3']"))).click();
        String pluginNameURL = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(), 'Plugin Name URL ')]/following-sibling::div/input']"))).getText();
        xrayReporter.addComment("Expected Skin Code Opened");

        assertEquals(pluginNameURL, config_value, "Matching text inserted in DB");
        xrayReporter.addComment("Value matched inserted in DB and inside Front-end");
    }

}
