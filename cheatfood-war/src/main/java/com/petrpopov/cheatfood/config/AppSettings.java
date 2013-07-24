package com.petrpopov.cheatfood.config;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * User: petrpopov
 * Date: 28.01.13
 * Time: 13:48
 */

public class AppSettings {

    public void loadConfig(String fileConfigPath)
    {
        log().info("Loading config");

        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(fileConfigPath));

            MONGODB_HOST = properties.getProperty("mongodb_host");
            MONGODB_DB = properties.getProperty("mongodb_db");

            FOURSQUARE_CLIENT_ID = properties.getProperty("foursquare_client_id");
            FOURSQUARE_CLIENT_SECRET = properties.getProperty("foursquare_client_secret");
            FOURSQUARE_CALLBACK_URL = properties.getProperty("foursquare_callback_url");

            FACEBOOK_CLIENT_ID = properties.getProperty("facebook_client_id");
            FACEBOOK_CLIENT_SECRET = properties.getProperty("facebook_client_secret");
            FACEBOOK_CALLBACK_URL = properties.getProperty("facebook_callback_url");

            TWITTER_CLIENT_ID = properties.getProperty("twitter_client_id");
            TWITTER_CLIENT_SECRET = properties.getProperty("twitter_client_secret");
            TWITTER_CALLBACK_URL = properties.getProperty("twitter_callback_url");

            VKONTAKTE_CLIENT_ID = properties.getProperty("vkontakte_client_id");
            VKONTAKTE_CLIENT_SECRET = properties.getProperty("vkontakte_client_secret");
            VKONTAKTE_CALLBACK_URL = properties.getProperty("vkontakte_callback_url");

            log().info("Config loaded successfully ! :)");
        } catch (FileNotFoundException e) {
            log().error("Config file not found !");
            e.printStackTrace();
        } catch (IOException e) {
            log().error("Config file has IO problems !");
            e.printStackTrace();
        }

    }

    private Logger log()
    {
        return Logger.getLogger(AppSettings.class);
    }

    public static String MONGODB_HOST;
    public static String MONGODB_DB;
    public static String FOURSQUARE_CLIENT_ID;
    public static String FOURSQUARE_CLIENT_SECRET;
    public static String FOURSQUARE_CALLBACK_URL;
    public static String FACEBOOK_CLIENT_ID;
    public static String FACEBOOK_CLIENT_SECRET;
    public static String FACEBOOK_CALLBACK_URL;
    public static String TWITTER_CLIENT_ID;
    public static String TWITTER_CLIENT_SECRET;
    public static String TWITTER_CALLBACK_URL;
    public static String VKONTAKTE_CLIENT_ID;
    public static String VKONTAKTE_CLIENT_SECRET;
    public static String VKONTAKTE_CALLBACK_URL;
}
