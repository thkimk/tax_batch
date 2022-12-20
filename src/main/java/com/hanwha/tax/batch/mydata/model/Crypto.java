package com.hanwha.tax.batch.mydata.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

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
@Component
public class Crypto {
	public static final String KEY = "486a35794e7a31467876466845785339454b7151344c6544546e757a75646c43";
	public static final String IV = "4d72416a71427a6e3746564c43785651";

	private Cipher cipherEnc;
	private Cipher cipherDec;

	public Crypto() {
		try {
			SecretKey secureKey = new SecretKeySpec(Hex.decodeHex(KEY.toCharArray()), "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(Hex.decodeHex(IV.toCharArray()));

			cipherEnc = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipherEnc.init(Cipher.ENCRYPT_MODE, secureKey, ivParameterSpec);
			cipherDec = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipherDec.init(Cipher.DECRYPT_MODE, secureKey, ivParameterSpec);
		} catch (DecoderException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
				 InvalidAlgorithmParameterException e) {
			log.error("**** 요청 처리 실패 {}", e);
		}
	}

	/**
	 * 암호화 : AES/CBC/PKCS5Padding, Base64 Encoding
	 * @param data
	 * @return
	 */
	public String encodeAESCBC(final String data) {
		String dataEnc = "";

		try {
			byte[] encrypted = cipherEnc.doFinal(data.trim().getBytes());
			dataEnc = Base64.encodeBase64String(encrypted);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			log.error("**** 요청 처리 실패 {}", e);
		}

		return dataEnc;
	}

	/**
	 * 복호화 : AES/CBC/PKCS5Padding, Base64 Decoding
	 * @param data
	 * @return
	 */
	public String decodeAESCBC(final String data) {
		String dataDec = "";

		try {
			byte[] decrypted = Base64.decodeBase64(data.trim());
			dataDec = new String(cipherDec.doFinal(decrypted));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			log.error("**** 요청 처리 실패 {}", e);
		}

		return dataDec;
	}
}
