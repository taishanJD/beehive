package com.quarkdata.yunpan.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author maorl
 * @date 12/14/17 2:00 PM
 */
public class ZipUtils {

    private ZipUtils() {
    }

    public static void doCompress(String srcFile, String zipFile) throws IOException {
        doCompress(new File(srcFile), new File(zipFile));
    }

    /**
     * 文件压缩
     *
     * @param srcFile 目录或者单个文件
     * @param zipFile 压缩后的ZIP文件
     */
    public static void doCompress(File srcFile, File zipFile) throws IOException {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            doCompress(srcFile, out);
        } catch (Exception e) {
            throw e;
        } finally {
            out.close();//记得关闭资源
        }
    }

    public static void doCompress(String fileName, ZipOutputStream out) throws IOException {
        doCompress(new File(fileName), out);
    }

    public static void doCompress(File file, ZipOutputStream out) throws IOException {
        File[] subFiles = file.listFiles();
        if(subFiles != null) {
            for(File subFile : subFiles) {
                doCompress(subFile, out, "");
            }
        }
    }

    public static void doCompress(File inFile, ZipOutputStream out, String dir) throws IOException {
        if (inFile.isDirectory()) {
            File[] files = inFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    String name = inFile.getName();
                    if (!"".equals(dir)) {
                        name = dir + "/" + name;
                    }
                    ZipUtils.doCompress(file, out, name);
                }
            } else {
                ZipUtils.doZip(inFile, out, dir);
            }
        } else {
            ZipUtils.doZip(inFile, out, dir);
        }
    }

    public static void doZip(File inFile, ZipOutputStream out, String dir) throws IOException {
        String entryName = inFile.getName();
        if (!"".equals(dir)) {
            if (inFile.isDirectory()) {
                entryName = dir + "/" + entryName + "\\/";
            } else {
                entryName = dir + "/" + entryName;
            }
        }else{
            if (inFile.isDirectory()) {
                entryName = entryName + "\\/";
            }
        }
        ZipEntry entry = new ZipEntry(entryName);
        out.putNextEntry(entry);

        if (!inFile.isDirectory()) {
            int len;
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(inFile);
            while ((len = fis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                out.flush();
            }
            fis.close();
        }
        out.closeEntry();


    }


}
