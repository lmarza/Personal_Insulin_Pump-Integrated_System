
import net.sourceforge.jwebunit.junit.WebTester;
import org.junit.Before;
import org.junit.Test;
//import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class LD_InsulinPumpTest {

    private WebTester tester;


    @Before
    public void prepare(){
        tester = new WebTester();
        tester.setBaseUrl("http://localhost:8080/");
    }

    @Test
    public void testInitialView(){
        tester.setScriptingEnabled(false);
        tester.beginAt("/");
        tester.assertTextPresent("Personal insulin pump device");
    }

}
