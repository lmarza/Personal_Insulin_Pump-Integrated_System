
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestingImplementation {

    private WebDriver driver;

    @BeforeClass
    public static void setupWebdriverChromeDriver() {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver");
        //System.setProperty("webdriver.opera.driver", System.getProperty("user.dir") + "/src/test/resources/operaDriver.exe");
    }

    @Before
    public void setup() {
        driver = new ChromeDriver();
        //driver = new OperaDriver();
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testWebInterface() throws InterruptedException
    {
        driver.get("http://localhost:8080/");

        assertThat(driver.getTitle(), containsString("Insulin pump device"));
        assertThat(driver.getPageSource(), containsString("Personal insulin pump device"));

        Thread.sleep(5000);
        Assert.assertEquals(driver.findElement(By.id("compDose")).getText(), "0 unit(s)");
        String beforeState = driver.findElement(By.id("state")).getText();
        String beforeR2 = driver.findElement(By.id("r2")).getText();

        Thread.sleep(10000);

        String afterState = driver.findElement(By.id("state")).getText();
        String afterR2 = driver.findElement(By.id("r2")).getText();

        System.out.println(beforeState + " " + afterState);
        System.out.println(beforeR2 + " " +  afterR2);


        if (beforeState.equalsIgnoreCase(afterState) && beforeState.toLowerCase().contains("running"))
            Assert.assertNotEquals(afterR2, beforeR2);
        else if (beforeState.equalsIgnoreCase(afterState) && beforeState.toLowerCase().contains("hardware issue"))
            Assert.assertEquals(afterR2, beforeR2);
        // if the states are not equals (for example first state 'running' and second 'hardware issue') the values should be the same
        else
            Assert.assertEquals(afterR2, beforeR2);
    }

    @Test
    public void testRebootButton()
    {
        driver.get("http://localhost:8080/");

        //seconds are required by WebDriverWait, so 1000 seconds are just a way to say 'don't throw an exception for a long time'
        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button")));
        driver.findElement(By.className("button")).click();

        assertThat(driver.findElement(By.id("state")).getText().toLowerCase(), containsString("booting device..."));
        assertThat(driver.findElement(By.id("r2")).getText().toLowerCase(), containsString("booting device..."));
    }



}