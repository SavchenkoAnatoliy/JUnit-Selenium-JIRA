package com.idera.xray.tutorials;

import app.getxray.xray.junit.customjunitxml.XrayTestReporter;
import app.getxray.xray.junit.customjunitxml.XrayTestReporterParameterResolver;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
public class DBCoreConnectionTests {

    //Logger log4j
    private static final Logger logger = LogManager.getLogger(com.idera.xray.tutorials.DBCoreConnectionTests.class);

    // Connection object
    static Connection con = null;
    // Statement object
    private static Statement stmt;
    // Constant for Database URL
    public static String DB_URL = "jdbc:postgresql://10.230.47.4:5432/azdev02db";
    //Database Username
    public static String DB_USER = "core@azdev-db01";
    // Database Password
    public static String DB_PASSWORD = "core";
    // Repository
    RepositoryParser repo;
    public static WebDriver driver;
    // Wait Obj
    public static WebDriverWait wait;
    //IMPORTANT: EVERY TIME THE JACKPOT GROUP CODE SHOULD BE NEW, NO WAY TO DELETE THIS CODE
    public static String jackpotGroupCode = "BBBC";
    //MSJJackpotGroupName
    public static String MSJJackpotGroupName = "AutomationMSJ";
    //Skin Code
    public static String skinCode="LT01";


    public static String query;
    public static ResultSet rs;

    @BeforeEach
    public void setup() {
        try{
            // Get connection to DB
            logger.info("Connection started");

            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // Statement object to send the SQL statement to the Database
            stmt = con.createStatement();
        }
        catch (Exception e)
        {
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
//        driver.quit();
//        driver = null;
        repo = null;
        con.close();
    }

    @Test
    @XrayTest(key = "EINSTPIN-696")
    public void  verifyWinningTransactionInDB(XrayTestReporter xrayReporter) throws SQLException {
        xrayReporter.addComment("Creating statment");
        logger.warn("Creating statment");
        String query = "select * from core.try_winnings_transaction";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);
        assertEquals(rs.next(), true);
        xrayReporter.addComment("----- Last Row print start -----");
        while(rs.next()) {
            if (rs.isLast()){
                xrayReporter.addComment(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " + rs.getString(6) + " " + rs.getString(7) + " " + rs.getString(8) + " " + rs.getString(9) + " " + rs.getString(10) + " " + rs.getString(11) + " " + rs.getString(12) + " " + rs.getString(13) + " " + rs.getString(14) + " " + rs.getString(15) + " " + rs.getString(16) + " " + rs.getString(17) + " " + rs.getString(18) + " " + rs.getString(19) + " " + rs.getString(20) + " " + rs.getString(21) + " " + rs.getString(22));
                System.out.print(rs.getString(1));
                System.out.print(" " + rs.getString(2));
                System.out.print(" " + rs.getString(3));
                System.out.print(" " + rs.getString(5));
                System.out.print(" " + rs.getString(6));
                System.out.print(" " + rs.getString(7));
                System.out.print(" " + rs.getString(8));
                System.out.print(" " + rs.getString(9));
                System.out.print(" " + rs.getString(10));
                System.out.print(" " + rs.getString(11));
                System.out.print(" " + rs.getString(12));
                System.out.print(" " + rs.getString(13));
                System.out.print(" " + rs.getString(14));
                System.out.print(" " + rs.getString(15));
                System.out.print(" " + rs.getString(16));
                System.out.print(" " + rs.getString(17));
                System.out.print(" " + rs.getString(18));
                System.out.print(" " + rs.getString(19));
                System.out.print(" " + rs.getString(20));
                System.out.print(" " + rs.getString(21));
                System.out.println(" " + rs.getString(22));
            }
        }
        xrayReporter.addComment("----- Last Row printed successfully -----");
    }

    @Test
    @XrayTest(key = "EINSTPIN-698")
    public void   checkSkinInDB(XrayTestReporter xrayReporter) throws SQLException {
        xrayReporter.addComment("Creating statment");
        logger.warn("Creating statement");
        String query = "select * from core.skin_code";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);
        assertEquals(rs.next(), true);
        xrayReporter.addComment("----- Last SKIN CODE print start -----");
        while(rs.next()) {
            if (rs.isLast()){
                xrayReporter.addComment(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5));
                System.out.print(rs.getString(1));
                System.out.print(" " + rs.getString(2));
                System.out.print(" " + rs.getString(3));
                System.out.println(" " + rs.getString(5));
            }
        }
        xrayReporter.addComment("----- Last SKIN CODE printed successfully -----");
    }

    @Test
    //For execute correctly this test need new skinCode and new skinName
    //@XrayTest(key = "RGSEINST-2831")
    public void   createSkinCodeandverifyPresenseOnDB(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
        //Go to RGS
        driver.get("http://10.230.47.71:10190/ng/#/home-page/");
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

        //Operators -> Operators Settings
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a/span[.='Operators']"))).click();
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='operatorsSettings']"))).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button/span[contains(text(), 'Add MSJ Jackpot Group')]")));
        xrayReporter.addComment("Operators Settings page Opened");

        //Opening Lottery eInstant Operator -> Add Skin Code
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[(contains(text(),'Lottery eInstant')) and (@class='operator-name')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(),'Add Skin')]"))).click();
        //New skinName and skinCode should be unique
        String new_skinName = "AutomationSkinName6";
        String new_skinCode = "AMSC6";
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Skin Name')]/following-sibling::div/input"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), new_skinName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Skin Code')]/following-sibling::div/input"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), new_skinCode);
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(),'Save Settings')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(),'Yes')]"))).click();
        Thread.sleep(2000);


        //DB Creating statment extract skinCode of jackpotGroupCode on DB
        xrayReporter.addComment("Extracting new_skinCode");
        xrayReporter.addComment("Creating SQL statment");
        query = "select skin_code from core.skin_code where skin_code = '"+new_skinCode+"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            assertEquals(rs.getString(1), new_skinCode);
            xrayReporter.addComment("skinCode extracted from DB is " + rs.getString(1));
        }
    }

    @Test
    //Working environment DB_URL = "jdbc:postgresql://10.230.47.4:5432/azdev02db"; DB_USER = "core@azdev-db01"; DB_PASSWORD = "core";
    //@XrayTest(key = "RGSEINST-2964")(Executed on DEV because QA is not ready)
    public void verifySettingOfWinningRetries(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
        //Go to RGS
        driver.get("http://10.230.47.71:10190/ng/#/home-page/");
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

        //Operators -> Operators Settings
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a/span[.='Operators']"))).click();
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='operatorsSettings']"))).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button/span[contains(text(), 'Add MSJ Jackpot Group')]")));
        xrayReporter.addComment("Operators Settings page Opened");

        //Open Operator previously created -> Skin
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr/td[contains(text(), '" + skinCode + "')]/preceding-sibling::td/span"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Winning Retries')]/following-sibling::div/input"))).clear();
        String winning_retries = "* * * 2 *";
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Winning Retries')]/following-sibling::div/input"))).sendKeys(winning_retries);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Update Settings')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Yes')]"))).click();


        //DB Creating statment controll config_value of skinCode
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        String query = "select config_value  from core.skin_settings where skin_code = '"+skinCode+"' and config_id = 'WINNINGS_RETRIES'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("config_value for " + skinCode + " extracted from DB is " + rs.getString(1));
            assertEquals(rs.getString(1), winning_retries);
            xrayReporter.addComment("config_value extract from DB matched");
        }
    }

    @Test
    //Working environment DB_URL = "jdbc:postgresql://10.230.47.4:5432/azdev02db"; DB_USER = "core@azdev-db01"; DB_PASSWORD = "core";
    //@XrayTest(key = "RGSEINST-2962")(Executed on DEV because QA is not ready)
    public void verifySettingOfRefundRetries(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
        //Go to RGS
        driver.get("http://10.230.47.71:10190/ng/#/home-page/");
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

        //Operators -> Operators Settings
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a/span[.='Operators']"))).click();
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='operatorsSettings']"))).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button/span[contains(text(), 'Add MSJ Jackpot Group')]")));
        xrayReporter.addComment("Operators Settings page Opened");

        //Open Operator previously created -> Skin
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr/td[contains(text(), '" + skinCode + "')]/preceding-sibling::td/span"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Refund Retries')]/following-sibling::div/input"))).clear();
        String refund_retries = "* * * 4 *";
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Refund Retries')]/following-sibling::div/input"))).sendKeys(refund_retries);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Update Settings')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Yes')]"))).click();


        //DB Creating statment controll config_value of skinCode
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        String query = "select config_value  from core.skin_settings where skin_code = '"+skinCode+"' and config_id = 'REFUND_RETRIES'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("config_value for " + skinCode + " extracted from DB is " + rs.getString(1));
            assertEquals(rs.getString(1), refund_retries);
            xrayReporter.addComment("config_value extract from DB matched");
        }
    }
}
