package EncryptionModel;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
public class Symmetric {
    SecretKey key;
    private static final String AES = "AES";
    private byte[] initializationVector = {10,10,20,30,10,20,1,40,3,35,11,12,13,48,19,39};
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";
    public Symmetric(){

    }
    public void generateKey(){
        try {
            key = Symmetric.createAESKey();
            //System.out.println("Symmetric key : "+DatatypeConverter.printHexBinary(key.getEncoded()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public static SecretKey createAESKey() throws Exception {
        SecureRandom securerandom = new SecureRandom();
        KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
        keygenerator.init(128, securerandom);
        SecretKey key = keygenerator.generateKey();
        return key;
    }

    public String encrypt(String data){
        try {
            byte[] cipherText = do_AESEncryption(data, key, initializationVector);
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String decrypt(String data){
        byte[] cipherText = Base64.getDecoder().decode(data);
        try {
            return do_AESDecryption(cipherText , key , initializationVector);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private byte[] do_AESEncryption(String plainText, SecretKey secretKey, byte[] initializationVector) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(plainText.getBytes());
    }
    private String do_AESDecryption(byte[] cipherText, SecretKey secretKey, byte[] initializationVector) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] result = cipher.doFinal(cipherText);
        return new String(result);
    }
}
