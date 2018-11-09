/**
 * Copyright &copy; 2015-2020 <a href="http://www.wusyx.com/">wusyx</a> All rights reserved.
 */
package com.quarkdata.yunpan.api.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * ceph-s3工具类
 *
 * @author maorl
 * @date 2017-9-28
 */
public class S3Utils {

    private static final Logger logger = LoggerFactory.getLogger(S3Utils.class);

    /**
     * 获取S3连接
     *
     * @param accessKey accessKey
     * @param secretKey secretKey
     * @param host      host
     * @return connection
     */
    public static AmazonS3 getConnect(String accessKey, String secretKey, String host) {
        logger.info("S3 Util ---- get AmazonS connect");
        AmazonS3 connection = null;
        try {
            long startTime = System.currentTimeMillis();   //获取开始时间
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setSignerOverride("S3SignerType");
            clientConfig.setProtocol(Protocol.HTTP);
            connection = new AmazonS3Client(credentials, clientConfig);
            connection.setEndpoint(host);
            long endTime = System.currentTimeMillis(); //获取结束时间
            System.out.println("获取AmazonS3耗时： "+(endTime-startTime)+"ms");
        } catch (Exception e) {
            logger.error("s3 connection 获取失败" + e);
        }
        return connection;
    }

    /**
     * 创建Bucket
     *
     * @param bucketName bucketName
     * @param connection connection
     * @return bucket
     */
    public static Bucket createBucket(String bucketName, AmazonS3 connection) {
        Bucket b = null;
        try {
            if (connection.doesBucketExist(bucketName)) {
                logger.info("Bucket %s already exists :" + bucketName);
                b = getBucket(bucketName, connection);
            } else {
                b = connection.createBucket(bucketName);
            }
        } catch (AmazonS3Exception e) {
            logger.error("Bucket 创建失败" + e);
        }
        return b;
    }

    /**
     * 获取Bucket
     *
     * @param bucketName bucketName
     * @param connection connection
     * @return bucket
     */
    public static Bucket getBucket(String bucketName, AmazonS3 connection) {
        Bucket bucket = null;
        List<Bucket> buckets = connection.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucketName)) {
                bucket = b;
                logger.info("获取bucket：" + b.getName());
            }
        }
        return bucket;
    }

    /**
     * 获取bucket的name
     *
     * @return
     */
    public static String[] getBucketNames(AmazonS3 connection) {
        List<Bucket> buckets = connection.listBuckets();
        String[] names = new String[buckets.size()];
        for (int i = 0; i < buckets.size(); i++) {
            names[i] = buckets.get(i).getName();
        }
        return names;
    }

    /**
     * 上传文件
     *
     * @param bucketName bucketName
     * @param uploadFile uploadFile
     */
    public static void uploadFile(String bucketName, File uploadFile, AmazonS3 connection) {
        try {
            ObjectListing objects = connection.listObjects(bucketName, uploadFile.getName());
            /*判断是否已上传*/
            if (objects == null || objects.getObjectSummaries().size() == 0) {
                connection.putObject(bucketName, uploadFile.getName(), uploadFile);
            }
        } catch (AmazonS3Exception e) {
            logger.error("upload failed" + e);
        }

    }

    public static void uploadFile(String bucketName, String key, File uploadFile, AmazonS3 connection) {
        connection.putObject(bucketName, key, uploadFile);
    }

    /**
     * 获取url
     *
     * @param bucketName bucketName
     * @param fileName   fileName
     * @return url
     */
    public static String getUrl(String bucketName, String fileName, AmazonS3 connection) {
        try {
            GeneratePresignedUrlRequest httpRequest = new GeneratePresignedUrlRequest(bucketName, fileName);
            /*临时链接*/
            String url = connection.generatePresignedUrl(httpRequest).toString();
            String url1 = connection.getUrl(bucketName, fileName).toString();
            return url + "---" + url1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param bucketName bucketName
     * @param fileName   fileName
     */
    public static void delObject(String bucketName, String fileName, AmazonS3 connection) {
        try {
            connection.deleteObject(bucketName, fileName);
        } catch (AmazonServiceException e) {
            logger.error("delete failed" + e);
        }

    }

    /**
     * 通过S3下载
     *
     * @param bucketName
     * @param fileName
     * @param path
     */
    public static void downByS3(String bucketName, String fileName, String path, AmazonS3 connection) {
        try {
            GetObjectRequest request = new GetObjectRequest(bucketName, fileName);
            connection.getObject(request, new File(path));
        } catch (SdkClientException e) {
            e.printStackTrace();
        } finally {
            connection.shutdown();
        }
    }

    /**
     * 使用TransferManager上传文件
     *
     * @param filePath   filePath
     * @param bucketName bucketName
     * @param keyPrefix  keyPrefix
     * @param connection connection
     */
    public static void uploadFileByTransferManager(String filePath, String bucketName, String keyPrefix, AmazonS3 connection) {
        String keyName = null;
        String fileName = filePath.substring(filePath.lastIndexOf("/"));
        if (keyPrefix != null) {
            keyName = keyPrefix + fileName;
        } else {
            keyName = fileName;
        }

        File f = new File(filePath);
        TransferManager xferMgr = new TransferManager(connection);
        try {
            Upload xfer = xferMgr.upload(bucketName, keyName, f);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xferMgr.shutdownNow(false);
    }

    /**
     * 删除Bucket
     *
     * @param bucketName bucketName
     */
    public static void deleteBucket(String bucketName, AmazonS3 connection) {

        try {
            logger.info(" - removing objects from bucket");
            ObjectListing objectListing = connection.listObjects(bucketName);
            while (true) {
                for (Iterator<?> iterator = objectListing.getObjectSummaries().iterator(); iterator.hasNext(); ) {
                    S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
                    connection.deleteObject(bucketName, summary.getKey());
                }

                // more object_listing to retrieve?
                if (objectListing.isTruncated()) {
                    objectListing = connection.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }

            logger.info(" - removing versions from bucket");
            VersionListing versionListing = connection
                    .listVersions(new ListVersionsRequest().withBucketName(bucketName));
            while (true) {
                for (Iterator<?> iterator = versionListing.getVersionSummaries().iterator(); iterator.hasNext(); ) {
                    S3VersionSummary vs = (S3VersionSummary) iterator.next();
                    connection.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
                }

                if (versionListing.isTruncated()) {
                    versionListing = connection.listNextBatchOfVersions(versionListing);
                } else {
                    break;
                }
            }

            logger.info(" OK, bucket ready to delete!");
            connection.deleteBucket(bucketName);
        } catch (AmazonServiceException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
        logger.info("Done!");
    }

    /**
     * 获取bucket的ACl
     *
     * @param bucketName bucketName
     */
    public static void getBucketAcl(String bucketName, AmazonS3 connection) {
        try {
            AccessControlList acl = connection.getBucketAcl(bucketName);
            List<Grant> grants = acl.getGrantsAsList();
            for (Grant grant : grants) {
                System.out.format("  %s: %s\n", grant.getGrantee().getIdentifier(), grant.getPermission().toString());
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
    }

    public static void getObjectAcl(String bucketName, String objectKey, AmazonS3 connection) {
        try {
            AccessControlList acl = connection.getObjectAcl(bucketName, objectKey);
            List<Grant> grants = acl.getGrantsAsList();
            for (Grant grant : grants) {
                System.out.format("  %s: %s\n", grant.getGrantee().getIdentifier(), grant.getPermission().toString());
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    /**
     * 复制对象
     *
     * @param bucketName            bucketName
     * @param sourceKey             sourceKey
     * @param destinationBucketName destinationBucketName
     */
    public static void copyObject(String bucketName, String sourceKey, String destinationBucketName,
                                  String destinationKey, AmazonS3 connection) {
        try {
            connection.copyObject(bucketName, sourceKey, destinationBucketName, destinationKey);
        } catch (AmazonServiceException e) {
            logger.error("copy obj failed " + e);
        }
    }

    /**
     * 获取bucket中所有object
     *
     * @param bucketName bucketName
     */
    public static List<String> listObjects(String bucketName, AmazonS3 connection) {
        logger.info("Objects in S3 bucket :" + bucketName);
        List<String> keyList = new ArrayList<String>();
        ObjectListing ol = connection.listObjects(bucketName);
        List<S3ObjectSummary> objects = ol.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            keyList.add(os.getKey());
        }
        return keyList;
    }

    /**
     * 获取bucket中所有object
     *
     * @param bucketName bucketName
     * @param prefix     prefix
     * @return list
     */
    public static List<String> listObjects(String bucketName, String prefix, AmazonS3 connection) {

        logger.info("Objects in S3 bucket :" + bucketName);
        List<String> keyList = new ArrayList<>();
        ObjectListing ol = connection.listObjects(bucketName, prefix);
        List<S3ObjectSummary> objects = ol.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            keyList.add(os.getKey());
        }
        return keyList;
    }

    /**
     * 获取目录下所有内容
     *
     * @param bucketName bucketName
     * @param prefix     prefix
     */
    public static void showContent(String bucketName, String prefix, AmazonS3 connection) {
        ObjectListing objects = connection.listObjects(bucketName, prefix);
        do {
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                System.out.println(objectSummary.getKey() + "\t" + objectSummary.getSize() + "\t"
                        + objectSummary.getLastModified() + "\t" + objectSummary.getStorageClass());
            }
            objects = connection.listNextBatchOfObjects(objects);
        } while (objects.isTruncated());
    }

    /**
     * 创建文件夹
     *
     * @param bucketName bucketName
     * @param folderName folderName
     */
    public static void createFolder(String bucketName, String folderName, AmazonS3 connection) {
        // Create metadata for my folder & set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // Create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // Create a PutObjectRequest passing the foldername suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + "/", emptyContent, metadata);
        // Send request to S3 to create folder
        connection.putObject(putObjectRequest);
    }

    /**
     * 下载目录
     *
     * @param bucketName bucketName
     * @param keyPrefix  keyPrefix
     * @param dirPath    dirPath
     * @param connection connection
     */
    public static void downloadDir(String bucketName, String keyPrefix, String dirPath, AmazonS3 connection) {
        TransferManager xferMgr = new TransferManager(connection);
        try {
            MultipleFileDownload xfer = xferMgr.downloadDirectory(bucketName, keyPrefix, new File(dirPath));
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xferMgr.shutdownNow(false);
    }

    /**
     * 用TransferManager下载文件
     *
     * @param bucketName bucketName
     * @param keyName    keyName
     * @param filePath   filePath
     * @param connection connection
     */
    public static void downloadFileByTransferManager(String bucketName, String keyName, String filePath, AmazonS3 connection) {
        File f = new File(filePath);
        TransferManager xferMgr = new TransferManager(connection);
        try {
            Download xfer = xferMgr.download(bucketName, keyName, f);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xferMgr.shutdownNow(false);
    }


    /**
     * 分片上传
     *
     * @param bucketName bucketName
     * @param key        key
     * @param filePath   filePath
     * @param conn       conn
     */
    public static void upload(String bucketName, String key, String filePath, AmazonS3 conn) {
        HashMap<String, Object> map = ObjectToFile.readObject(key);
        if (map != null) {
            multipartUpload(bucketName, key, filePath, null, map, conn);
        } else {
            InitiateMultipartUploadResult initResponse = initiateMultipartUpload(bucketName, key, conn);
            multipartUpload(bucketName, key, filePath, initResponse, null, conn);
        }
    }

    /**
     * 分片上传
     *
     * @param bucketName   bucketName
     * @param key          key
     * @param filePath     filePath
     * @param initResponse initResponse
     * @param map          map
     */
    private static void multipartUpload(String bucketName, String key, String filePath, InitiateMultipartUploadResult initResponse, HashMap<String, Object> map, AmazonS3 conn) {
        logger.info("分片断点上传");
        File file = new File(filePath);
        long contentLength = file.length();
        List<PartETag> partETags;
        /**Set part size to 5 MB.**/
        long partSize = 5242880;
        String uploadId;
        long filePosition = 0;
        int partNum = 1;
        if (map != null) {
            partETags = (List<PartETag>) map.get("tagList");
            uploadId = (String) map.get("uploadId");
            filePosition = (long) map.get("position");
            partNum = (int) map.get("partNum") + 1;
        } else {
            partETags = new ArrayList<>();
            uploadId = initResponse.getUploadId();
        }
        Map<String, Object> map1 = new HashMap<String, Object>(16);
        try {
            for (int i = partNum; filePosition < contentLength; i++) {
                // Last part can be less than 5 MB. Adjust part size.
                partSize = Math.min(partSize, (contentLength - filePosition));

                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName).withKey(key)
                        .withUploadId(uploadId).withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                partETags.add(
                        conn.uploadPart(uploadRequest).getPartETag());
                filePosition += partSize;
                map1.put("tagList", partETags);
                map1.put("uploadId", uploadId);
                map1.put("position", filePosition);
                map1.put("partNum", i);
                ObjectToFile.writeObject((HashMap<String, Object>) map1, key);
                logger.debug(filePosition + "----" + i + "----" + partETags.size());
            }

            CompleteMultipartUploadRequest compRequest = new
                    CompleteMultipartUploadRequest(
                    bucketName,
                    key,
                    uploadId,
                    partETags);

            conn.completeMultipartUpload(compRequest);
            logger.info("ok");
        } catch (AmazonServiceException e) {
            conn.abortMultipartUpload(new AbortMultipartUploadRequest(
                    bucketName, key, uploadId));
            logger.error("上传失败", e);
        }
    }


    /**
     * 初始化上传请求
     *
     * @param bucketName bucketName
     * @param key        key
     * @param connection connection
     * @return InitiateMultipartUploadResult
     */
    private static InitiateMultipartUploadResult initiateMultipartUpload(String bucketName, String key, AmazonS3 connection) {
        InitiateMultipartUploadRequest initRequest = new
                InitiateMultipartUploadRequest(bucketName, key);
        return connection.initiateMultipartUpload(initRequest);
    }


    public static void main(String[] args) throws Exception {
        String accessKey = "I71GKWFFSRS83SG3UE8F";
        String secrectKey = "HmfqnAwr8bvxMxFN6brMBW7A4M2p9oqcp1Ncrzks";
        String host = "http://192.168.200.5:7480";
        //String host = "http://139.219.64.117:7480";
        AmazonS3 connection = getConnect(accessKey, secrectKey, host);
        String bucketName = "testbucket";
//		createBucket(bucketName,connection);
//		List<Bucket> buckets = connection.listBuckets();
//		System.out.println(buckets);
//
//		createBucket(bucketName,connection);
//

//
//        deleteBucket(bucketName,connection);
// upload(bucketName,"test355s","/home/user/pypy/bigone.zip",connection);
        showContent(bucketName, "", connection);
//          Test thread = new Test();
//          thread.start();
        downByS3(bucketName, "test355", "/home/user/xxxx.zip", connection);
//        System.out.println("开始");
//        Thread.sleep(5000);
//        thread.suspend();//获取此线程停止
//        System.out.println("休眠");
//        Thread.sleep(50000);
//        thread.resume();//获取线程继续
//        System.out.println("启动");
//         PropertiesLoader propertiesLoader = new PropertiesLoader("config/config.properties");
//		String a = propertiesLoader.getProperty("accessKey","22");
//		System.out.println("****"+a);
//		System.out.println(PropertiesUtils.prop.get("accessKey"));
//		 showContent(bucketName, null, connection);
//		Bucket bucket = getBucket(bucketName, connection);
//		 getBucketAcl(bucketName, connection);
//         getObjectAcl("bigsizefiles", "/hugeOne.zip", connection);
//		 System.out.println(getUrl("bigsizefiles", "/hugeOne.zip",
//		 connection));
//
//
    }

}