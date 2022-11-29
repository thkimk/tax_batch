package com.hanwha.tax.batch;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class CryptoUtil {
	public static final String KEY = "Yq3t6w9z$C&F)J@M";
	public static final String IV = "Yq3t6w9z$C&F)J@M";

	/**
	 * 암호화 : AES/CBC/PKCS5Padding, Base64 Encoding
	 * @param data
	 * @return
	 */
	public static String encodeAESCBC(String data) {
		String dataEnc = "";

		try {
			SecretKey secureKey = new SecretKeySpec(KEY.getBytes(), "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
			byte[] encrypted = c.doFinal(data.trim().getBytes());
			dataEnc = Base64.encodeBase64String(encrypted);
		} catch (NoSuchAlgorithmException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (NoSuchPaddingException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (InvalidKeyException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (IllegalBlockSizeException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (BadPaddingException e) {
			log.error("**** 요청 처리 실패 {}", e);
		}

		return dataEnc;
	}

	/**
	 * 복호화 : AES/CBC/PKCS5Padding, Base64 Decoding
	 * @param data
	 * @return
	 */
	public static String decodeAESCBC(String data) {
		String dataDec = "";

		try {
			SecretKey secureKey = new SecretKeySpec(KEY.getBytes(), "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
			byte[] decrypted = Base64.decodeBase64(data.trim());
			dataDec = new String(c.doFinal(decrypted));
		} catch (NoSuchAlgorithmException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (NoSuchPaddingException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (InvalidKeyException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (IllegalBlockSizeException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} catch (BadPaddingException e) {
			log.error("**** 요청 처리 실패 {}", e);
		}

		return dataDec;
	}
}
