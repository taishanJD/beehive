package com.quarkdata.yunpan.api.service;


import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.FullTextVo;

import java.util.List;

/**
 * Created by liuda
 * Date : 2018/3/26
 */
public interface FullTextService {
    /**
     * 索引文件
     * @param id es文档的唯一表示
     * @param documentName 文件名
     * @param type 文件类型 个人文件 组织文件  归档文件
     * @param documentType 文件类型 doc txt...
     * @param content 文件内容
     */
    public void createDocumentIndex(String id,String documentId, String documentName, String type, String documentType, String content);

    /**
     * 查入失败的索引记录，以备后期添加
     * @param incId 租户ID
     * @param userId 用户ID
     * @param documentId 文档ID
     */
    void insertToFailList(long incId, long userId, String documentId, String documentType);


    void generateIndex(long incId);
    /**
     * 删除es中的一条记录
     * @param index 索引名
     * @param type 类型
     * @param id _id
     */
    public boolean deleteDocumentIndex(String index, String id);

    /**
     * 修改es中的一条记录
     * @param index 索引名
     * @param field 字段
     * @param id _id
     * @param modify 修改后的内容
     */
    public boolean updateDocumentIndex(String index, String field, String id, String modify);

    /**
     * 全文搜索
     * @param param 搜索关键字
     * @param pageNum 分页初始偏移量
     * @param pageSize 分页大小
     * @param esIndex 索引名称（quarkdata+组织id）
     * @param ids 有权限的文件id数组
     * @return
     */
    public ResultCode<FullTextVo> searchEs(String param, Integer pageNum, Integer pageSize, String esIndex, String[] ids, String type, String preTag, String postTag);


    public ResultCode<FullTextVo> searchOrgEs(Long incId, Long parentId, String param, Integer pageNum, Integer pageSize, String esIndex, List<DocumentExtend> ids, String type, String preTag, String postTag);


    /**
     *复制文件添加新索引
     * @param esIndex es索引  "quarkdata"+组织id
     * @param oldId
     * @param newId
     * @param type 变更后的文件类型 个人文件 组织文件  归档文件
     */
    public boolean copyEsFile(String esIndex, String oldId, String newId, String documentId, String type);

    /**
     * 获取个人文件id数组
     * @param incId
     * @param userId
     * @return
     */
    public String[] getPersonalFileIdList(Long incId, Long userId);

    public String[] getOrgFileIdList(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId,boolean isOrgAdmin);

    public String[] getExternalSpaceFileIdList(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId);

    List<DocumentExtend> getOrgFiles(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId,boolean isOrgAdmin);

    /**
     * 获取归档列表文件id集合
     * @param incId
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @return
     */
    public String[] getArchivalFileIdList(Long incId, Long userId, List<Long> userGroupIds, Long deptId);
    /**
     * 判断指定的索引名是否存在
     *
     * @param indexName
     *            索引名
     * @return 存在：true; 不存在：false;
     */
    public  boolean isExistsIndex(String indexName);

    /**
     * 新建索引库
     * 此为超级管理员设置项，慎用，只有当前企业开通全文检索才能使用
     * @param incId
     * @param shards
     * @param replicas
     * @return
     */
    public boolean createIndex(String incId, Integer shards, Integer replicas);

    /**
     * 删除索引库
     * 此为超级管理员设置项，慎用，只有当前企业不在使用全文检索才能使用
     * @param incId
     * @return
     */
    public ResultCode deleteIndex(String incId);
}
