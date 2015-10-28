package net.frebib.sscdatabase;

import java.io.IOException;
import java.util.Properties;

public class DBConfig {
    private Properties props;

    private DBConfig(Properties props) {
        if (props == null)
            throw new NullPointerException("Properties file is null");
        this.props = props;
    }
    public static DBConfig load(String path) throws IOException {
        return new DBConfig(IOHelper.loadProperties(path));
    }

    public String getProp(String key) {
        String val = props.getProperty(key);
        if (val == null)
            throw new IllegalArgumentException("A property for '" + key + "' was not found");
        return val;
    }
}
