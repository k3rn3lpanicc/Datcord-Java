package EncryptionModel;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class Asymmetric {
    private KeyPair pair;
    private PublicKey userKey;

    public PublicKey getUserKey() {
        return userKey;
    }

    public Asymmetric(){
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        generator.initialize(2048);
        this.pair = generator.generateKeyPair();
    }
    public void setUserKey(PublicKey userKey) {
        this.userKey = userKey;
    }
    public PublicKey getPublic(){
        return pair.getPublic();
    }


    public String encrypt(String data) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt2(data));
    }

    public  byte[] encrypt2(String data) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, userKey);
        return cipher.doFinal(data.getBytes());
    }

    public String decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
        return new String(cipher.doFinal(data));
    }
    public  String decrypt(String data) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()));
    }


}
