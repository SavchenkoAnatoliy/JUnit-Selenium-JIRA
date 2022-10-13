import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseClass {
    public WebDriver driver;
    public static ThreadLocal<WebDriver> tdriver = new ThreadLocal<WebDriver>();

    public WebDriver inizialize_driver(){
        System.setProperty("webdriver.chrome.driver","C:\\Users\\a.savchenko\\Desktop\\SIINFO\\RINGMASTER\\chromedriver\\chromedriver.exe");
//        WebDriverManager.firefoxdriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1050, 718));
        tdriver.set(driver);
        return getDriver();
    }

    public static synchronized WebDriver getDriver(){
        return tdriver.get();
    }
}
