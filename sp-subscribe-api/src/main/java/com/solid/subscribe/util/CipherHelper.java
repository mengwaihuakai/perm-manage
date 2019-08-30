package com.solid.subscribe.util;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.util.FileCopyUtils;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CipherHelper {
    private final String _algo;
    private final byte[] _key;
    private final byte[] _iv;

    private CipherHelper(String algo, String hexKey, String hexIV) {
        this._algo = algo;
        this._key = fromHex(hexKey);
        this._iv = fromHex(hexIV);
    }


    public static CipherHelper V2() {
        return new CipherHelper("AES/CBC/PKCS5Padding", "bdc818355a2bf0706f9d4ec165c2ae00", "52b882384f29962040e6bb30278c13c9");
    }

    /* analytics/v3/**
        AES/CBC/PKCS5Padding
        hex(key) = bdc818355a2bf0706f9d4ec165c2ae00
        hex(iv) = 52b882384f29962040e6bb30278c13c9
     */
    public static CipherHelper V3() {
        return new CipherHelper("AES/CBC/PKCS5Padding", "bdb818355a2bf0706f9d4ec165c2ae00", "6835703e9199e98913752df4ebc43bc4");
    }

    public byte[] zip(final byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length / 8);
        GZIPOutputStream os = new GZIPOutputStream(bos);
        os.write(data);
        os.flush();
        os.close();
        return bos.toByteArray();
    }

    public byte[] unzip(final byte[] data) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        GZIPInputStream is = new GZIPInputStream(bis);
        return FileCopyUtils.copyToByteArray(is);
    }


    private static int fromHex(char c) {
        if (c >= 'a' && c <= 'f')
            return (c - 'a') + 10;
        if (c >= 'A' && c <= 'F')
            return (c - 'A') + 10;
        if (c >= '0' && c <= '9')
            return (c - '0');

        throw new IllegalArgumentException("must be hex!");
    }

    private static byte[] fromHex(String hex) {
        if (hex == null)
            return null;

        if (hex.length() % 2 != 0)
            throw new IllegalArgumentException("length must be even!");

        byte[] bin = new byte[hex.length() / 2];
        for (int i = 0; i < bin.length; i++) {
            bin[i] = (byte) ((fromHex(hex.charAt(i * 2)) << 4) | (fromHex(hex.charAt(i * 2 + 1))));
        }

        return bin;
    }


    public byte[] encrypt(final byte[] data) {
        try {
            SecretKeySpec key = new SecretKeySpec(_key, "AES");
            AlgorithmParameterSpec iv = new IvParameterSpec(_iv);
            Cipher cipher = Cipher.getInstance(_algo);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(data);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public byte[] decrypt(final byte[] data) {
        try {
            SecretKeySpec key = new SecretKeySpec(_key, "AES");
            AlgorithmParameterSpec iv = new IvParameterSpec(_iv);
            Cipher cipher = Cipher.getInstance(_algo);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(data);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public byte[] encrypt(final String data) throws Exception {
        return encrypt(data.getBytes("utf-8"));
    }

    public String decrypt_unzip(final String data) throws Exception {
        byte[] plusPwd = data.getBytes("utf-8");
        return new String(unzip(decrypt(plusPwd)));
    }

    public String decrypt_unzip(final byte[] data) throws Exception {
        return new String(unzip(decrypt(data)));
    }

    public static void main(String[] args) throws Exception {
        CipherHelper helper = CipherHelper.V3();
        String s = "test12334";
        String result = Base64.getEncoder().encodeToString(helper.encrypt(helper.zip(s.getBytes())));
        System.out.println(result);
        System.out.println(helper.decrypt_unzip(Base64.getDecoder().decode(result)));
    }
}
