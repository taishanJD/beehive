package com.quarkdata.yunpan.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author maorl
 * @date 12/19/17.
 */
public class DownloadUtil {
    static Logger logger = LoggerFactory.getLogger(DownloadUtil.class);

    private static String mediaType;
    private static String IMAGE_TYPE;

    @Value("${IMAGE_TYPE}")
    public void setIMAGE_TYPE(String IMAGE_TYPE) {
        this.IMAGE_TYPE = IMAGE_TYPE;
    }

    @Value("${MEDIA_TYPE}")
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * 下载
     *
     * @param filePath
     * @param response
     */
    public static String download(String filePath, HttpServletResponse response) {
        logger.info("开始从服务器下载文件:"+filePath);
        String fileName = getFileName(filePath);
        File file = new File(filePath);
        try {
            // 设置Content-Disposition
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            String contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName;
            response.setHeader("Access-Control-Expose-Headers", "filename");
            response.setHeader("filename", encodedFileName);
            response.setHeader("Content-Disposition", contentDisposition);
            response.setContentLength(Integer.parseInt(file.length() + ""));
            response.setHeader("Accept-Ranges", "bytes");
            // 读取文件
            InputStream ins = new FileInputStream(filePath);
            // 放到缓冲流里面
            BufferedInputStream bins = new BufferedInputStream(ins);
            // 获取文件输出IO流
            // 读取目标文件，通过response将目标文件写到客户端
            OutputStream outs = response.getOutputStream();
            BufferedOutputStream bouts = new BufferedOutputStream(outs);
            // 获取文件后缀,判断是否为多媒体类型
            String fileType = DownloadUtil.getFileType(filePath);
            if(fileType != null && DownloadUtil.mediaType.contains(fileType.toLowerCase())) {
                response.setContentType("audio/mp4");
            }
            if(fileType != null && DownloadUtil.IMAGE_TYPE.contains(fileType.toLowerCase())) {
                response.setContentType("image/*");
            }

            // 写文件
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            // 开始向网络传输文件流
            while ((bytesRead = bins.read(buffer, 0, 8192)) != -1) {
                bouts.write(buffer, 0, bytesRead);

            }
            bouts.flush();
            ins.close();
            bins.close();
            outs.close();
            bouts.close();
            logger.info("下载完成："+fileName);
            return fileName;
        } catch (Exception e) {
            logger.error("download from server error" + e);
        } finally {
            //删除服务器临时文件
            DeleteFileUtil.delete(new File(filePath).getParent());
        }

        return null;
    }


    /**
     * 截取文件名
     *
     * @param filePath
     * @return
     */
    private static String getFileName(String filePath) {
        int index = filePath.lastIndexOf("/");
        return filePath.substring(index + 1);
    }

    /**
     * 截取文件类型
     * @param filePath
     * @return
     */
    private static String getFileType(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }

    /**
     * 断点下载
     * @param range
     * @param filePath
     * @param response
     */
    public static void breakPointDownload(String range, String filePath, HttpServletResponse response) {
        try {
            logger.info("<<<<<< download from server start in {} byte ", range);
            String fileName = getFileName(filePath);
            RandomAccessFile raFile = new RandomAccessFile(filePath, "r");

            int start = 0, end = 0;
            if(null!=range && range.startsWith("bytes=")){
                String[] values =range.split("=")[1].split("-");
                start = Integer.parseInt(values[0]);
                end = Integer.parseInt(values[1]);
            }
            int requestSize = 0;
            if(end!=0 && end > start){
                requestSize = end - start + 1;
                response.setContentLength(requestSize);
            } else {
                requestSize = Integer.MAX_VALUE;
            }

            byte[] buffer = new byte[4096];
            // 设置Content-Disposition
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            String contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName;
            response.setHeader("Access-Control-Expose-Headers", "filename");
            response.setHeader("filename", encodedFileName);
            response.setHeader("Content-Disposition", contentDisposition);
            response.setHeader("Accept-Ranges", "bytes");
            ServletOutputStream os = response.getOutputStream();
            int needSize = requestSize;
            raFile.seek(start);
            while(needSize > 0){
                int len = raFile.read(buffer);
                if(needSize < buffer.length){
                    os.write(buffer,0,needSize);
                } else {
                    os.write(buffer,0,len);
                    if(len < buffer.length){
                        break;
                    }
                }
                needSize -= buffer.length;
            }

            raFile.close();
            os.close();

            logger.info(">>>>>> download from server end >>>>>>");
        } catch (Exception e) {
            logger.error("<<<<<< download from server error <<<<<<" + e);
        } finally {
            //删除服务器临时文件
            DeleteFileUtil.delete(new File(filePath).getParent());
        }
    }
    /**
     * 断点下载
     * @param range
     * @param filePath
     * @param response
     * @param lastModified
     */
    public static void breakPointDownload2(HttpServletRequest request, String range, String filePath, HttpServletResponse response, String ETag, String lastModified) {
        String fileName = getFileName(filePath);
        File file = new File(filePath);
        Long fileLength = file.length();
        // 记录已下载文件大小
        long pastLength = 0;
        // 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
        int rangeSwitch = 0;
        // 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
        long toLength = 0;
        long contentLength = 0;// 客户端请求的字节总量
        // 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
        String rangeBytes = "";
        RandomAccessFile raf = null;
        OutputStream os = null;
        OutputStream out = null;
        byte b[] = new byte[4 * 1024]; // 暂存容器

        if (range != null) {
            logger.info("<<<<<< download from server start in {} byte ", range);
            System.out.println(range);

            rangeBytes = range.replaceAll("bytes=", "");
            int index = rangeBytes.indexOf('-');
            String str = rangeBytes.substring(rangeBytes.length() - 1, rangeBytes.length());
            int length = rangeBytes.length() - 1;
            if (index == 1 || "-".equals(str)) { // bytes=969998336-
                rangeSwitch = 1;
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                pastLength = Long.parseLong(temp0.trim());
                if ("".equals(temp2)) {
                    contentLength = fileLength - pastLength ;// 客户端请求的是 969998336 之后的字节
                } else {
                    toLength = Long.parseLong(temp2);// bytes=1275856879-1275877358，到第 1275877358 个字节结束
                    contentLength = toLength - pastLength + 1;// 客户端请求的是 1275856879-1275877358 之间的字节
                }
            } else {// bytes=1275856879-1275877358
                rangeSwitch = 2;
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                pastLength = Long.parseLong(temp0.trim());// bytes=1275856879-1275877358，从第 1275856879 个字节开始下载
                toLength = Long.parseLong(temp2);// bytes=1275856879-1275877358，到第 1275877358 个字节结束
                contentLength = toLength - pastLength + 1;// 客户端请求的是 1275856879-1275877358 之间的字节
            }
        } else {
            // 从开始进行下载, 客户端要求全文下载
            contentLength = fileLength;
        }

        // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。 响应的格式是: Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
        // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
        response.reset();
        if(range != null){
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
        }
        response.setHeader("Accept-Ranges", "bytes");// 如果是第一次下,还没有断点续传,状态是默认的
        if (pastLength != 0) {
            // 不是从最开始下载,
            // 响应的格式是: Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
            logger.info("----------------------------不是从开始进行下载！服务器即将开始断点续传...");
            switch (rangeSwitch) {
                case 1: {
                    // 针对 bytes=27000- 的请求
                    String contentRange = new StringBuffer("bytes ")
                            .append(new Long(pastLength).toString()).append("-")
                            .append(new Long(fileLength - 1).toString()).append("/")
                            .append(new Long(fileLength).toString()).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                case 2: {
                    // 针对 bytes=27000-39000 的请求
                    String contentRange = rangeBytes + "/" + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            // 是从开始下载
            logger.info("----------------------------是从开始进行下载！");
        }

        try {
            // response.setContentType(CommonUtil.setContentType(downloadFile.getName()));
            // set the MIME type.
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            String contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName;
            response.setHeader("Access-Control-Expose-Headers", "filename");
            response.setHeader("filename", encodedFileName);
            response.setHeader("Content-Disposition", contentDisposition);
            response.addHeader("Content-Length", String.valueOf(contentLength));
            response.addHeader("total", String.valueOf(fileLength));
            response.addHeader("ETag", ETag);
            response.addHeader("Last-modified", lastModified);
            // 获取文件后缀,判断是否为多媒体类型
            String fileType = DownloadUtil.getFileType(filePath);
            if(fileType != null && DownloadUtil.mediaType.contains(fileType.toLowerCase())) {
                response.setContentType("application/octet-stream");
            }
            os = response.getOutputStream();
            out = new BufferedOutputStream(os);
            raf = new RandomAccessFile(filePath, "r");
            try {
                switch (rangeSwitch) {
                    case 0: {
                        // 普通下载，或者从头开始的下载
                    }
                    case 1: {
                        // 针对 bytes=27000- 的请求
                        raf.seek(pastLength);// 形如 bytes=969998336- 的客户端请求，跳过 969998336 个字节
                        int n = 0;
                        while ((n = raf.read(b, 0, 1024)) != -1) {
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    case 2: {
                        // 针对 bytes=27000-39000 的请求
                        raf.seek(pastLength - 1);
                        // 形如 bytes=1275856879-1275877358 的客户端请求，找到第 1275856879 个字节
                        int n = 0;
                        // 记录已读字节数
                        long readLength = 0;
                        while (readLength <= contentLength - 1024) {
                            // 大部分字节在这里读取
                            n = raf.read(b, 0, 1024);
                            readLength += 1024;
                            out.write(b, 0, n);
                        }
                        if (readLength <= contentLength) {
                            // 余下的不足 1024 个字节在这里读取
                            n = raf.read(b, 0, (int) (contentLength - readLength));
                            out.write(b, 0, n);
                        }
                        // raf.seek(pastLength);//形如 bytes=1275856879-1275877358 的客户端请求，找到第 1275856879 个字节
                        // while (raf.getFilePointer() < toLength) {
                        // out.write(raf.read());
                        // }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                out.flush();
            } catch (IOException e) {
                logger.error("<<<<<< download from server error <<<<<<" + e);
                /**
                 * 在写数据的时候， 对于 ClientAbortException 之类的异常， 是因为客户端取消了下载，而服务器端继续向浏览器写入数据时，
                 * 抛出这个异常，这个是正常的。 尤其是对于迅雷这种吸血的客户端软件， 明明已经有一个线程在读取 bytes=1275856879-1275877358，
                 * 如果短时间内没有读取完毕，迅雷会再启第二个、第三个。。。线程来读取相同的字节段， 直到有一个线程读取完毕，迅雷会 KILL
                 * 掉其他正在下载同一字节段的线程， 强行中止字节读出，造成服务器抛 ClientAbortException。 所以，我们忽略这种异常
                 */
            }
        } catch (Exception e) {
            logger.error("<<<<<< download from server error <<<<<<" + e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //删除服务器临时文件
            DeleteFileUtil.delete(new File(filePath).getParent());
            logger.info(">>>>>> download from server end >>>>>>");
        }
    }

    private String getDownLoadFileName(HttpServletRequest request, String filename) {
        String new_filename = null;
        try {
            new_filename = URLEncoder.encode(filename, "UTF8").replace("+", "%20");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String userAgent = request.getHeader("User-Agent");
        // System.out.println(userAgent);
        String rtn = "filename=\"" + new_filename + "\"";
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            // IE浏览器，只能采用URLEncoder编码
            if (userAgent.indexOf("msie") != -1) {
                rtn = "filename=\"" + new_filename + "\"";
            }
            // Opera浏览器只能采用filename*
            else if (userAgent.indexOf("opera") != -1) {
                rtn = "filename*=UTF-8''" + new_filename;
            }
            // Safari浏览器，只能采用ISO编码的中文输出
            else if (userAgent.indexOf("safari") != -1) {
                try {
                    rtn = "filename=\""
                            + new String(filename.getBytes("UTF-8"),
                            "ISO8859-1") + "\"";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
            else if (userAgent.indexOf("applewebkit") != -1) {
                try {
                    new_filename = MimeUtility.encodeText(filename, "UTF8", "B");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                rtn = "filename=\"" + new_filename + "\"";
            }
            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            else if (userAgent.indexOf("mozilla") != -1) {
                rtn = "filename*=UTF-8''" + new_filename;
            }
        }
        return rtn;
    }

}
