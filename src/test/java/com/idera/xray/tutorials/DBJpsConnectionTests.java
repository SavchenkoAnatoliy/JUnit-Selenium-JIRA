package com.idera.xray.tutorials;

import app.getxray.xray.junit.customjunitxml.XrayTestReporter;
import app.getxray.xray.junit.customjunitxml.XrayTestReporterParameterResolver;
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
public class DBJpsConnectionTests {
    // Connection object
    static Connection con = null;
    // Statement object
    private static Statement stmt;
    // Constant for Database URL
    public static String DB_URL = "jdbc:postgresql://10.230.47.32:5432/azqa01db";
    //Database Username
    public static String DB_USER = "jps@azqa01-db01";
    // Database Password
    public static String DB_PASSWORD = "jps";
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

    @Test
    //@XrayTest(key = "RGSEINST-2736")
    public void CheckUpdateActionOnDBVersion2(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
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



    @Test
    //@XrayTest(key = "RGSEINST-2743")
    public void CheckTerminationOfMSJJackpotGroupFromDB(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
        //DB Creating statment controll jackpotGroupCode exist on DB
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        String query = "select * from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("Skin Code extract from DB " + rs.getString(7));
            assertEquals(rs.getString(7), jackpotGroupCode);
            xrayReporter.addComment("Skin Code extract from DB matched");
        }

        //DB Creating statment for update grpinst_status_code directly on DB
        xrayReporter.addComment("Updating grpinst_status_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "update jps.tbjps_jackpot_group_instance \n" +
                "set grpinst_status_code='RETD'\n" +
                "where group_instance_family_code = '"+ jackpotGroupCode +"'";
        stmt = con.createStatement();
        stmt.executeUpdate(query);
        xrayReporter.addComment("Statment Executed");

        //DB Creating statment controll jackpotGroupCode exist on DB
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "select grpinst_status_code from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("group_instance_family_code extract for " + jackpotGroupCode + " from DB is " + rs.getString(1));
            assertEquals(rs.getString(1), "RETD");
            xrayReporter.addComment("group_instance_family_code extract from DB matched");
        }

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

        //Open MSJ -> Jackpot Groups
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MSJ']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MsjJackpotGroups']"))).click();
        xrayReporter.addComment("Jackpot Groups page Opened");

        //Return MSJ Jackpot Group to ACTV
        //Update MSJ Jackpot Group previously terminated with update to RETD

        //DB Creating statment for update grpinst_status_code directly on DB
        xrayReporter.addComment("Updating grpinst_status_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "update jps.tbjps_jackpot_group_instance \n" +
                "set grpinst_status_code='ACTV'\n" +
                "where group_instance_family_code = '"+ jackpotGroupCode +"'";
        stmt = con.createStatement();
        stmt.executeUpdate(query);
        xrayReporter.addComment("Statment Executed");

        //DB Creating statment controll jackpotGroupCode is ACTV on DB
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "select grpinst_status_code from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("group_instance_family_code extract for " + jackpotGroupCode + " from DB is " + rs.getString(1));
            assertEquals(rs.getString(1), "ACTV");
            xrayReporter.addComment("group_instance_family_code extract from DB matched");
        }
    }

    @Test
    //@XrayTest(key = "RGSEINST-2026")
    public void CheckTerminationOfMSJJackpotGroupFromDB_Story2(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
        //DB Creating statment controll jackpotGroupCode exist on DB
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        String query = "select * from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("Skin Code extract from DB " + rs.getString(7));
            assertEquals(rs.getString(7), jackpotGroupCode);
            xrayReporter.addComment("Skin Code extract from DB matched");
        }

        //DB Creating statment for update grpinst_status_code directly on DB
        xrayReporter.addComment("Updating grpinst_status_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "update jps.tbjps_jackpot_group_instance \n" +
                "set grpinst_status_code='RETD'\n" +
                "where group_instance_family_code = '"+ jackpotGroupCode +"'";
        stmt = con.createStatement();
        stmt.executeUpdate(query);
        xrayReporter.addComment("Statment Executed");

        //DB Creating statment controll jackpotGroupCode exist on DB
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "select grpinst_status_code from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("group_instance_family_code extract for " + jackpotGroupCode + " from DB is " + rs.getString(1));
            assertEquals(rs.getString(1), "RETD");
            xrayReporter.addComment("group_instance_family_code extract from DB matched");
        }

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

        //Open MSJ -> Jackpot Groups
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MSJ']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MsjJackpotGroups']"))).click();
        xrayReporter.addComment("Jackpot Groups page Opened");

        //Return MSJ Jackpot Group to ACTV
        //Update MSJ Jackpot Group previously terminated with update to RETD

        //DB Creating statment for update grpinst_status_code directly on DB
        xrayReporter.addComment("Updating grpinst_status_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "update jps.tbjps_jackpot_group_instance \n" +
                "set grpinst_status_code='ACTV'\n" +
                "where group_instance_family_code = '"+ jackpotGroupCode +"'";
        stmt = con.createStatement();
        stmt.executeUpdate(query);
        xrayReporter.addComment("Statment Executed");

        //DB Creating statment controll jackpotGroupCode is ACTV on DB
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        query = "select grpinst_status_code from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("group_instance_family_code extract for " + jackpotGroupCode + " from DB is " + rs.getString(1));
            assertEquals(rs.getString(1), "ACTV");
            xrayReporter.addComment("group_instance_family_code extract from DB matched");
        }
    }

    @Test
    //@XrayTest(key = "RGSEINST-2025")
    public void CheckTerminationOfMSJJackpotGroupOnDBFromFronEnd(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
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

        //Open MSJ -> Jackpot Groups
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MSJ']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MsjJackpotGroups']"))).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button/span[contains(text(), 'Add MSJ Jackpot Group')]")));
        xrayReporter.addComment("Jackpot Groups page Opened");

        //Open Jackpot Group previously created and terminate it
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr/td[contains(text(), '" + jackpotGroupCode + "')]/preceding-sibling::td[2]/a[contains(@class, 'font-16')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(),'Terminate this Jackpot Group')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea"))).sendKeys("Automation Reason");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[.='OK']"))).click();

        //Control pop-up header text
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div/span[contains(text(), 'This action cannot be undone')]")));
        Assert.assertEquals("This action cannot be undone.", driver.findElement(By.xpath("//div/span[contains(text(), 'This action cannot be undone')]")).getText().trim());
        Thread.sleep(2000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@label='OK']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'MSJ Jackpot Group Names')]")));
        //BUG IN QA ENV -> DID NOT CLICKED OK ON THE LAST INFO POP-UP
        Assert.assertTrue("Wating for correct turning to home page after Termination of Jackpot", driver.findElement(By.xpath("//div[contains(text(), 'MSJ Jackpot Group Names')]")).isDisplayed());

        //DB Creating statment controll jackpotGroupCode exist on DB
        xrayReporter.addComment("Extracting group_instance_family_code");
        xrayReporter.addComment("Creating SQL statment");
        String query = "select grpinst_status_code from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode +"'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("group_instance_family_code extract for " + jackpotGroupCode + " from DB is " + rs.getString(1));
            assertEquals(rs.getString(1), "RETD");
            xrayReporter.addComment("group_instance_family_code extract from DB matched");
        }
    }

    @Test
    //@XrayTest(key = "RGSEINST-2025")
    public void VerifyPresenceOfJackpotGroupCreatedOnDB(XrayTestReporter xrayReporter) throws SQLException {
        String grp_instance_id = null;
        String band_id = null;
        String jackpot_id=null;
        //DB Creating statment extract grp_instance_id of jackpotGroupCode on DB
        xrayReporter.addComment("Extracting grp_instance_id");
        xrayReporter.addComment("Creating SQL statment");
        query = "select grp_instance_id from jps.tbjps_jackpot_group_instance where group_instance_family_code = '" + jackpotGroupCode + "'";
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            grp_instance_id = rs.getString(1);
            xrayReporter.addComment("grp_instance_id extract for " + jackpotGroupCode + " from DB is " + rs.getString(1));
        }

        //DB Creating statment extract band_id of jackpotGroupCode on DB
        xrayReporter.addComment("Extracting grp_instance_id");
        xrayReporter.addComment("Creating SQL statment");
        query = "select band_id from jps.tbjps_band_instance where grp_instance_id = "+grp_instance_id;
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        if (rs.next()) {
            xrayReporter.addComment("If band_id exist means that jackpotGroupCode was associated at some jackpot");
            band_id = rs.getString(1);
            //If band_id exist means that jackpotGroupCode was associated at some jackpot
            xrayReporter.addComment("band_id extract for " + jackpotGroupCode + " from DB is " + rs.getString(1));
        }

        //DB Creating statment extract jackpot_id of jackpotGroupCode on DB
        xrayReporter.addComment("Extracting jackpot_id");
        xrayReporter.addComment("Creating SQL statment");
        query = "select jackpot_id from jps.tbjps_jackpot_band_config where band_id  = "+band_id;
        stmt = con.createStatement();
        xrayReporter.addComment("Statment Executed");
        rs = stmt.executeQuery(query);

        while (rs.next()) {
            System.out.println(rs.getString(1));
            //If band_id exist means that jackpotGroupCode was associated at some jackpot
            xrayReporter.addComment("jackpot_id extracted for " + jackpotGroupCode + " from DB is " + rs.getString(1));
        }
    }


    @Test
    //@XrayTest(key = "RGSEINST-2021")
    public void CheckUpdateActionOnConfigurationTabOnDB(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
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

        //Open MSJ -> Jackpot Groups
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MSJ']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MsjJackpotGroups']"))).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button/span[contains(text(), 'Add MSJ Jackpot Group')]")));
        xrayReporter.addComment("Jackpot Groups page Opened");

        //Open Jackpot Group previously created -> Configuration -> Heat Index -> Jackpot 1
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr/td[contains(text(), '" + jackpotGroupCode + "')]/preceding-sibling::td[2]/a[contains(@class, 'font-16')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a/span[contains(text(), 'Configuration')]"))).click();
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='S01-1005-01']"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), "10");
        String heat_idex_1 = "11";
        String heat_idex_2 = "12";
        String heat_idex_3 = "13";
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-2-content']/div/div/div[2]/jps-msj-heat-indexes-table/div/p-table/div/div[2]/table/tbody/tr[9]/td[2]/jps-heat-index-input/div/input"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), heat_idex_1);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-2-content']/div/div/div[2]/jps-msj-heat-indexes-table/div/p-table/div/div[2]/table/tbody/tr[9]/td[3]/jps-heat-index-input/div/input"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), heat_idex_2);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-2-content']/div/div/div[2]/jps-msj-heat-indexes-table/div/p-table/div/div[2]/table/tbody/tr[9]/td[4]/jps-heat-index-input/div/input"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), heat_idex_3);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Save')]"))).click();
        Assert.assertTrue("Pop-up confirmation",wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div/p[contains(text(),'Your changes have been saved.')]"))).isDisplayed());

        //Open Jackpot Values
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a/span[contains(text(), 'Jackpot Values')]"))).click();
        Assert.assertTrue("Jackpot is displayed",wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(text(), 'Jackpot 1')]/following-sibling::td[02]"))).isDisplayed());
        String jackpot_id = driver.findElement(By.xpath("//td[contains(text(), 'Jackpot 1')]/following-sibling::td[03]")).getText().trim();

        //DB Creating statment controll the values exists on DB
        xrayReporter.addComment("Extracting future_heat_index");
        xrayReporter.addComment("Creating SQL statment");
        String query = "SELECT\n" +
                "            j.jackpot_id_str,\n" +
                "            ji.jkptinstanceid_str,\n" +
                "            j.jackpot_name,\n" +
                "            jc.meter_value_amt,\n" +
                "            jc.reserve_meter_amt,\n" +
                "            jc.oef_meter_amt,\n" +
                "            jc.reset_amt,\n" +
                "            jc.app_jackpot_cycle_id,\n" +
                "            jc.start_dt,\n" +
                "            jch.seed_value_amt cur_seed_value_amt,\n" +
                "            -- Added 10/30/2017 to obtain current and future max amount\n" +
                "            jch.max_value_amt cur_max_value_amt,\n" +
                "            fnjps_get_jkptInstHeatIndexCfg(ji.jkptinstanceid_str, 'C') current_heat_index,\n" +
                "            -- Added 10/30/2017 current and future wtd resets and triggers\n" +
                "            fnjps_get_jpInstKydWtdConfig(ji.jkptinstanceid_str, 'C') as current_wtd_rst_trg,\n" +
                "            -- Added 10/30/2017 to obtain current and future max amount\n" +
                "            jch_future.seed_value_amt fut_seed_value_amt,\n" +
                "            jch_future.max_value_amt fut_max_value_amt,\n" +
                "            fnjps_get_jkptInstHeatIndexCfg(ji.jkptinstanceid_str, 'F') future_heat_index,\n" +
                "            -- Added 10/30/2017 current and future wtd resets and triggers\n" +
                "            fnjps_get_jpInstKydWtdConfig(ji.jkptinstanceid_str, 'F') as future_wtd_rst_trg\n" +
                "        FROM tbjps_jackpot j\n" +
                "        INNER JOIN tbjps_jackpot_instance ji ON ji.jackpot_id=j.jackpot_id\n" +
                "        INNER JOIN tbjps_jackpot_cycle jc ON jc.jkpt_instance_id=ji.jkpt_instance_id\n" +
                "        -- Added 10/30/2017 to obtain current and future max amount\n" +
                "        INNER JOIN tbjps_jkptinstance_config_hist jch ON jch.jkpt_instance_id=ji.jkpt_instance_id\n" +
                "        INNER JOIN\n" +
                "            (SELECT\n" +
                "            jkpt_instance_id,\n" +
                "            seed_value_amt,\n" +
                "            max_value_amt\n" +
                "            FROM tbjps_jkptinstance_config_hist\n" +
                "            WHERE current_row_ind='Y'\n" +
                "            AND enabled_ind='Y'\n" +
                "            ) jch_future ON jch_future.jkpt_instance_id=ji.jkpt_instance_id\n" +
                "        WHERE j.enabled_ind='Y'\n" +
                "          AND ji.enabled_ind='Y'\n" +
                "          AND jc.jkpt_cycle_status_code='ACTV'\n" +
                "          -- Added 10/30/2017 to obtain current and future max amount\n" +
                "          AND jch.current_jkpt_cycle_row_ind='Y'\n" +
                "          AND ji.jkptinstanceid_str IN ('"+jackpot_id+"');";
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        xrayReporter.addComment("Statment Executed");
        xrayReporter.addComment("Jackpot_ID = "+jackpot_id);
        if (rs.next()) {
            System.out.println(rs.getString(1));
            assertEquals(rs.getString("future_heat_index"), "{"+heat_idex_1+"00"+","+heat_idex_2+"00"+","+heat_idex_3+"00"+"}", "Matching the values from DB");
            xrayReporter.addComment("Matching the values from DB complete");
        }
    }

    @Test
    //@XrayTest(key = "RGSEINST-2738")
    public void CheckUpdateActionOnConfigurationTabOnDBVersion2(XrayTestReporter xrayReporter) throws SQLException, InterruptedException {
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

        //Open MSJ -> Jackpot Groups
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MSJ']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='MsjJackpotGroups']"))).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button/span[contains(text(), 'Add MSJ Jackpot Group')]")));
        xrayReporter.addComment("Jackpot Groups page Opened");

        //Open Jackpot Group previously created -> Configuration -> Heat Index -> Jackpot 1
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr/td[contains(text(), '" + jackpotGroupCode + "')]/preceding-sibling::td[2]/a[contains(@class, 'font-16')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a/span[contains(text(), 'Configuration')]"))).click();
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='S01-1005-01']"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), "10");
        String heat_idex_1 = "12";
        String heat_idex_2 = "13";
        String heat_idex_3 = "14";
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-2-content']/div/div/div[2]/jps-msj-heat-indexes-table/div/p-table/div/div[2]/table/tbody/tr[9]/td[2]/jps-heat-index-input/div/input"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), heat_idex_1);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-2-content']/div/div/div[2]/jps-msj-heat-indexes-table/div/p-table/div/div[2]/table/tbody/tr[9]/td[3]/jps-heat-index-input/div/input"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), heat_idex_2);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ui-panel-2-content']/div/div/div[2]/jps-msj-heat-indexes-table/div/p-table/div/div[2]/table/tbody/tr[9]/td[4]/jps-heat-index-input/div/input"))).sendKeys(Keys.chord(Keys.CONTROL, "a"), heat_idex_3);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/span[contains(text(), 'Save')]"))).click();
        Assert.assertTrue("Pop-up confirmation",wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div/p[contains(text(),'Your changes have been saved.')]"))).isDisplayed());

        //Open Jackpot Values
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a/span[contains(text(), 'Jackpot Values')]"))).click();
        Assert.assertTrue("Jackpot is displayed",wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(text(), 'Jackpot 1')]/following-sibling::td[02]"))).isDisplayed());
        String jackpot_id = driver.findElement(By.xpath("//td[contains(text(), 'Jackpot 1')]/following-sibling::td[03]")).getText().trim();

        //DB Creating statment controll the values exists on DB
        xrayReporter.addComment("Extracting future_heat_index");
        xrayReporter.addComment("Creating SQL statment");
        String query = "SELECT\n" +
                "            j.jackpot_id_str,\n" +
                "            ji.jkptinstanceid_str,\n" +
                "            j.jackpot_name,\n" +
                "            jc.meter_value_amt,\n" +
                "            jc.reserve_meter_amt,\n" +
                "            jc.oef_meter_amt,\n" +
                "            jc.reset_amt,\n" +
                "            jc.app_jackpot_cycle_id,\n" +
                "            jc.start_dt,\n" +
                "            jch.seed_value_amt cur_seed_value_amt,\n" +
                "            -- Added 10/30/2017 to obtain current and future max amount\n" +
                "            jch.max_value_amt cur_max_value_amt,\n" +
                "            fnjps_get_jkptInstHeatIndexCfg(ji.jkptinstanceid_str, 'C') current_heat_index,\n" +
                "            -- Added 10/30/2017 current and future wtd resets and triggers\n" +
                "            fnjps_get_jpInstKydWtdConfig(ji.jkptinstanceid_str, 'C') as current_wtd_rst_trg,\n" +
                "            -- Added 10/30/2017 to obtain current and future max amount\n" +
                "            jch_future.seed_value_amt fut_seed_value_amt,\n" +
                "            jch_future.max_value_amt fut_max_value_amt,\n" +
                "            fnjps_get_jkptInstHeatIndexCfg(ji.jkptinstanceid_str, 'F') future_heat_index,\n" +
                "            -- Added 10/30/2017 current and future wtd resets and triggers\n" +
                "            fnjps_get_jpInstKydWtdConfig(ji.jkptinstanceid_str, 'F') as future_wtd_rst_trg\n" +
                "        FROM tbjps_jackpot j\n" +
                "        INNER JOIN tbjps_jackpot_instance ji ON ji.jackpot_id=j.jackpot_id\n" +
                "        INNER JOIN tbjps_jackpot_cycle jc ON jc.jkpt_instance_id=ji.jkpt_instance_id\n" +
                "        -- Added 10/30/2017 to obtain current and future max amount\n" +
                "        INNER JOIN tbjps_jkptinstance_config_hist jch ON jch.jkpt_instance_id=ji.jkpt_instance_id\n" +
                "        INNER JOIN\n" +
                "            (SELECT\n" +
                "            jkpt_instance_id,\n" +
                "            seed_value_amt,\n" +
                "            max_value_amt\n" +
                "            FROM tbjps_jkptinstance_config_hist\n" +
                "            WHERE current_row_ind='Y'\n" +
                "            AND enabled_ind='Y'\n" +
                "            ) jch_future ON jch_future.jkpt_instance_id=ji.jkpt_instance_id\n" +
                "        WHERE j.enabled_ind='Y'\n" +
                "          AND ji.enabled_ind='Y'\n" +
                "          AND jc.jkpt_cycle_status_code='ACTV'\n" +
                "          -- Added 10/30/2017 to obtain current and future max amount\n" +
                "          AND jch.current_jkpt_cycle_row_ind='Y'\n" +
                "          AND ji.jkptinstanceid_str IN ('"+jackpot_id+"');";
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        xrayReporter.addComment("Statment Executed");
        xrayReporter.addComment("Jackpot_ID = "+jackpot_id);
        if (rs.next()) {
            System.out.println(rs.getString(1));
            assertEquals(rs.getString("future_heat_index"), "{"+heat_idex_1+"00"+","+heat_idex_2+"00"+","+heat_idex_3+"00"+"}", "Matching the values from DB");
            xrayReporter.addComment("Matching the values from DB complete");
        }
    }
}
