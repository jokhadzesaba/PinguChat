package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatServer {
    public ServerSocket serverSocket;
    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public void startServer(){
        try {
            while (!serverSocket.isClosed()){
            Socket socket = serverSocket.accept();
            ClinetHandler clinetHandler = new ClinetHandler(socket);
            System.out.println(clinetHandler.getLocalTime() + ": " + clinetHandler.getUserName() + " has joined chat");
            System.out.println("Server is waiting on port 5000");
            Thread thread = new Thread(clinetHandler);
            thread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void closeServer(){
        try {
            if (serverSocket != null)
                serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public static void main(String[] args) throws IOException {
        System.out.println("Server is waiting on port 5000");
        ServerSocket serverSocket = new ServerSocket(5000);
        ChatServer server = new ChatServer(serverSocket);
        server.startServer();

    }
}
