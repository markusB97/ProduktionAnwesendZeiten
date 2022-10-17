package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class ConfigService {
    private static final String CONFIG = "zeiten.cfg";

    public static String readFromConfig(String key) throws IOException {
        Properties prop = new Properties();
        InputStream is = new FileInputStream(CONFIG);
        prop.load(new InputStreamReader(is, Charset.forName("UTF-8")));
        return prop.getProperty(key);
    }
}