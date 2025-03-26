package utilities;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {

    public static String getProperty(String key){

        Properties properties=new Properties();




        try {
            FileInputStream fis=new FileInputStream("C:\\Users\\EverBook\\IdeaProjects\\HBTaskAPI\\configuration.properties");

            properties.load(fis);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty(key);
    }



}
