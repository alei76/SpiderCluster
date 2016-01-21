package com.omartech.spiderClient.core;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Date: 2/23/14
 * Time: 9:53 AM
 */
public class DefetcherUtils {

    public static String getResouce(String name) {
        try (InputStream is = DefetcherUtils.class.getClassLoader().getResourceAsStream(name)) {
            return IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36";
    private static Logger logger = LoggerFactory.getLogger(DefetcherUtils.class);

    public static final String CHARSET = "charset=";
    public static final Charset ASCII = Charset.forName("US-ASCII");
    public static final Charset UTF_8 = Charset.forName("utf8");
    public static final Charset GBK = Charset.forName("gbk");

    public static Charset parseCharset(String type) {
        if (type != null) {
            try {
                type = type.toLowerCase();
                int i = type.indexOf(CHARSET);
                if (i != -1) {
                    String charset = type.substring(i + CHARSET.length()).trim();
                    return Charset.forName(fixCharset(charset));
                }
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    private final static String fixCharset(String s) {
        if (s.equalsIgnoreCase("gb2312")) {
            return "gbk";
        }
        return s;
    }

    public static Charset guess(String html, String patten) {
        int idx = html.indexOf(patten);
        if (idx != -1) {
            int start = idx + patten.length();
            int end = html.indexOf('"', start);
            if (end == -1) {
                end = html.indexOf("'", start);
            }
            if (end != -1) {
                try {
                    String charset = html.substring(start, end);
                    return Charset.forName(fixCharset(charset));
                } catch (Exception ignore) {
                }
            }
        }
        return null;
    }

    public static Charset detectCharset(HttpResponse resp, byte[] body) {
        Header header = resp.getFirstHeader("content-type");
        if (header != null) {
            Charset c = parseCharset(header.getValue());
            if (c != null) {
                return c;
            }
        }
        String s = new String(body, 0, body.length, ASCII);//
        Charset c = guess(s, CHARSET);
        Charset charset = c == null ? UTF_8 : c;
        logger.info("guess result: {}, charset : {}", c, charset);
        return charset;
    }

    public static String toString(HttpResponse resp) throws IOException {
        byte[] bytes = EntityUtils.toByteArray(resp.getEntity());
        return new String(bytes, 0, bytes.length, detectCharset(resp, bytes));
    }

    public static String toString(HttpResponse resp, String contentType) throws IOException {
        byte[] bytes = EntityUtils.toByteArray(resp.getEntity());
        Charset charset = UTF_8;
        switch (contentType) {
            case "UTF_8":
                charset = UTF_8;
                break;
            case "GB2312":
                charset = GBK;
                break;
            case "GBK":
                charset = GBK;
                break;
            default:
                break;
        }
        return new String(bytes, 0, bytes.length, charset);
    }


    public static String assemble(URI uri, Map<String, String> p) {
        StringBuilder sb = new StringBuilder();
        sb.append(uri.getScheme()).append("://").append(uri.getHost());
        if (uri.getPath() != null) {
            sb.append(uri.getPath());
        }

        sb.append("?");
        for (Map.Entry<String, String> param : p.entrySet()) {
            try {
                sb.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(), "utf8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            sb.append("&");
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
    }


    private static boolean isWhitespace(char c) {
        return Character.isWhitespace(c) || c == 160; // &nbsp;
    }

    public static String getLocation(HttpResponse resp) {
        Header location = resp.getFirstHeader("Location");
        if (location != null) {
            return location.getValue();
        } else {
            return "";
        }
    }

    private static final String[] IGNORE_TAGS = new String[]{"script", "style", "link", "#comment", "h2", "h1"};

}
