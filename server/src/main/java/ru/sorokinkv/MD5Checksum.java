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


    public static String getMd5Text(String text) {
        try {
            String md5text = getMD5ChecksumText(text);
            System.out.println(md5text);
            return md5text;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMd5File(String fileName) {
        try {
            String md5file = getMD5ChecksumText(fileName);
            System.out.println(getMD5ChecksumFile(fileName));
            return md5file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}



