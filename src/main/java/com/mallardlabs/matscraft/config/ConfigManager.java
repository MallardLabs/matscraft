package com.mallardlabs.matscraft.config;

public class ConfigManager {
    /*
     * Update this with your postgress confirguration
     */
    public static String PG_URL = "";
    public static  String PG_USER = "";
    public static String PG_PW = "";
    /*
     * Update the database every 100 block breaks
     * Default value is 100.
     */
    public static Integer BLOCK_BREAK_BATCH = 100;
}