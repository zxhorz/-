package com.zxh.dormMG.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.log4j.Logger;

public class PasswordUtil {
    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class);
    private static final Integer PASSWORD_LENGTH = 10;
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

	public static String generatePassword() {
		// TODO Auto-generated method stub
		char charr[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*.?".toCharArray();
		// System.out.println("字符数组长度:" + charr.length); //可以看到调用此方法多少次
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		for (int x = 0; x < PASSWORD_LENGTH; ++x) {
			sb.append(charr[r.nextInt(charr.length)]);
		}
		return sb.toString();
	}

}
