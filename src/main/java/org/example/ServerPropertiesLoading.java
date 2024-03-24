package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerPropertiesLoading {
    private String host;
    private int port;
    private String nameSettingsFile = "/settings.txt";


    public ServerPropertiesLoading() {
        loadSettings();
    }

    public void loadSettings() {
        Properties prop = new Properties();

        try (InputStream input = ServerPropertiesLoading.class.getResourceAsStream(nameSettingsFile)) {

            if (input == null) {
                throw new IOException("Файл настроек не найден: " + nameSettingsFile);
            }

            prop.load(input);

            this.host = prop.getProperty("host", "localhost");
            this.port = Integer.parseInt(prop.getProperty("serverPort", "8080"));

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке файла настроек: " + e.getMessage());
        }
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getNameSettingsFile() {
        return nameSettingsFile;
    }
}
