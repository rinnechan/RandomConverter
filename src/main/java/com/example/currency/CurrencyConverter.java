package com.example.currency;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

public class CurrencyConverter {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String API_URL = dotenv.get("API_URL");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter from currency (e.g., USD): ");
            String from = scanner.nextLine().toUpperCase();

            System.out.print("Enter to currency (e.g., EUR): ");
            String to = scanner.nextLine().toUpperCase();

            if (amount <= 0) {
                System.err.println("Amount must be greater than zero.");
                return;
            }

            if (!from.equals("USD")) {
                String to2 = from;
                from = "USD";
                double tmp = convertCurrency(1, from, to2);
                double result = convertCurrency(amount / tmp, from, to);
                return;
            }

            double result = convertCurrency(amount, from, to);
            System.out.printf("%.2f %s = %.2f %s%n", amount, from, result, to);

        } catch (NumberFormatException e) {
            System.err.println("Invalid amount entered.");
        } catch (Exception e) {
            System.err.println("Error during currency conversion: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    public static double convertCurrency(double amount, String from, String to) throws Exception {
        String urlStr = API_URL +
                "?access_key=" + API_KEY +
                "&currencies=" + to +
                "&source=" + from +
                "&format=1";

        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseJson = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                responseJson.append(line);
            }

            in.close();

            JSONObject json = new JSONObject(responseJson.toString());
            System.out.println("API response: " + json.toString(2));

            if (!json.getBoolean("success")) {
                JSONObject error = json.getJSONObject("error");
                throw new RuntimeException("API error: " + error.getString("info"));
            }

            JSONObject quotes = json.getJSONObject("quotes");
            String quoteKey = from + to;

            if (!quotes.has(quoteKey)) {
                throw new RuntimeException("Exchange rate not found for: " + quoteKey);
            }

            double rate = quotes.getDouble(quoteKey);
            return amount * rate;

        } else {
            throw new RuntimeException("API call failed with HTTP code: " + responseCode);
        }
    }
}
