package com.company;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Scanner;

public class ChatClient{
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;
    private LocalTime localTime;
    public ChatClient(Socket socket) {
        try {
            this.socket = socket;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            localTime = LocalTime.now();
        }catch (IOException e){
            close(socket, bufferedWriter, bufferedReader);
        }
    }
    public void startClient() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
        userName = scanner.nextLine();
        String stringBuilder = "1. Simply type message to send broadcast to all  active clients" + "\n" +
                "2. Type '@username<blank>message' without quotes to send message to desired client" + "\n" +
                "3. Type 'WHOIS' without quotes to see list of active clients" + "\n" +
                "4. Type 'LOGOUT' without quotes to log off from server" + "\n" +
                "5. Type 'PENGU' without quotes to request a random penguin fact" + "\n";
        System.out.println(stringBuilder);
        try {
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Read input from the server
        Thread thread = new Thread(() -> {
            String message;
            try {
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        while (true) {
            String message = scanner.nextLine();
            if (message.equals("LOGOUT")) {
                System.exit(0);
            }
            try {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void close(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (socket!=null)
                socket.close();
            if (bufferedReader!=null)
                bufferedReader.close();
            if (bufferedWriter!=null)
                bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();

        }
    }
    public String getUserName() {
        return userName;
    }
    public LocalTime getLocalTime() {
        return localTime;
    }
    public static void main(String[] args) {
        try {
        Socket socket = new Socket("localhost", 5000);
        ChatClient client = new ChatClient(socket);
        client.startClient();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
