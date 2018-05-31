package ru.sorokinkv;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;

//----------Block for testng-----------
/*
import static ru.sorokinkv.MD5Checksum.*;

class MainMD5 {
    public static void main(String[] args) {
        printMd5Text("");
    }
}
*/

public class MD5Checksum {

    public static String getMD5ChecksumText(String someText) throws Exception {
        MessageDigest complete = MessageDigest.getInstance("MD5");
        complete.update(someText.getBytes(Charset.forName("UTF-8")));
        return Hex.encodeHexString(complete.digest());
    }

    public static String getMD5ChecksumFile(String fileName) {
        try (InputStream fis = new FileInputStream(fileName)) {
            return DigestUtils.md5Hex(fis);
        } catch (Throwable couse) {
            couse.printStackTrace();
        }
        return null;
    }


    public static void printMd5Text(String text) {
        try {
            System.out.println(getMD5ChecksumText(text));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printMd5File(String fileName) {
        try {
            System.out.println(getMD5ChecksumFile(fileName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



