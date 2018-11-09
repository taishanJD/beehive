package com.quarkdata.yunpan.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by yanyq1129@thundersoft.com on 2018/7/19.
 */
public class ZipUtils2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);
    private static final int BUFFER_SIZE = 2 * 1024;

    private ZipUtils2() {
    }

    /**
     * 压缩成ZIP 方法1
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    public static void toZip(String srcDir, OutputStream out, boolean keepDirStructure) {
        long start = System.currentTimeMillis();
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), keepDirStructure);
            long end = System.currentTimeMillis();
            LOGGER.info("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            LOGGER.info("zip error from ZipUtils", e);
        }
    }

    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     */
    public static void toZip(List<File> srcFiles, OutputStream out) {
        long start = System.currentTimeMillis();
        try (ZipOutputStream zos = new ZipOutputStream(out);) {
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            LOGGER.info("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            LOGGER.info("zip error from ZipUtils", e);
        }
    }


    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean keepDirStructure) throws Exception {
        if (sourceFile.isFile()) {//文件
            byte[] buf = new byte[BUFFER_SIZE];
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
            return;
        }
        //目录
        File[] listFiles = sourceFile.listFiles();
        if (listFiles == null || listFiles.length == 0) {//空目录
            // 需要保留原来的文件结构时,需要对空文件夹进行处理
            if (keepDirStructure) {
                // 空文件夹的处理
                zos.putNextEntry(new ZipEntry(name + "\\/"));
                // 没有文件，不需要文件的copy
                zos.closeEntry();
            }
        } else {//非空目录
            for (File file : listFiles) {
                // 判断是否需要保留原来的文件结构
                if (keepDirStructure) {
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                } else {
                    compress(file, zos, file.getName(), keepDirStructure);
                }
            }
        }
    }

}