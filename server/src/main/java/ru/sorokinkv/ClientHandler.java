package ru.sorokinkv;

import com.sun.deploy.util.Waiter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private int id;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        System.out.println("Login page " + this + ": " + msg);

                        if (msg.startsWith("/auth ")) {
                            String[] tokensA = msg.split(" ");
                            System.out.println(tokensA);
                            if (tokensA.length < 3) {
                                this.sendMsg("/error@ Все поля должны быть заполнены");
                            }
                            if (tokensA.length == 3) {
                                String nick = SQLHandlerDB.getNickByLoginPass(tokensA[1], tokensA[2]);
                                if (nick != null) {
                                    if (server.isNickBusy(nick)) {
                                        out.writeUTF("/error@ Учетная запись уже используется на другом устройстве");
                                        continue;
                                    }

                                    out.writeUTF("/authok@ " + nick);
                                    this.nick = nick;
                                    this.id = SQLHandlerDB.getIdByNick(nick);
                                    server.subscribe(this);

                                    break;
                                } else {
                                    out.writeUTF("/error@ Неверный логин/пароль");
                                }
                            }
                            if (tokensA.length > 3) {
                                out.writeUTF("/error@ Запрещено в логине/пароле использовать пробелы");
                            }
                        }
                            if (msg.startsWith("/reg ")) {

                                String[] tokensR = msg.split(" ");
                                System.out.println("Длинна: " + tokensR.length);
                                System.out.println(msg);

                                // ToDo: add DB injection filter

                                if (tokensR.length < 4) {
                                    out.writeUTF("/error@ Все поля должны быть заполнены");
                                }

                                if (tokensR.length == 4) {
                                    boolean loginInBase = SQLHandlerDB.searchLoginUser(tokensR[2]);
                                    System.out.println(tokensR[2]);
                                    if (loginInBase) {
                                        System.out.println(tokensR[1] + " " + tokensR[2] + " " + tokensR[3]);
                                        this.sendMsg(String.format("/error@ Логин %s уже используется, придумайте другой", tokensR[2]));
                                        System.out.println(String.format("Логин %s уже используется, придумайте другой", tokensR[2]));
                                        continue;
                                    } else {
                                        SQLHandlerDB.addNewUser(tokensR[2], tokensR[3], tokensR[1]); //add to DB
                                        String name = tokensR[1];
                                        this.sendMsg("/regok@ " + "Пльзователь " + name + " зарегистрирован.");
                                        System.out.println("RegOK " + name);
                                        System.out.println(SQLHandlerDB.getAllUserInfo());
                                    }
                                    break;


                                }

                                if (tokensR.length > 4) {
                                    out.writeUTF("/error@ Запрещено в нике/логине/пароле использовать пробелы");
                                }
                                this.sendMsg(msg);

                            }


                    }
                    while (true) {
                        String msg = in.readUTF();
                        this.sendMsg(this +": "+ msg);
                        if (msg.startsWith("/exit")) {
                            this.sendMsg("/kick@ ");
                        }
                       //  server.broadcastMsg(this, msg);
                        }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }

    public int getId() {
        return id;
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
