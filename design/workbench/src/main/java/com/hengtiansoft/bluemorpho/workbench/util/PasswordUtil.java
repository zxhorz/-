package com.hengtiansoft.bluemorpho.workbench.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 13, 2018 1:33:04 PM
 */
public class PasswordUtil {
    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class);

	public static String MD5(String text) {
		try {
			StringBuffer buf = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
		    LOGGER.error(e);
		}
		return null;
	}
	
	public static String generateRandomString(int length) {  
        String val = "";  
        Random random = new Random();        
        for(int i = 0; i < length; i++) {          
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";  
            if( "char".equalsIgnoreCase(charOrNum) ) {  
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;  
                val += (char)(random.nextInt(26) + temp);  
            } else if( "num".equalsIgnoreCase(charOrNum) ) {  
                val += String.valueOf(random.nextInt(10));  
            }  
        }  
        return val;  
    }  
	
}
