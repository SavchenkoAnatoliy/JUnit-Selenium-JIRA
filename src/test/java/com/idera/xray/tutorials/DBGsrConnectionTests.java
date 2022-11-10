package com.idera.xray.tutorials;

import app.getxray.xray.junit.customjunitxml.XrayTestReporter;
import app.getxray.xray.junit.customjunitxml.XrayTestReporterParameterResolver;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
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
public class DBGsrConnectionTests {
    // Connection object
    static Connection con = null;
    // Statement object
    private static Statement stmt;
    //TESTED ON DEV ENV BECAUSE QA IS OUT OF ORDER
    // Constant for Database URL
    public static String DB_URL = "jdbc:postgresql://10.230.47.34:5432/azdev02db";
    //Database Username
    public static String DB_USER = "gsr@azqa01-db01";
    // Database Password
    public static String DB_PASSWORD = "gsr";
    // Repository
    RepositoryParser repo;
    // Webdriver
    public static WebDriver driver;
    // Wait Obj
    public static WebDriverWait wait;
    //IMPORTANT: EVERY TIME THE JACKPOT GROUP CODE SHOULD BE NEW, NO WAY TO DELETE THIS CODE
    public static String jackpotGroupCode = "BBBC";
    //MSJJackpotGroupName
    public static String MSJJackpotGroupName = "AutomationMSJ";
    public static String query;
    public static ResultSet rs;

    @BeforeEach
    public void setup() {
        try{
        // Get connection to DB
        con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        // Statement object to send the SQL statement to the Database
        stmt = con.createStatement();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        repo = new RepositoryParser("./src/configs/object.properties");

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--no-sandbox"); // Bypass OS security model, to run in Docker
//        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        repo = new RepositoryParser("./src/configs/object.properties");
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterEach
    public void tearDown() throws Exception {
//        driver.quit();
//        driver = null;
        repo = null;
        con.close();
    }

    @Test
    //@XrayTest(key = "RGSEINST-2015")
    public void verifyPresenceOfJackpotGroupOnDB(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
        //Go to RGS
        driver.get("http://10.230.47.75:10980/ng/#/newlogin");
        xrayReporter.addComment("Open RGS Page");

        //Login
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='uname1']"))).sendKeys("super");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='pwd1']"))).sendKeys("password");
        driver.findElement(By.xpath("//*[@id='btnLogin']")).click();
        xrayReporter.addComment("Sing Up button clicked");
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='sideMenuList']")));
        Boolean LoginSuccess = menu.isDisplayed();
        Assert.assertEquals(LoginSuccess, true);
        xrayReporter.addComment("Login Success");

        //Open MSJ -> Jackpot Groups and add New Jackpot Group
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MSJ']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MsjJackpotGroups']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Add MSJ Jackpot Group')]"))).click();
        xrayReporter.addComment("Jackpot Group adding page Opened");

        //Input fields insert
        xrayReporter.addComment("Start input fields");
        //CHange XPATH TO MORE VERBOSE AND PUNCTUAL
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-0-content']/div/div/table/tbody/tr[1]/td[3]/input"))).sendKeys(MSJJackpotGroupName);
        //IMPORTANT: EVERY TIME THE JACKPOT GROUP CODE SHOULD BE NEW, NO WAY TO DELETE THIS CODE
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-0-content']/div/div/table/tbody/tr[2]/td[3]/input"))).sendKeys(jackpotGroupCode);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-0-content']/div/div/table/tbody/tr[3]/td[3]/p-dropdown/div/label"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li/span[contains(text(), 'JG_EUR_S01-1005_MSJ')]"))).click();
        Thread.sleep(1500);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Save')]"))).click();
        WebElement terminateJackpot_Btn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Terminate this Jackpot Group')]")));
        Assert.assertEquals(terminateJackpot_Btn.isDisplayed(), true);
        xrayReporter.addComment("Jackpot Group Added");

        //DB Creating statment for update PLUGIN_NAME directly on DB
        //In future need to change SQL statement to swagger API call

        xrayReporter.addComment("Creating SQL statment");
        query = "select * from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("Skin Code extract from DB " + rs.getString(7));
            assertEquals(rs.getString(7), jackpotGroupCode);
            xrayReporter.addComment("Skin Code extract from DB matched");
        }
    }

    @Test
    //@XrayTest(key = "RGSEINST-2019")
    public void CheckUpdateActionOnDB(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
        //Go to RGS
        driver.get("http://10.230.47.75:10980/ng/#/newlogin");
        xrayReporter.addComment("Open RGS Page");

        //Login
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='uname1']"))).sendKeys("super");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='pwd1']"))).sendKeys("password");
        driver.findElement(By.xpath("//*[@id='btnLogin']")).click();
        xrayReporter.addComment("Sing Up button clicked");
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='sideMenuList']")));
        Boolean LoginSuccess = menu.isDisplayed();
        Assert.assertEquals(LoginSuccess, true);
        xrayReporter.addComment("Login Success");

        //Open MSJ -> Jackpot Groups and add New Jackpot Group
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MSJ']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MsjJackpotGroups']"))).click();

        //Open Jackpot Group previously created, change it's name and save
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr/td[contains(text(), '" + jackpotGroupCode + "')]/preceding-sibling::td[2]/a[contains(@class, 'font-16')]"))).click();
        String nameChanger = "AutoCambio";
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@formcontrolname='groupInstanceName']"))).sendKeys(nameChanger);
        String newJackpotGroupName = MSJJackpotGroupName+nameChanger;
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(),'Save')]"))).click();
        Boolean saveCanges = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//p[contains(text(), 'Your changes have been saved.')]"))).isDisplayed();
        Assert.assertEquals(saveCanges,true);

        //DB Creating statement control jackpotGroupName saved on DB
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "select group_instance_family_name  from jps.tbjps_group_instance_family where group_instance_family_code ='" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("group_instance_family_name extract for " + jackpotGroupCode + " from DB is " + rs.getString(1));
            assertEquals(rs.getString(1), newJackpotGroupName);
            xrayReporter.addComment("group_instance_family_name extract from DB matched");
        }

        //Turn back the Jackpot Group name
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@formcontrolname='groupInstanceName']"))).clear();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@formcontrolname='groupInstanceName']"))).sendKeys(MSJJackpotGroupName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(),'Save')]"))).click();
        saveCanges = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//p[contains(text(), 'Your changes have been saved.')]"))).isDisplayed();
        Assert.assertEquals(saveCanges,true);
    }



}
