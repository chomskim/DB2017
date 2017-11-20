package hufs.ces.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Config  {

    private static Config confInstance = null;
    private Properties prop = null;

    private Config() {

        load(getConfLocation());
    }

    public synchronized static Config getInstance() {
        if (null == confInstance) {  
            confInstance = new Config();
        }
        return confInstance;
    }

    public synchronized void load(String confFileName) {

        try {
            prop = new Properties();
            
            FileInputStream propIn = new FileInputStream(confFileName);
            prop.load(new BufferedInputStream(propIn));
            propIn.close();
        } catch (Exception ex) {
            System.err.println(ex+"-- Fail to load property file.");
        }
    }

    public String getProperty(String propKey){
        return prop.getProperty(propKey);
    }
    
 	private static String getConfLocation() {
		File confFile = 
			new File(Thread.currentThread().getContextClassLoader()
					.getResource("config/dbconn.properties").getFile());

		return confFile.getAbsolutePath();
	}
 	
}