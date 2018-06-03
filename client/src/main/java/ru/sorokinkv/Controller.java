package ru.sorokinkv;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static ru.sorokinkv.ClientConst.*;

public class Controller implements Initializable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int authorized = 0;
    private String nick;
    private ObservableList<String> clientsList;

    @FXML
    TextField msgField;

    @FXML
    TextArea mainTextArea;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passField;

    @FXML
    TextField regNickField;

    @FXML
    TextField regLoginField;

    @FXML
    TextField regPassField;

    @FXML
    HBox registerPanel, authPanel, msgPanel;

 /*   @FXML
    VBox registerPanel;*/

 /*   @FXML
    ListView<String> clientsView;*/

    public void setAuthorized(int authorized) {
        this.authorized = authorized;
        if (this.authorized == 2) {
            registerPanel.setVisible(true);
            registerPanel.setManaged(true);
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
            //     clientsView.setVisible(true);
            //     clientsView.setManaged(true);
        }
        if (this.authorized == 1) {
            registerPanel.setVisible(false);
            registerPanel.setManaged(false);
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);
       //     clientsView.setVisible(true);
       //     clientsView.setManaged(true);
        }
        if (this.authorized ==0 ) {
            registerPanel.setVisible(false);
            registerPanel.setManaged(false);
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
       //     clientsView.setVisible(false);
       //     clientsView.setManaged(false);
            nick = "";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(0);
     //   clientsList = FXCollections.observableArrayList();
     //   clientsView.setItems(clientsList);

    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCustomMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket(SERVER_URL, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(new Runnable() {

                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            System.out.println("One "+str);

                            if (str.startsWith("/error@ ")) {
                                String error = str.split("@ ")[1];
                                showAlert(error);
                          //      setAuthorized(2);
                                // sendCustomMsg("/history");
                                break;
                            }

                            if (str.startsWith("/authok")) {
                                //nick =  tokens[2];
                                setAuthorized(1);
                               // sendCustomMsg("/history");
                                break;
                            }
                            if (str.startsWith("/regok")) {
                                String info = str.split("@ ")[1];
                                showAlert(info);
                                System.out.println("RegOK" + nick);

                                setAuthorized(0);
                                // sendCustomMsg("/history");
                                break;
                            }
                            System.out.println("Two "+str);
                            mainTextArea.appendText(str );
                           mainTextArea.appendText(" \n");
                        }
                     /*   while (true) {
                            String str = in.readUTF();
                            System.out.println(str);
                            mainTextArea.appendText(str);
                            mainTextArea.appendText("\n");
                        }*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                      //  showAlert("Произошло отключение от сервера");
                        setAuthorized(0);
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
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            if(loginField.getText()=="" || passField.getText() == ""){
                showAlert("Все поля должны быть заполнены");
            }
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Невозможно отправить данные, проверьте сетевое соединение...");
        }
    }

    public void sendNewUser(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {

                String nick = regNickField.getText();
                out.writeUTF("/reg " + regNickField.getText() + " " + regLoginField.getText() + " " + regPassField.getText());
                System.out.println(regNickField.getText() + " " + regLoginField.getText() + " " + regPassField.getText());
               // regNickField.clear();
               // regLoginField.clear();
               // regPassField.clear();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Связь с сервером потеряна, проверьте сетевое соединение...");
        }
    }
    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public void startReg(ActionEvent actionEvent) {

        setAuthorized(2);
    }



    public void cancelReg(ActionEvent actionEvent) {
        setAuthorized(0);
    }
}

  /*  public void clickClientsList(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2) {
            String str = clientsView.getSelectionModel().getSelectedItem();
            msgField.setText("/w " + str + " ");
            msgField.requestFocus();
            msgField.selectEnd();
        }
    }*/
//}
