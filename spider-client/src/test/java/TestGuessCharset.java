import com.omartech.spiderClient.core.DefetcherUtils;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by omar on 16/1/21.
 */
public class TestGuessCharset extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestGuessCharset.class);

    @Test
    public void testGBK1() throws IOException {
        String s = DefetcherUtils.getResouce("gbk.html");
        Charset charset = DefetcherUtils.guess(s, DefetcherUtils.CHARSET);
        if(charset != null) {
            assertEquals("GBK", charset.displayName());
        }else{
            logger.error("null of charset");
        }



        String s2 = DefetcherUtils.getResouce("gbk2.html");
        Charset charset2 = DefetcherUtils.guess(s2, DefetcherUtils.CHARSET);
        assertEquals("GBK", charset2.displayName());

        String s3 = DefetcherUtils.getResouce("gbk3.html");
        Charset charset3 = DefetcherUtils.guess(s3, DefetcherUtils.CHARSET);
        if (charset3 != null) {
            assertEquals("GBK", charset3.displayName());
        } else {
            logger.error("charset isnull");
        }

    }

    @Test
    public void testRegex() {
        Pattern p = Pattern.compile("charset=[\"'](.*)[\"']");
        String s3 = DefetcherUtils.getResouce("gbk3.html");
        Matcher matcher = p.matcher(s3);
        if (matcher.find()) {
            logger.info(matcher.group());
            logger.info(matcher.group(1));
        }
    }
}
