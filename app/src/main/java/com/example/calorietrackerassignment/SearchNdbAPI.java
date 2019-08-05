package com.example.calorietrackerassignment;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SearchNdbAPI {
    public static final String API_KEY = "3TbLWl0w6cySClUVbNIkQbd8dhKWxULuHrOtUxoy";

    public static final String NDB_SEARCH_URL = "https://api.nal.usda.gov/ndb/search/?format=json&max=1&offset=0&ds=Standard Reference&api_key="+API_KEY+"&q=";
    public static final String NDB_NUTRIENTS_URL = "http://api.nal.usda.gov/ndb/nutrients/?format=json&api_key="+API_KEY+"&nutrients=204&nutrients=268&ndbno=";

    public static String searchFoodFromNDB(String keyword) {

        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query_parameter = "";

        try {
            url = new URL(NDB_SEARCH_URL+ keyword );
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult;
    }


    public static String searchFoodNutrientsFromNDB(String keyword) {

        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query_parameter = "";

        try {
            url = new URL(NDB_NUTRIENTS_URL+ keyword );
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult;
    }
}
