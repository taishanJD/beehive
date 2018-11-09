/*
 * Copyright © 2014-2015 Thunder Software Technology Co., Ltd.
 * All rights reserved.
 */

package com.quarkdata.yunpan.api.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encyption {
//	private static String Md5(String plainText) {
//		try {
//			MessageDigest md = MessageDigest.getInstance("MD5");
//			md.update(plainText.getBytes());
//			byte b[] = md.digest();
//
//			int i;
//
//			StringBuffer buf = new StringBuffer("");
//			for (int offset = 0; offset < b.length; offset++) {
//				i = b[offset];
//				if (i < 0)
//					i += 256;
//				if (i < 16)
//					buf.append("0");
//				buf.append(Integer.toHexString(i));
//			}
//
//			System.out.println("result: " + buf.toString());// 32位的加密
//			System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
//			return buf.toString();
//
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static String getMD5Encyption(String password) {
		MessageDigest mdInst = null;
		String result = null;
		try {
			mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(password.getBytes());		// 使用指定的字节更新摘要
			byte[] md = mdInst.digest();				// 获得密文
			result = MD5Encyption.byteArrToHexStr(md);
//			System.out.println("after jimi : " + result);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
        }
		return result;
	}
	
	public static String byteArrToHexStr(byte[] arrB) {  
		int iLen = arrB.length;
		// 每个byte(8位)用两个(16进制)字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			while (intTmp < 0) {						// 把负数转换为正数  
				intTmp = intTmp + 256;
			}
			if (intTmp < 16) {							// 小于0F的数需要在前面补0  
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}
	public static void main(String[] args) {
		String str=MD5Encyption.getMD5Encyption("admin");
		System.out.println(str);
	}
	
}
