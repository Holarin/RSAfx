package sample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyGeneration
{

    private final static String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+
            "abcdefghijklmnopqrstuvwxyz"+
            "0123456789";

    private final static int KEYLENGTH = 1024;

    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String aesKey;

    public void generateRandomKeyAES() throws IOException
    {
        StringBuilder s = new StringBuilder();
        for(int i=0; i<16 ; i++)
        {
            s.append( ALPHA_NUMERIC.charAt((int) (Math.random() * ALPHA_NUMERIC.length())));
        }

        this.aesKey = s.toString();

        System.out.println("AES Key:"+this.aesKey);

        writeToFile("Keys/AES.txt", aesKey.getBytes());
    }

    public void generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, IOException
    {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(KEYLENGTH);

        this.createKeys();
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public void setPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(KEYLENGTH);
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public void setPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

    }

    private void createKeys() throws IOException
    {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();

        writeToFile("Keys/publicKey.txt", this.getPublicKey().getEncoded());
        writeToFile("Keys/privateKey.txt", this.getPrivateKey().getEncoded());

        System.out.println("PrivateKey:"+this.privateKey);
        System.out.println("PublicKey:"+this.publicKey);
    }

    public PrivateKey getPrivateKey()
    {
        return this.privateKey;
    }

    public PublicKey getPublicKey()
    {
        return this.publicKey;
    }

    public String getAESKey()
    {
        return this.aesKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException
    {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    public void encryptAESKey() throws IOException, GeneralSecurityException
    {
        RSA_Algorithm rsa = new RSA_Algorithm();
        rsa.encryptASEKey(this.aesKey.getBytes(),new File("Keys/ASE_Encrypted.txt"),  this.privateKey);

    }

    public void decryptAESKey() throws IOException, GeneralSecurityException
    {
        RSA_Algorithm rsa = new RSA_Algorithm();
        rsa.decryptASEKey(rsa.filetoBytes(new File("Keys/ASE_Encrypted.txt")),
                new File("Keys/ASE_Decrypted.txt"),  this.publicKey);

    }

    public static void main(String[] s) throws NoSuchAlgorithmException, NoSuchProviderException, IOException
    {
        KeyGeneration k = new KeyGeneration();
        k.generateKeyPair();
        k.generateRandomKeyAES();
        k.writeToFile("Keys/publicKey.txt", k.getPublicKey().getEncoded());
        k.writeToFile("Keys/privateKey.txt", k.getPrivateKey().getEncoded());
    }
}