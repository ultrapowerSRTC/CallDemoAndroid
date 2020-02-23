package com.srtc.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Title:CheckSumBuilder
 * <p>
 * Description:模拟一个CheckSum
 * </p>
 * Author Han.C
 * Date 2019/4/23 14:37
 */
public class CheckSumBuilder {
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    // 计算并获取CheckSum
    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("sha256", appSecret + nonce + curTime);
    }

    // 计算并获取md5值
    public static String getMD5(String requestBody) {
        return encode("md5", requestBody);
    }

    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest
                    = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    public static String getMD5String(String sourceStr) {
        String s = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            //这两行代码的作用是：
            // 将bytes数组转换为BigInterger类型。1，表示 +，即正数。
            BigInteger bigInt = new BigInteger(1, md.digest(sourceStr.getBytes()));
            // 通过format方法，获取32位的十六进制的字符串。032,代表高位补0 32位，X代表十六进制的整形数据。
            //为什么是32位？因为MD5算法返回的时一个128bit的整数，我们习惯于用16进制来表示，那就是32位。
            s = String.format("%032x", bigInt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }
}
