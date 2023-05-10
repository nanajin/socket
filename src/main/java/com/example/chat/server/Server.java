package com.example.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.chat.client.Client;
import com.example.chat.client.ClientApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class Server implements Runnable {
    private int portNumber;
    private ServerSocket socket;
    private ArrayList<Socket> clients;
    public ArrayList<ClientThread> clientThreads;

//    private ArrayList<ClientThread> clientThreads;
    public ObservableList<String> serverLog;
    public ObservableList<String> clientNames;
    Server(){};

    // 추가
    public static ObservableList<String> list;
    public static class MyListener implements ListChangeListener<String> {
        @Override
        public void onChanged(Change<? extends String> change) {
            System.out.println("server change.getlist = " + change.getList());
            list = (ObservableList<String>) change.getList();
        }
    }
    //
    public Server(int portNumber) throws IOException {
        this.portNumber = portNumber;
        serverLog = FXCollections.observableArrayList();
        clientNames = FXCollections.observableArrayList();
        clients = new ArrayList<Socket>();
        clientThreads = new ArrayList<ClientThread>();
        socket = new ServerSocket(portNumber);
    }

    public void startServer() {

        try {
            socket = new ServerSocket(this.portNumber); /*
             * Instantiates the
             * server socket so
             * clients can connect
             */
            serverLog = FXCollections.observableArrayList();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            /* Infinite loop to accept any incoming connection requests */
            while (true) {
                /* Add to log that the server's listening */

                Platform.runLater(() -> serverLog.add("Listening for client"));

                final Socket clientSocket = socket.accept();

                /* Add the incoming socket connection to the list of clients */
                clients.add(clientSocket);
                /* Add to log that a client connected */
                Platform.runLater(() -> {
                    {
                        serverLog.add("Client "
                                + clientSocket.getRemoteSocketAddress()
                                + " connected");
                    }
                });
                ClientThread clientThreadHolderClass = new ClientThread(
                        clientSocket, this);
                Thread clientThread = new Thread(clientThreadHolderClass);
                clientThreads.add(clientThreadHolderClass);
                clientThread.setDaemon(true);
                clientThread.start();
                ServerApplication.threads.add(clientThread);
                System.out.println("85번줄: "+ clientThreads);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void clientDisconnected(ClientThread client, Boolean b) {//b는 강퇴(false)여부
        System.out.println("Server.java에서 clientThread: "+clientThreads);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                serverLog.add("Client "
                        + client.getClientSocket().getRemoteSocketAddress()
                        + " disconnected");
                clients.remove(clientThreads.indexOf(client));
                clientNames.remove(clientThreads.indexOf(client));
                clientThreads.remove(clientThreads.indexOf(client));

                if(b){
                    writeToAllSockets(client.getClientName()+"님께서 나가셨습니다", false);
                }
                else{
                    System.out.println("Server.java outperson: "+client.getClientName());
                    client.writeToServer("강퇴당하셨습니다", client);
                    writeToAllSockets(client.getClientName()+"님께서 강퇴당하셨습니다", false);
//                    new Client().stopClient(client.getClientSocket()); // null오지게뜸
                }

                // 추가
//                client.writeToServer("close", null);
//                new Client().stopClient(client.getClientSocket()); // null오지게뜸

            }
        });
    }
    public void writeToAllSockets(String input, Boolean time) {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat s = new SimpleDateFormat("[a hh:mm] ");

        for (ClientThread clientThread : clientThreads) {
            if(list!=null && list.contains(clientThread.getClientName())) {
                System.out.println("뮤트");
            }
            else{
                if(time){
                    clientThread.writeToServer(s.format(now) + input, clientThread);
                }
                else{
                    clientThread.writeToServer(input, clientThread);
                }
            }
        }
    }
}
