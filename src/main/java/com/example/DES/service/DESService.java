package com.example.DES.service;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

@Service
public class DESService {
    byte[] initialization_vector = { 21, 31, 15, 44, 55, 99, 66, 77 };
    public static String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        return hexString.toString();
    }

    public SecretKey getSecretKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance("DES").generateKey();
    }
    public SecretKey keyDecrypt(String keyText){
        return new SecretKeySpec(Base64.getDecoder().decode(keyText), 0, 8, "DES");
    }
    public String encode(String text, String secretKey) throws Exception{
        Cipher encode = Cipher.getInstance("DES/CBC/PKCS5Padding");
        AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(initialization_vector);
        encode.init(Cipher.ENCRYPT_MODE, keyDecrypt(secretKey), algorithmParameterSpec);
        return toHexString(encode.doFinal(text.getBytes()));
    }
    public String decode(String text, String secretKey) throws Exception {
        Cipher decode = Cipher.getInstance("DES/CBC/PKCS5Padding");
        AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(initialization_vector);
        decode.init(Cipher.DECRYPT_MODE, keyDecrypt(secretKey), algorithmParameterSpec);
        BigInteger bigInteger = new BigInteger(text, 16);
        byte[] textByte = bigInteger.toByteArray();
        if (textByte[0] == Byte.parseByte("0")){
            textByte = Arrays.copyOfRange(textByte, 1, textByte.length);
        }
        return new String(decode.doFinal(textByte));
    }
    public String getRunStyle(XWPFRun run) {
        StringBuilder style = new StringBuilder(" style=\"");

        // Font family
        String fontFamily = run.getFontFamily();
        if (fontFamily != null && !fontFamily.isEmpty()) {
            style.append("font-family: ").append(fontFamily).append(";");
        }
        else {
            style.append("font-family: ").append("Calibri").append(";");
        }

        // Font size
        if (run.getFontSizeAsDouble() != null){
            int fontSize = run.getFontSizeAsDouble().intValue();
            style.append("font-size: ").append(fontSize).append("pt;");
        }
        // Other styling properties (e.g., bold, italic, underline, color, etc.)
        if (run.isBold()) {
            style.append("font-weight: bold;");
        }
        if (run.isItalic()) {
            style.append("font-style: italic;");
        }
        if (run.isStrikeThrough()) {
            style.append("text-decoration: line-through;");
        }
        if (run.getColor() != null) {
            style.append("color: #").append(run.getColor()).append(";");
        }
        if (run.getUnderline() != UnderlinePatterns.NONE) {
            style.append("text-decoration: underline;");
        }
        if (!Objects.equals(String.valueOf(run.getTextHighlightColor()), "none")){
            style.append("background-color: ").append(run.getTextHighlightColor()).append(";");
        }
        style.append("\"");
        return style.toString();
    }
}
