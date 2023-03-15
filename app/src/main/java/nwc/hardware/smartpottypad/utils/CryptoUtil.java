package nwc.hardware.smartpottypad.utils;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import nwc.hardware.smartpottypad.listeners.OnSavedEndListener;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class CryptoUtil {
    private static final String TAG = "CryptoUtil";

    public static String DecryptText(byte[] data){
        String decryptText = null;
        try {
            SetPreferences setPreferences = SetPreferences.getInstance();
            SecretKeySpec keySpec = new SecretKeySpec(Base64.decode(setPreferences.getKey(), Base64.DEFAULT), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

            IvParameterSpec ivParameterSpec = new IvParameterSpec(SetPreferences.ivBytes);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
            decryptText = new String(cipher.doFinal(data));

            Log.d(TAG, "DecryptText --> " + decryptText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return decryptText;
    }

    public static String EncryptText(byte[] data){
        String encryptText = null;
        try {
            SetPreferences setPreferences = SetPreferences.getInstance();

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecretKey key = keyGenerator.generateKey();
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(SetPreferences.ivBytes);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            encryptText = Base64.encodeToString(cipher.doFinal(data), Base64.DEFAULT);
            setPreferences.setKey(Base64.encodeToString(key.getEncoded(), Base64.DEFAULT)).setAutoLogin(true).setLoginInfo(encryptText).addEndTaskListener(new OnSavedEndListener() {
                @Override
                public void onSaved() {
                    Log.d(TAG, "Save Preferences!!!");
                }
            }).save();

            Log.d(TAG, "EncryptText --> " + encryptText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return encryptText;
    }
}
