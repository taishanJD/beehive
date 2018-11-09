package com.quarkdata.yunpan.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.service.DownloadService;
import com.quarkdata.yunpan.api.util.DeleteFileUtil;
import com.quarkdata.yunpan.api.util.DownloadUtil;
import com.quarkdata.yunpan.api.util.S3Utils;
import com.quarkdata.yunpan.api.util.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author maorl
 * @date 12/12/17.
 */
@Service
public class DownloadServiceImpl extends BaseLogServiceImpl implements DownloadService {
    static Logger logger = LoggerFactory.getLogger(DownloadServiceImpl.class);

    @Value("${tempPath}")
    private String serverDownloadPath;
    @Autowired
    DocumentVersionMapper documentVersionMapper;
    @Autowired
    DocumentMapper documentMapper;
    @Autowired
    DocumentVersionMapper2 documentVersionMapper2;
    @Autowired
    DocumentMapper2 documentMapper2;
    @Autowired
    IncConfigMapper incConfigMapper;
    @Autowired
    ExternalLinkMapper externalLinkMapper;

    private LogMapper logMapper;

    private LogDocumentRelMapper logDocumentRelMapper;

    @Resource
    public void setLogMapper(LogMapper logMapper) {
        this.logMapper = logMapper;
        super.setLogMapper(logMapper);
    }

    @Resource
    public void setLogDocumentRelMapper(LogDocumentRelMapper logDocumentRelMapper) {
        this.logDocumentRelMapper = logDocumentRelMapper;
        super.setLogDocumentRelMapper(logDocumentRelMapper);
    }

    @Override
    public String getUrlsByDocId(List<String> ids, Integer incId) {
        String[] documentIds = ids.toArray(new String[0]);

        String temFolder = "/" + UUID.randomUUID().toString().replace("-", "");
        String url = null;
        try {
            url = perDownload(documentIds, temFolder);
        } catch (Exception e) {
            logger.error("文件下载异常", e);
            DeleteFileUtil.delete(serverDownloadPath + temFolder);
        }

        return url;
    }

    @Override
    public String getUrlByVersion(String versionId, Integer incId) {
        DocumentVersion documentVersion = documentVersionMapper.selectByPrimaryKey(Long.valueOf(versionId));
        String url = singleDownload(documentVersion);
        return url;
    }

    /**
     * 将文件从集群下载到服务器上
     *
     * @param versions
     * @param path
     * @return
     */
    private String downloadToServer(DocumentVersion versions, String path) {
        logger.info("将文件从集群下载到服务器上");
        String bucket = versions.getCephBucket();
        String fileKey = versions.getCephBucketKey();
        String realPath = serverDownloadPath + path;
        try {
            S3Utils.downByS3(bucket, fileKey, realPath, this.getConnectionByIncId(versions.getIncId().intValue()));
        } catch (Exception e) {
            logger.error("S3下载异常", e);
            throw e;
        }
        logger.info("从集群上下载完成");
        return realPath;
    }

    /**
     * 判断是单个下载还是批量打包下载
     *
     * @param docIds
     * @return
     */
    private String perDownload(String[] docIds, String tempFolder) throws IOException {
        logger.info("判断是单个下载还是批量打包下载");
        if (docIds == null) {
            return null;
        }


        if (docIds.length == 1) {
            Long docIdVal = Long.valueOf(docIds[0]);
            Document document = documentMapper.selectByPrimaryKey(docIdVal);
            if ("dir".equals(document.getDocumentType())) {
                //下载文件夹
                return traverseFolder(docIds, tempFolder);
            } else {
                //下载单个文件
                return singleDownload(document, tempFolder);
            }
        } else {
            return traverseFolder(docIds, tempFolder);
        }

    }

    /**
     * 单文件下载
     *
     * @param document
     * @return
     */
    private String singleDownload(Document document, String temFolder) {
        logger.info("单文件下载");
        DocumentVersion documentVersion = documentVersionMapper.selectByPrimaryKey(document.getDocumentVersionId());
        String fileName = "/" + document.getDocumentName();
        return downloadToServer(documentVersion, temFolder + fileName);
    }


    /**
     * 单文件版本下载
     *
     * @param version
     * @return
     */
    private String singleDownload(DocumentVersion version) {
        logger.info("单文件版本下载");
        String path;
        String temFolder = UUID.randomUUID().toString().substring(0, 13);
        String fileName = "/" + "(V-" + version.getVersion() + ".0)" + documentVersionMapper2.getFileNameByVersionId(version.getId());
        path = downloadToServer(version, "/" + temFolder + fileName);
        return path;

    }

    /**
     * 处理批量下载
     *
     * @param docIds
     * @return
     */
    private String traverseFolder(String[] docIds, String tempFolder) throws IOException {
        logger.info("处理批量下载");
        String downloadFolder = tempFolder + "/download";
        String zipName = null;
        for (String docId : docIds) {
            Long docIdVal = Long.valueOf(docId);
            Document document = documentMapper.selectByPrimaryKey(docIdVal);
            if (zipName == null) {
                zipName = document.getDocumentName();
            }
            //下载文件夹
            if ("dir".equals(document.getDocumentType())) {
                DocumentExample documentExample = new DocumentExample();
                documentExample.createCriteria().
                        andIdPathLike("%/" + docIdVal + "/%").
                        andIsDeleteEqualTo("0");

                List<Document> documents = documentMapper.selectByExample(documentExample);
                downloadFolder(documents, docId, downloadFolder);
            } else {
                downloadToServer(documentVersionMapper.selectByPrimaryKey(document.getDocumentVersionId()),
                        downloadFolder + "/" + document.getDocumentName());
            }
        }

        if (docIds.length == 1) {
            //压缩单个文件夹
            zipName = zipName + ".zip";
        } else {
            //压缩多个文件或文件夹
            zipName = "[批量下载]" + zipName + "等.zip";
        }
        zipName = serverDownloadPath + tempFolder + "/" + zipName;
        ZipUtils.doCompress(serverDownloadPath + downloadFolder, zipName);
        return zipName;
    }

    /**
     * 下载文件夹
     *
     * @param documents
     * @param perDocId    父文件夹id
     * @param batchFolder
     */
    private void downloadFolder(List<Document> documents, String perDocId, String batchFolder) {
        logger.info("下载文件夹");
        for (Document document :
                documents) {
            //获取当前目录
            String idPath = document.getIdPath();
            int index = idPath.indexOf(perDocId);
            String currentPath = "/" + idPath.substring(index);
            String namePath = batchFolder + idToNamePath(currentPath);
            if (!"dir".equals(document.getDocumentType())) {
                DocumentVersion version = documentVersionMapper.selectByPrimaryKey(document.getDocumentVersionId());
                downloadToServer(version, namePath);
            }else {
                String dirName = document.getDocumentName();
                File file = new File(serverDownloadPath + namePath);
                boolean mkdirs = file.mkdirs();
                if (mkdirs) {
                    logger.info("创建文件夹成功" + serverDownloadPath + namePath);
                } else {
                    logger.error("创建文件夹失败" + dirName);
                }
            }
        }
    }

    /**
     * 将id路径转化为实际路径
     *
     * @param idPath
     * @return
     */
    private String idToNamePath(String idPath) {
        String[] names = idPath.split("/");
        StringBuilder sb = new StringBuilder();
        for (String name :
                names) {
            if ("".equals(name)) {
                continue;
            }
            Document document = documentMapper.selectByPrimaryKey(Long.valueOf(name));
            sb.append("/").append(document.getDocumentName());
        }
        return sb.toString();
    }

    /**
     * 通过id获取文件名
     *
     * @param ids
     * @return
     */
    @Override
    public List<String> getDocNameById(List<String> ids) {
        List<String> names = documentMapper2.getNamesByIds(ids, null);
        return names;
    }

    @Override
    public Long getIncIdByCode(String code) {
        ExternalLinkExample example = new ExternalLinkExample();
        example.createCriteria().andExternalLinkCodeEqualTo(code);
        List<ExternalLink> externalLinks = externalLinkMapper.selectByExample(example);
        ExternalLink link = externalLinks.get(0);
        return link.getIncId();
    }

    @Override
    public void download(HttpServletRequest request, String filePath, HttpServletResponse response, List<String> documentIds, String ETag, String lastModified) {
        String range = request.getHeader("Range");
        DownloadUtil.breakPointDownload2(request, range, filePath, response, ETag, lastModified);
        // 记录文档下载日志
        for (String docId: documentIds) {
            Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
            this.addDocumentLog(request, docId, ActionType.DOWNLOAD, document.getDocumentName(), new Date());
        }
    }

    @Override
    public String downloadFile(long documentVersionId) {
        DocumentVersion version = this.documentVersionMapper.selectByPrimaryKey(documentVersionId);
        return  singleDownload(version);
    }

    private AmazonS3 getConnectionByIncId(Integer incId) {
        IncConfigExample incConfig = new IncConfigExample();
        incConfig.createCriteria().andIncIdEqualTo(incId.longValue());
        List<IncConfig> incConfigs = incConfigMapper.selectByExample(incConfig);
        IncConfig incConfig1 = incConfigs.get(0);
        String accessKey = incConfig1.getCephAccessKey();
        String secretKey = incConfig1.getCephSecretKey();
        String host = incConfig1.getCephUrl();
        return S3Utils.getConnect(accessKey, secretKey, host);
    }
}
