package com.company;
import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClinetHandler implements Runnable {
    private static ArrayList<ClinetHandler> clients = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;
    private LocalTime localTime;

    public ClinetHandler(Socket socket) {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = bufferedReader.readLine();
            clients.add(this);
            sendMessage(LocalTime.now() + ": " + userName + " entered chat");
            this.localTime = LocalTime.now();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Thread thread = new Thread(() -> {
            String message;
            while (socket.isConnected()) {
                try {
                message = bufferedReader.readLine();
                if ("WHOIS".equals(message)) {
                    // Send the list of connected users to the client
                    List<ClinetHandler> clients = ClinetHandler.clients;
                    StringBuilder sb = new StringBuilder();
                    sb.append("List of user connected at: ").append(LocalTime.now()).append("\n");
                    for (ClinetHandler client : clients) {
                        sb.append(client.userName).append(" since: ").append(client.getLocalTime()).append("\n");
                    }
                    bufferedWriter.write(sb.toString());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }else if("PINGU".equals(message)){
                    Random random = new Random();
                    int randomNumber = random.nextInt(pinguFacts().size());
                    bufferedWriter.write((pinguFacts().get(randomNumber)));
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }else if (message.startsWith("@")){
                    String name = message.split(" ")[0].substring(1);
                    ClinetHandler targetClient = null;
                    for (ClinetHandler c : clients) {
                        if (c.getUserName().equals(name)) {
                            targetClient = c;
                            break;
                        }
                    }
                    if (targetClient != null) {
                        targetClient.bufferedWriter.write(getUserName() + ": " + message.substring(name.length() + 2));
                        targetClient.bufferedWriter.newLine();
                        targetClient.bufferedWriter.flush();
                    }
                }else {
                    sendMessage(getUserName()+": " + message);
                }
            } catch (IOException e) {
                close(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    });
    thread.start();
}
private void sendMessage(String message) {
        for (ClinetHandler clinetHandler: clients) {
            try {
            if (!clinetHandler.userName.equals(userName)){
                clinetHandler.bufferedWriter.write(message);
                clinetHandler.bufferedWriter.newLine();
                clinetHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                close(socket,bufferedWriter,bufferedReader);
            }
        }
    }
    private void close(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        clients.remove(this);
        sendMessage(LocalTime.now() +": " +getUserName() + " has left the Chat!!!");
        try {
            if (bufferedReader!=null)
                bufferedReader.close();
            if (bufferedWriter!=null)
                bufferedWriter.close();
            if (socket!=null)
                socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static ArrayList<String> pinguFacts(){
    ArrayList<String> facts = new ArrayList<>();
        facts.add("The world’s smallest penguin stands just over 30cm high");
        facts.add("Penguins’ black and white ‘tuxedo’ helps them avoid predators");
        facts.add("Adelie penguins have extremely complicated love lives");
        facts.add("Penguin poo is visible from space. ngl idk if this is really true");
        facts.add("Giant penguins once roamed the planet");
        facts.add("Many male penguins gift female penguins with rocks in order to woo them.");
        facts.add("According to some animal experts, the penguin is one of the most streamlined animals in the world.");
        return facts;
    }
    public static List<ClinetHandler> getClients() {
        return clients;
    }
    public String getUserName() {
        return userName;
    }
    public LocalTime getLocalTime() {
        return localTime;
    }

}
