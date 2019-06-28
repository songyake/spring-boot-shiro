package com.test.springboot.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * @Author: songyake
 * @Date: 2019/06/28
 */
public class EncryptUtil {

	private static final Logger logger =  LoggerFactory.getLogger(EncryptUtil.class);

	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA1";
	public static final String HmacMD5 = "HmacMD5";
	public static final String HmacSHA1 = "HmacSHA1";
	public static final String HmacSHA256 = "HmacSHA256";
	public static final String DES = "DES";
	public static final String AES = "AES";

	/**编码格式*/
	public static final String CHARSET = "UTF-8";
	/**DES*/
	public static final int KEYSIZEDES = 0;
	/**AES*/
	public static final int KEYSIZEAES = 128;
	public static EncryptUtil me = new EncryptUtil();

	private EncryptUtil(){
		//单例
	}

	public static EncryptUtil getInstance(){
		return me;
	}

	/**
	 * 使用MessageDigest进行单向加密（无密码）
	 * @param str 明文
	 * @param algorithm 加密算法
	 * @return
	 */
	private String messageDigest(String str,String algorithm){
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] resBytes = CHARSET==null?str.getBytes():str.getBytes(CHARSET);
			return base64(md.digest(resBytes));
		} catch (Exception e) {
			logger.warn("EncryptUtil messageDigest is error",e);
		}
		return null;
	}

	/**
	 * 使用KeyGenerator进行单向/双向加密（可设密码）
	 * @param str 明文
	 * @param algorithm 加密算法
	 * @param key 盐值
	 * @return
	 */
	private String keyGeneratorMac(String str,String algorithm,String key){
		try {
			SecretKey sk = null;
			if (key==null) {
				KeyGenerator kg = KeyGenerator.getInstance(algorithm);
				sk = kg.generateKey();
			}else {
				byte[] keyBytes = CHARSET==null?key.getBytes():key.getBytes(CHARSET);
				sk = new SecretKeySpec(keyBytes, algorithm);
			}
			Mac mac = Mac.getInstance(algorithm);
			mac.init(sk);
			byte[] result = mac.doFinal(str.getBytes());
			return base64(result);
		} catch (Exception e) {
			logger.warn("EncryptUtil keyGeneratorMac is error",e);
		}
		return null;
	}

	/**
	 * 使用KeyGenerator双向加密，DES/AES，注意这里转化为字符串的时候是将2进制转为16进制格式的字符串，不是直接转，因为会出错
	 * @param str 明文
	 * @param algorithm 加密算法
	 * @param key 盐值
	 * @param keysize
	 * @param isEncode 是否编码转换
	 * @return
	 */
	private String keyGeneratorES(String str,String algorithm,String key,int keysize,boolean isEncode){
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			KeyGenerator kg = KeyGenerator.getInstance(algorithm);
			if (keysize == 0) {
				byte[] keyBytes = CHARSET==null?key.getBytes():key.getBytes(CHARSET);
				secureRandom.setSeed(keyBytes);
				kg.init(secureRandom);
			}else if (key==null) {
				kg.init(keysize);
			}else {
				byte[] keyBytes = CHARSET==null?key.getBytes():key.getBytes(CHARSET);
				secureRandom.setSeed(keyBytes);
				kg.init(keysize, secureRandom);
			}
			SecretKey sk = kg.generateKey();
			SecretKeySpec sks = new SecretKeySpec(sk.getEncoded(), algorithm);
			Cipher cipher = Cipher.getInstance(algorithm);
			if (isEncode) {
				cipher.init(Cipher.ENCRYPT_MODE, sks);
				byte[] resBytes = CHARSET==null?str.getBytes():str.getBytes(CHARSET);
				return parseByte2HexStr(cipher.doFinal(resBytes));
			}else {
				cipher.init(Cipher.DECRYPT_MODE, sks);
				return new String(cipher.doFinal(parseHexStr2Byte(str)));
			}
		} catch (Exception e) {
			logger.warn("EncryptUtil keyGeneratorES is error",e);
		}
		return null;
	}

	public String base64(byte[] bytes){
		return Base64.encode(bytes);
	}
	/**
	 * 将二进制转换成16进制
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	/**
	 * 将16进制转换为二进制
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length()/2];
		for (int i = 0;i< hexStr.length()/2; i++) {
			int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
			int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
	/**
	 * MD5加密
	 * @param str 明文
	 * @return 密文
	 */
	public String MD5(String str) {
		return messageDigest(str, MD5);
	}
	/**
	 * MD5 加密
	 * @param str 明文
	 * @param key 盐值
	 * @return 密文
	 */
	public String MD5(String str, String key) {
		return keyGeneratorMac(str, HmacMD5, key);
	}
	/**
	 * SHA1 加密
	 * @param str
	 * @return
	 */
	public String SHA1(String str) {
		return messageDigest(str, SHA1);
	}
	/**
	 * SHA1 加密
	 * @param str 加密字符串
	 * @param key 盐值
	 * @return
	 */
	public String SHA1(String str, String key) {
		return keyGeneratorMac(str, HmacSHA1, key);
	}
	/**
	 * SHA256 加密
	 * @param str 加密字符串
	 * @param key 盐值
	 * @return
	 */
	public String SHA256(String str, String key) {
		return keyGeneratorMac(str, HmacSHA256, key);
	}
	/**
	 * DES 加密
	 * @param str 明文
	 * @param key
	 * @return
	 */
	public String DESencode(String str, String key) {
		return keyGeneratorES(str, DES, key, KEYSIZEDES, true);
	}
	/**
	 * DES 解密
	 * @param str
	 * @param key
	 * @return
	 */
	public String DESdecode(String str, String key) {
		return keyGeneratorES(str, DES, key, KEYSIZEDES, false);
	}
	/**
	 * AES 加密
	 * @param str
	 * @param key
	 * @return
	 */
	public String AESencode(String str, String key) {
		return keyGeneratorES(str, AES, key, KEYSIZEAES, true);
	}
	/**
	 * AES 解密
	 * @param str
	 * @param key
	 * @return
	 */
	public String AESdecode(String str, String key) {
		return keyGeneratorES(str, AES, key, KEYSIZEAES, false);
	}
	/**
	 * 异或加密
	 * @param str
	 * @param key
	 * @return
	 */
	public String XORencode(String str, String key) {
		byte[] bs = str.getBytes();
		for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte) ((bs[i]) ^ key.hashCode());
		}
		return parseByte2HexStr(bs);
	}
	/**
	 * 异或加密
	 * @param str
	 * @param key
	 * @return
	 */
	public String XORdecode(String str, String key) {
		byte[] bs = parseHexStr2Byte(str);
		for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte) ((bs[i]) ^ key.hashCode());
		}
		return new String(bs);
	}
	/**
	 * 异或加密
	 * @param str
	 * @param key
	 * @return
	 */
	public int XOR(int str, String key) {
		return str ^ key.hashCode();
	}
}
