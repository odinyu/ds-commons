package org.ds.common;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 把字符串进行MD5加密
     *
     * @param string 字符串
     * @return MD5加密后的字符串
     */
    public static String encode(String string) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashValue = md.digest(string.getBytes());
            for (byte b : hashValue) {
                sb.append(Integer.toHexString((b & 0xf0) >> 4));
                sb.append(Integer.toHexString(b & 0x0f));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte b1 : b) {
            sb.append(HEX_DIGITS[(b1 & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b1 & 0x0f]);
        }
        return sb.toString();
    }

    public static String md5sum(String filename) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead;
        MessageDigest md5;
        try {
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            String md5Str = toHexString(md5.digest());
            return StringUtils.isEmpty(md5Str) ? "" : md5Str;
        } catch (Exception e) {
            System.out.println("error");
            return "";
        }
    }
}
