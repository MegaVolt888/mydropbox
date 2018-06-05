package ru.sorokinkv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import static ru.sorokinkv.ServerConst.*;

public class Server {
    private ServerSocket serverSocket;

    private Vector<ClientHandler> clients;

    public Server() {
        try {
            SQLHandlerDB.connect();
            serverSocket = new ServerSocket(PORT);
            clients = new Vector<>();
            System.out.println("Сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                System.out.println("Connected "+this);
                new ClientHandler(this, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SQLHandlerDB.disconnect();
        }
    }


    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
     }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }


    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }
}
