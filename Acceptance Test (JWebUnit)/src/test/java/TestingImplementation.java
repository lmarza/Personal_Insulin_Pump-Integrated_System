
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestingImplementation {

    private WebDriver driver;

    @BeforeClass
    public static void setupWebdriverChromeDriver() {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver");
    }

    @Before
    public void setup() {
        driver = new ChromeDriver();
    }

    @After
    public void teardown() {
        if (driver != null) {
            //driver.quit();
        }
    }

    @Test
    public void testWebInterface() throws InterruptedException {
        driver.get("http://localhost:8080/");
        Thread.sleep(5000);
        assertThat(driver.getTitle(), containsString("Insulin pump device"));

        String firstState = driver.findElement(By.id("state")).getText();
        String measurement = driver.findElement(By.id("r2")).getText();
        Thread.sleep(10000);
        String secondState = driver.findElement(By.id("state")).getText();
        String measurement2 = driver.findElement(By.id("r2")).getText();

        System.out.println(firstState + " " + secondState);
        System.out.println(measurement + " " +  measurement2);


        if (firstState.equalsIgnoreCase(secondState) && firstState.toLowerCase().contains("running"))
            Assert.assertNotEquals(measurement2, measurement);
        else if (firstState.equalsIgnoreCase(secondState) && firstState.toLowerCase().contains("hardware issue"))
            Assert.assertEquals(measurement2, measurement);
        // if the states are not equals for example first state running and second hardware issue the values should be the same
        else
            Assert.assertEquals(measurement2, measurement);

    }
}