package org.example.server;

import java.io.*;
import java.net.*;
import java.util.regex.*;

public class SimpleHTTPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server đang chạy tại cổng 8080...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleRequest(socket)).start();
        }
    }

    private static void handleRequest(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Đọc dòng đầu tiên: "GET / HTTP/1.1"
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;
            System.out.println("Yêu cầu: " + requestLine);

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // Đọc phần header (bỏ qua)
            while (in.ready()) {
                String header = in.readLine();
                if (header == null || header.isEmpty()) break;
            }

            File file = new File("www/test.html");

            if (!file.exists()) {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n");
                out.flush();
                socket.close();
                return;
            }

            if (method.equals("HEAD")) {
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: text/html\r\n");
                out.write("Content-Length: " + file.length() + "\r\n\r\n");
                out.flush();
            } else if (method.equals("GET") || method.equals("POST")) {
                String html = new String(java.nio.file.Files.readAllBytes(file.toPath()));

                // Đếm số thẻ
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: text/html\r\n\r\n");
                out.write(html);
                out.write("\n\n<!-- THỐNG KÊ THẺ -->\n");
                out.write("Số lượng <p>: " + countTag(html, "p") + "<br>");
                out.write("Số lượng <div>: " + countTag(html, "div") + "<br>");
                out.write("Số lượng <span>: " + countTag(html, "span") + "<br>");
                out.write("Số lượng <img>: " + countTag(html, "img") + "<br>");
                out.flush();
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int countTag(String html, String tag) {
        Matcher m = Pattern.compile("<" + tag + "\\b", Pattern.CASE_INSENSITIVE).matcher(html);
        int count = 0;
        while (m.find()) count++;
        return count;
    }
}
