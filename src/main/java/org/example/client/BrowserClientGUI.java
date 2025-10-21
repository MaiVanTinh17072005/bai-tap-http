package org.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class BrowserClientGUI extends JFrame {
    private JTextField urlField;
    private JComboBox<String> methodBox;
    private JTextArea resultArea;

    public BrowserClientGUI() {
        setTitle("Simple HTTP Browser");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        urlField = new JTextField("http://localhost/bt%20Http/index.html", 30);
        methodBox = new JComboBox<>(new String[]{"GET", "POST", "HEAD"});
        JButton okButton = new JButton("OK");

        topPanel.add(urlField);
        topPanel.add(methodBox);
        topPanel.add(okButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(resultArea);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        okButton.addActionListener(e -> sendRequest());
    }

    private void sendRequest() {
        try {
            String urlStr = urlField.getText();
            String method = (String) methodBox.getSelectedItem();

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            int code = conn.getResponseCode();
            resultArea.setText("Response Code: " + code + "\n");

            if (method.equals("HEAD")) {
                resultArea.append("=== HEADER ===\n");
                conn.getHeaderFields().forEach((k, v) -> resultArea.append(k + ": " + v + "\n"));
            } else {
                resultArea.append("=== BODY ===\n");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    resultArea.append(line + "\n");
                }
                in.close();
            }
        } catch (Exception ex) {
            resultArea.setText("Lỗi: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BrowserClientGUI().setVisible(true));
    }
}

