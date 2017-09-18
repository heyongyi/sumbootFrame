package org.sumbootFrame.tools;

import org.apache.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES Coder<br/>
 * secret key length:   128bit, default:    128 bit<br/>
 * mode:    ECB/CBC/PCBC/CTR/CTS/CFB/CFB8 to CFB128/OFB/OBF8 to OFB128<br/>
 * padding: Nopadding/PKCS5Padding/ISO10126Padding/
 * @author Aub
 *
 */
public final class AESCoder {
	
	protected  final static Logger logger = Logger.getLogger(AESCoder.class);

    /**
     * 加密
     * 
     * @param content 需要加密的内容
     * @param password  加密密码
     * @return
     */
    public static String encrypt(String content,String password) {
            try {
                    KeyGenerator kgen = KeyGenerator.getInstance("AES");
                    SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG"  );   
                    secureRandom.setSeed(password.getBytes());
                    kgen.init(128, secureRandom);
                    SecretKey secretKey = kgen.generateKey();
                    byte[] enCodeFormat = secretKey.getEncoded();
                    SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
                    Cipher cipher = Cipher.getInstance("AES");// 创建密码器
                    byte[] byteContent = content.getBytes("utf-8");
                    cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
                    byte[] result = cipher.doFinal(byteContent);
                    return parseByte2HexStr(result); // 加密
            } catch (NoSuchAlgorithmException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (NoSuchPaddingException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (InvalidKeyException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (UnsupportedEncodingException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (IllegalBlockSizeException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (BadPaddingException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            }
            return null;
    }
    
    /**解密
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decrypt(String content,String password) {
            try {
            	 byte[] decryptFrom = parseHexStr2Byte(content);
                     KeyGenerator kgen = KeyGenerator.getInstance("AES");
                     SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG"  );   
                     secureRandom.setSeed(password.getBytes());
                     kgen.init(128, secureRandom);
                     SecretKey secretKey = kgen.generateKey();
                     byte[] enCodeFormat = secretKey.getEncoded();
                     SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");            
                     Cipher cipher = Cipher.getInstance("AES");// 创建密码器
                    cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
                    byte[] result = cipher.doFinal(decryptFrom);
                    try {
						return new String(result,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // 加密
            } catch (NoSuchAlgorithmException e) {
                   logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (NoSuchPaddingException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (InvalidKeyException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (IllegalBlockSizeException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            } catch (BadPaddingException e) {
            	logger.error("Emarketing:AEScoder-->"+e.getMessage());
            }
            return null;
    }
    
    /**将二进制转换成16进制
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
    
    /**将16进制转换为二进制
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
    
//    public static void main(String[] args) {
//        String content = "graborder";
//        //加密
//        System.out.println("加密前：" + content);
//        String encryptResultStr = encrypt(content);
//        System.out.println("加密后：" + encryptResultStr);
//        //解密
//       String decryptResult = decrypt(encryptResultStr);
//        System.out.println("解密后：" + decryptResult);
//	}
}
