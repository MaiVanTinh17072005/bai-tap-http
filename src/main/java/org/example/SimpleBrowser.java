package org.example;

import java.io.*;
import java.net.*;
import java.util.regex.*;

public class SimpleBrowser {
    public static void main(String[] args) throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Nhập URL: ");
        String urlString = console.readLine();

        System.out.print("Nhập phương thức (GET/POST/HEAD): ");
        String method = console.readLine().toUpperCase();

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);

        System.out.println("Response Code: " + conn.getResponseCode());
        System.out.println("Response Message: " + conn.getResponseMessage());

        if (method.equals("HEAD")) {
            System.out.println("\n=== HEADER INFO ===");
            conn.getHeaderFields().forEach((k, v) -> System.out.println(k + ": " + v));
        } else {
            System.out.println("\n=== HTML CONTENT ===");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder html = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }
            in.close();
            System.out.println(html);

            // Đếm số lượng thẻ HTML
            String htmlStr = html.toString();
            countTag(htmlStr, "p");
            countTag(htmlStr, "div");
            countTag(htmlStr, "span");
            countTag(htmlStr, "img");
        }
    }

    public static void countTag(String html, String tag) {
        Matcher m = Pattern.compile("<" + tag + "\\b", Pattern.CASE_INSENSITIVE).matcher(html);
        int count = 0;
        while (m.find()) count++;
        System.out.println("Số lượng <" + tag + "> : " + count);
    }
}
