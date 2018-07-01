package MyFileTransfer;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPart {
//   JTextArea area;
//    int port;
 //   String personalFolder = "user2";

      public ServerPart(){}
      /*
              int port, String personalFolder) {
           this.port = port;
           this.personalFolder = personalFolder;
           connectFileGetter();
        }
*/
    public void connectFileGetter(int port, String personalFolder){

        Runnable r = new Runnable() {
             public void run() {
                try {
                    ServerSocket ss = new ServerSocket(port);
                    //area.append("Wait connect...");
                    System.out.println("Wait connect...");
                    Socket soket = ss.accept();
                    InputStream in = soket.getInputStream();
                    DataInputStream din = new DataInputStream(in);
                    boolean waiter = true;


                    while (waiter) {

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
                            FileOutputStream outF = new FileOutputStream("Folders/" + personalFolder + "/" + fileName);
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
                            System.out.println("\nПоток закрывается\n---------------------------------\n");

                        }
                        waiter = false;
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
