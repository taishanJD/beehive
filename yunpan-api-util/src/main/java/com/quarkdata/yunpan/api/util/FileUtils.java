package com.quarkdata.yunpan.api.util;

import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author typ 2017年12月21日
 */
public class FileUtils {

	/**
	 * 获取文件的md5值
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static String getMd5Hex(InputStream inputStream) throws IOException {
		String md5 = DigestUtils.md5Hex(IOUtils.toByteArray(inputStream));
		if(inputStream != null) {
			inputStream.close();
		}
		return md5;
	}

	public static String getBigFileMD5(MultipartFile multipartFile) throws IOException {
		File file = multipartToFile(multipartFile);
		// 缓冲区大小（这个可以抽出一个参数）
		int bufferSize = 8 * 1024;
		FileInputStream fileInputStream = new FileInputStream(file);
		DigestInputStream digestInputStream = null;
		try {
			// 拿到一个MD5转换器（同样，这里可以换成SHA1）
			MessageDigest messageDigest =MessageDigest.getInstance("MD5");
			// 使用DigestInputStream
			digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
			// read的过程中进行MD5处理，直到读完文件
			byte[] buffer =new byte[bufferSize];
			while (digestInputStream.read(buffer) > 0);
			// 获取最终的MessageDigest
			messageDigest= digestInputStream.getMessageDigest();
			// 拿到结果，也是字节数组，包含16个元素
			byte[] resultByteArray = messageDigest.digest();
			// 同样，把字节数组转换成字符串
			return MD5Encyption.byteArrToHexStr(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} finally {
			try {
				digestInputStream.close();
			} catch (Exception e) {
			}
			try {
				fileInputStream.close();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 文件大小转化为易读的格式(KB、MB、GB)
	 * @param size 
	 * @return	
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "Byte(s)";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}

	public static File multipartToFile(MultipartFile multfile) throws IOException {
		CommonsMultipartFile cf = (CommonsMultipartFile) multfile;
		// 这个myfile是MultipartFile的
		DiskFileItem fi = (DiskFileItem) cf.getFileItem();
		File file = fi.getStoreLocation();
		// 手动创建临时文件
		if (file.length() < DocumentConstants.MIN_FILE_SIZE) {
			File tmpFile = new File(System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + file.getName());
			multfile.transferTo(tmpFile);
			return tmpFile;
		}
		return file;
	}

}
