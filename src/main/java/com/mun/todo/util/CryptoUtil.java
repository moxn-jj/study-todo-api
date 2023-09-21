package com.mun.todo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class CryptoUtil {

    private static String secretKey;

    private static String salt;

    @Value("${crypto.secret}")
    public void setSecretKey(String secret) {
        CryptoUtil.secretKey = secret;
    }

    @Value("${crypto.salt}")
    public void setSalt(String s) {
        CryptoUtil.salt = s;
    }
    public static String encrypt(String data) {

        SecretKey secret = new SecretKeySpec(secretKey.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(salt.getBytes());
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret, iv);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.info("??");
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedData) {
        SecretKey secret = new SecretKeySpec(secretKey.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(salt.getBytes());
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, iv);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
