package utilities.config;

import java.util.Properties;

/**
 * Container fuer Konfigurationsparameter aus der Konfigurationsdatei -
 * Singleton
 */
public class Configuration {

	private static final Configuration instance = new Configuration();

	private static final String PROPERTIES_FILE_NAME = "config.properties";

	private Properties p;

	/**
	 * Default-Konstruktor - private wegen Singleton - kann nicht außerhalb der
	 * Klasse aufgerufen werden
	 */
	private Configuration() {
		p = loadProperties(PROPERTIES_FILE_NAME);
	}

	/**
	 * Statische Methode, welche die einzige exitstierende Instanz der Klasse
	 * zurückliefert
	 * 
	 * @return instance - einzige Instanz der Klasse
	 */
	public static Configuration getInstance() {
		return instance;
	}

	/**
	 * Laedt die Konfigurationsdatei
	 * 
	 * @param propertiesFileName
	 *            - Name der Konfigurationsdatei
	 * @return Properties-Objekt
	 */
	private Properties loadProperties(String propertiesFileName) {
		Properties properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream(propertiesFileName));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(String.format("Die Konfigurationsdatei %s konnte nicht geladen werden!", propertiesFileName));
			System.exit(0);
		}
		return properties;
	}

	public Properties getProperty() {
		return p;
	}
}