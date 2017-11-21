package com.example.tue2t.iotdevicelist.Security;


import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;


import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;


/**
 * Created by tue2t on 9/10/2017.
 */

public class DHKeyExchange {



    public static final String TAG = "KeyStore";
    private PrivateKey privateKey;
    private PublicKey  publicKey;
    private PublicKey  receivedPublicKey;
    public byte[]     secretKey;
    private int counter = 0;
    private String mAlias = "testKey";
    private SecretKey sKey;
    private byte[] iv;


    /*
        Generate key pair and store in keyStore
     */
    @TargetApi(Build.VERSION_CODES.M)
    public String generateKeys() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(mAlias + String.valueOf(counter), KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(128)
                    //  .setKeySize(192)
                    //  .setKeySize(256)
                        .build();


            keyGenerator.init(spec);
            sKey = keyGenerator.generateKey();
            counter++;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return mAlias+String.valueOf(counter);
    }


    /*
    Key Store operation
     */

    public void getSkey (String alias) {
        try {
            KeyStore ks = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);

            // sKey = (SecretKey) ks.getEntry(alias, null);
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) ks.getEntry(alias, null);
            sKey = secretKeyEntry.getSecretKey();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "Fail to get SecretKey");
        }
    }

    public String encrypt(String alias, String toencrypt) {
        String retStr = "";
        getSkey(alias);
        try {
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            byte[] encryptByte = cipher.doFinal(toencrypt.getBytes("UTF-8"));
            iv = cipher.getIV();
            retStr = Base64.encodeToString(encryptByte, Base64.DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return retStr;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String decrypt(String alias, String todecrypt) {
        String str = "";
        byte[] todecryptByte = Base64.decode(todecrypt, Base64.DEFAULT);
        try {
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, sKey, spec);

            final byte[] decodedData = cipher.doFinal(todecryptByte);
            str = new String(decodedData, "UTF-8");


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /*
    RSA operation
     */

    /*
    // retrieve public key
    public PublicKey getPublicKey(String alias) {
        PublicKey pubKey = null;
        try {
            KeyStore ks = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);

            KeyStore.Entry entry = ks.getEntry(alias, null);
            PrivateKey privKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            pubKey = ks.getCertificate(alias).getPublicKey();


        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "Fail to load keystore");
        }
        return pubKey;
    }

    public PrivateKey getPrivateKey(String alias) {
        PrivateKey privKey = null;
        try {
            KeyStore ks = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);

            KeyStore.Entry entry = ks.getEntry(alias, null);
            privKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "Fail to get Private Key");
        }
        return privKey;
    }

    public String encrypt(String alias, String toEncryptStr) {
        PublicKey pubKey = getPublicKey(alias);
        String cipherStr = "";
        if (pubKey == null) {
            Log.d("Error", "Fail to retrieve public key");
            return cipherStr;
        }

        try {
            Log.d("Test", "ENCRYPTING");
            final Cipher cipher = Cipher.getInstance(SecurityConstants.TYPE_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] cipherText = cipher.doFinal(toEncryptStr.getBytes());
            cipherStr = Base64.encodeToString(cipherText, Base64.DEFAULT);

        }
        catch(Exception e) {
            e.printStackTrace();
            Log.d("Error", "Fail to encrypt string");
        }
        return cipherStr;
    }

    public String decrypt (String alias, String toDecryptString) {
        String decryptStr = "";
        PrivateKey privKey = getPrivateKey(alias);
        byte[] toDecryptByte = Base64.decode(toDecryptString, Base64.DEFAULT);

        if (privKey == null) {
            Log.d("Error", "Fail to retrieve public key");
            return decryptStr;
        }

        try {
            Log.d("Test", "DECRYPTING");
            final Cipher cipher1 = Cipher.getInstance(SecurityConstants.TYPE_RSA);
            cipher1.init(Cipher.DECRYPT_MODE, privKey);
            byte[] decryptByte = cipher1.doFinal(toDecryptByte);
            decryptStr = Base64.encodeToString(decryptByte, Base64.DEFAULT);
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.d("Error", "Fail to decrypt string");
        }
        return decryptStr;
    }
    */


    /*
    DH Key Exchange Helper Function
     */


    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void receivePublicKeyFrom(final DHKeyExchange keyExchange) {

        receivedPublicKey = keyExchange.getPublicKey();
    }

    public void generateCommonSecretKey() {

        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance(SecurityConstants.TYPE_DH);
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receivedPublicKey, true);


            byte[] longSecret = keyAgreement.generateSecret();
            secretKey = shortenSecretKey(longSecret);

            // store secret in key store
            KeyStore ks = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);
            ks.setKeyEntry("sharedKey", secretKey, null);

            /*
            // Print out the secret
            System.out.println("secret! is : " );
            for (byte b : secretKey) {
                System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1));
            }
            System.out.println("");
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 1024 bit symmetric key size is so big for DES so we must shorten the key size. You can get first 8 longKey of the
     * byte array or can use a key factory
     *
     * @param   longKey
     *
     * @return
     */
    private byte[] shortenSecretKey(final byte[] longKey) {

        try {

            // Use 8 bytes (64 bits) for DES, 6 bytes (48 bits) for Blowfish
            final byte[] shortenedKey = new byte[8];

            System.arraycopy(longKey, 0, shortenedKey, 0, shortenedKey.length);

            return shortenedKey;

            // Below lines can be more secure
            // final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // final DESKeySpec       desSpec    = new DESKeySpec(longKey);
            //
            // return keyFactory.generateSecret(desSpec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
