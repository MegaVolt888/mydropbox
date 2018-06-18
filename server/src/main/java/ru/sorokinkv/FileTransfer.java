package ru.sorokinkv;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static ru.sorokinkv.ServerConst.PORT;
import static ru.sorokinkv.ServerConst.SERVER_URL;

public class FileTransfer {
    public static void main(String[] args) {
        new ServerPart();
    }
}

class ServerPart {
    JTextArea area;

    ServerPart(){
        connectFileGetter();
   }

    public void connectFileGetter(){
        Runnable r = new Runnable() {
        int port = PORT;
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(port);
                    //area.append("Wait connect...");
                    System.out.println("Wait connect...");

                    while (true) {
                        Socket soket = ss.accept();

                        InputStream in = soket.getInputStream();
                        DataInputStream din = new DataInputStream(in);

                        int filesCount = din.readInt();//получаем количество файлов
                        //area.setText("Передается " + filesCount + " файлов\n");
                        System.out.println("Передается " + filesCount + " файлов\n");
                        for (int i = 0; i < filesCount; i++) {
                            System.out.println("Прием " + (i + 1) + "вого файла: \n");

                            long fileSize = din.readLong(); // получаем размер файла

                            String fileName = din.readUTF(); //прием имени файла
                            System.out.println("Имя файла: " + fileName + "\n");
                            System.out.println("Размер файла: " + fileSize + " байт\n");

                            byte[] buffer = new byte[64 * 1024];
                            FileOutputStream outF = new FileOutputStream(fileName);
                            int count, total  = 0;
                            int progress = 0;


                            while ((count = din.read(buffer, 0, (int) Math.min(buffer.length, fileSize - total))) != -1) {
                                total += count;

                                if(progress < (int)(total/(fileSize/100))) {
                                    progress = (int)(total/(fileSize/100));
                                    if (progress %5 ==0) {
                                        System.out.print(">" + progress);
                                    }
                                }
                                outF.write(buffer, 0, count);

                                if (total == fileSize) {
                                    break;
                                }
                            }
                            outF.flush();
                            outF.close();
                            System.out.println("\nФайл принят\n---------------------------------\n");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).setDaemon(true);
        new Thread(r).start();
    }
    }

/*    public static void main(String[] arg){
        new ServerPart();
    }*/
//}

/*
class ClientPart {

    JTextArea area;
    JTextField field;
    Socket socket;
    ArrayList<String> selectFiles;
    ClientPart(){

        JFrame f = new JFrame("Client");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 200);
        f.setLayout(new BorderLayout());
        f.setVisible(true);

        area = new JTextArea();
        field = new JTextField(20);
        final JButton selectBut = new JButton("Select");

        final JButton but = new JButton("Send");
        but.setEnabled(false);
        f.add(but, BorderLayout.SOUTH);
        f.add(area);
        f.add(selectBut, BorderLayout.NORTH);

        but.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                sendFiles(selectFiles);
            }
        });

        selectBut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                selectFiles = new ArrayList<String>();
                area.setText("");
                int returnVal = chooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION){
                    area.append("Выбранны файлы для передачи:\n" );
                    File[] file = chooser.getSelectedFiles();
                    for (File d : file){
                        selectFiles.add(d+"");
                        area.append(d+"\n");
                    }
                    but.setEnabled(true);
                }
            }
        });
    }



    private void sendFiles(ArrayList<String> list){
        //----------------------------------------------
        int port = PORT;
        String addres = SERVER_URL;
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(addres);
            socket = new Socket(ipAddress, port);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //----------------------------------------------
        int countFiles = list.size();

        DataOutputStream outD;
        try{
            outD = new DataOutputStream(socket.getOutputStream());

            outD.writeInt(countFiles);//отсылаем количество файлов

            for(int i = 0; i<countFiles; i++){
                File f = new File(list.get(i));

                outD.writeLong(f.length());//отсылаем размер файла
                outD.writeUTF(f.getName());//отсылаем имя файла

                FileInputStream in = new FileInputStream(f);
                byte [] buffer = new byte[64*1024];
                int count;

                while((count = in.read(buffer)) != -1){
                    outD.write(buffer, 0, count);
                }
                outD.flush();
                in.close();
            }
            socket.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new ClientPart();
    }
}*/