package MyFileTransfer;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class ClientPart {

    Socket socket;
 //   ArrayList<String> selectFiles;

    public void sendFiles(String addres,int port, ArrayList<String> list){
        //----------------------------------------------
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

}