import com.omartech.spiderClient.core.DefetcherUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by omar on 16/1/21.
 */
public class TestGuessCharset {

    private static Logger logger = LoggerFactory.getLogger(TestGuessCharset.class);

    public static void main(String[] args) throws IOException {
        String s = DefetcherUtils.getResouce("gbk.html");
        Charset charset = DefetcherUtils.guess(s, DefetcherUtils.CHARSET);
        logger.info("guess result: {}, charset : {}", charset, charset);
        System.out.println(charset);


        String s2 = DefetcherUtils.getResouce("gbk2.html");
        Charset charset2 = DefetcherUtils.guess(s2, DefetcherUtils.CHARSET);
        logger.info("guess result: {}, charset : {}", charset2, charset2);
        System.out.println(charset2);

    }
}
