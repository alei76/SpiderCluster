import com.omartech.spiderServer.handler.RequestHandler;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by OmarTech on 15-10-17.
 */
public class TestNumber extends TestCase {
    @Test
    public void testGenerate() {

        int num = 1;
        int length = 5;
        String format = RequestHandler.transferNum(num, length);
        assertEquals("00001", format);
    }
}
