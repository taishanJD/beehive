package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.DocumentEsIndexMapper;
import com.quarkdata.yunpan.api.dal.dao.DocumentEsIndexMapper2;
import com.quarkdata.yunpan.api.dal.dao.DocumentMapper2;
import com.quarkdata.yunpan.api.dal.dao.DocumentPermissionMapper;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.vo.DocumentEsIndexExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.FullTextVo;
import com.quarkdata.yunpan.api.model.vo.ResultEsVo;
import com.quarkdata.yunpan.api.service.DownloadService;
import com.quarkdata.yunpan.api.service.FullTextService;
import com.quarkdata.yunpan.api.service.OrganizedFileService;
import com.quarkdata.yunpan.api.util.DeleteFileUtil;
import com.quarkdata.yunpan.api.util.EsUtils;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.common.config.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by liuda
 * Date : 2018/3/26
 */
@Service
public class FullTextServiceImpl implements FullTextService {

    static Logger logger = LoggerFactory.getLogger(FullTextServiceImpl.class);

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private DocumentMapper2 documentMapper2;
    @Autowired
    DocumentEsIndexMapper documentEsIndexMapper;

    @Autowired
    DocumentEsIndexMapper2 documentEsIndexMapper2;
    private DocumentPermissionMapper documentPermissionMapper;
    @Autowired
    OrganizedFileService organizedFileService;
    @Override
    public void createDocumentIndex(String id, String documentId, String documentName,String type,String documentType, String content) {
        UserInfoVO user = null;
        try {
            user = UserInfoUtil.getUserInfoVO();
            long incId = user.getUser().getIncid();
            boolean isCompleted = createIndex(id, documentId, documentName, type, documentType, content, incId);
            if(!isCompleted){
                insertToFailList(user.getUser().getIncid(), user.getUser().getUserId(),id,documentType);
            }
        } catch (Exception e) {
            logger.error("es索引文件失败");
            e.printStackTrace();
        }
    }
    private boolean createIndex(String id, String documentId, String documentName,String type,String documentType, String content, long incId) {
        IndexResponse response = null;
        try {
            response = transportClient.prepareIndex(Global.getConfig("index.prefixion") + incId, "file", id)
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("documentId", documentId)
                            .field("documentType", documentType)
                            .field("type", type)
                            .field("title", documentName)
                            .field("content", content)
                            .endObject()
                    ).get();
        }catch (IOException e){
            logger.error("es索引文件失败");
            return false;
        }
        return true;
    }

    @Override
    public void insertToFailList(long incId, long userId, String documentId, String documentType){
        try {
            DocumentEsIndex index = new DocumentEsIndex();
            index.setIncId(incId);
            index.setCreateTime(new Date());
            index.setCreateUserId(userId);
            index.setDocumentType(documentType);
            index.setDocumentVersionId(Long.parseLong(documentId));
            documentEsIndexMapper.insertSelective(index);
        }catch (Exception e){
            logger.error("es索引文件失败", e.toString());
        }

    }

    @Override
    public void generateIndex(long incId) {
        if (this.isExistsIndex(Global.getConfig("index.prefixion")+incId)) {
            List<DocumentEsIndexExtend> documents = documentEsIndexMapper2.selectDocumentIndexByVersionId(incId);
            for (DocumentEsIndexExtend document : documents) {
                try {
                    if (document.getDocumentType().equals("dir")) {
                        createIndex("dir_" + document.getDocumentId().toString(), document.getDocumentId().toString(),
                                document.getDocumentName(), document.getType(), document.getDocumentType(), null, incId);
                    } else {
                        String filePath = downloadService.downloadFile(new Long(document.getDocumentVersionId()));
                        InputStream input = new FileInputStream(filePath);
                        String content = EsUtils.getContent(input, document.getDocumentType());
                        input.close();
                        DeleteFileUtil.delete(filePath);
                        this.createIndex(String.valueOf(document.getDocumentVersionId()), String.valueOf(document.getDocumentId()),
                                document.getDocumentName(), document.getType(), document.getDocumentType(), content, incId);
                    }
                    documentEsIndexMapper.deleteByPrimaryKey(document.getIndexId());
                } catch (Exception e) {
                    logger.error(e.toString());
                }
            }
        }
    }

    @Override
    public boolean deleteDocumentIndex(String index, String id) {
        try {
            if (isExistsIndex(index)){
                DeleteResponse response =transportClient.prepareDelete(index,"file",id).get();
                RestStatus status = response.status();
                if ("OK".equals(String.valueOf(status))){
                    logger.info("删除成功");
                }else {
                    logger.info("文档不存在");
                }
            }else {
                logger.info("索引不存在");
            }
            return true;
            //System.out.println(response.status()); //OK表示成功
        }catch (Exception e){
            logger.error("es删除记录失败");
            return false;
        }
    }

    @Override
    public boolean updateDocumentIndex(String index,String filed,String id, String modify) {
        try {
            transportClient.prepareUpdate(index,"file",id)
                    .setDoc(jsonBuilder()
                            .startObject()
                            .field(filed, modify)
                            .endObject())
                    .get();
            logger.info("修改文件名成功");
            return true;
        }catch (DocumentMissingException e1){
            logger.error("es索引中未找到文档，请查看索引或者文档是否存在");
            return false;
        } catch (Exception e) {
            logger.error("修改es中的一条记录 标题修改失败");
            return false;
        }
    }

    @Override
    public ResultCode<FullTextVo> searchEs(String param, Integer pageNum, Integer pageSize, String esIndex, String[] ids, String type, String preTag, String postTag) {
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags(preTag)
                .postTags(postTag)
                .field("content", 70)
                .field("title");
        if(type.equals("0") || type.equals("3")){
            //return getOrgFiles(param, pageNum, pageSize, esIndex, ids, hiBuilder);
        }else{
        }
        return getPersonalFiles(param, pageNum, pageSize, esIndex, ids, hiBuilder);
    }

    @Override
    public ResultCode<FullTextVo> searchOrgEs(Long incId,  Long parentId, String param, Integer pageNum, Integer pageSize, String esIndex, List<DocumentExtend> documents, String type, String preTag, String postTag) {
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags(preTag)
                .postTags(postTag)
                .field("content", 70)
                .field("title");
            List<String> docIds = getDocIdList(documents, parentId);
            String condition = StringUtils.join(docIds, ',');
            List<String> orgIds = documentMapper2.getVersionIdsForFullText(incId, condition);
            return getOrgFiles(param, pageNum, pageSize, esIndex, listToArray(orgIds), hiBuilder, documents);

    }

    private ResultCode<FullTextVo> getPersonalFiles(String param, Integer pageNum, Integer pageSize, String esIndex, String[] ids, HighlightBuilder hiBuilder){
        ResultCode<FullTextVo> resultCode =null;
        ArrayList<ResultEsVo> resultEsVos = new ArrayList<>();
        try {
            resultCode = new ResultCode<>();
            FullTextVo fullTextVo = new FullTextVo();
            //设置搜索条件
            QueryBuilder qb2 = QueryBuilders.multiMatchQuery(param, "title", "content");
            //设置高亮

            SearchResponse response = transportClient
                    .prepareSearch(esIndex)
                    .setTypes("file")
                    .setQuery(qb2)
                    .setPostFilter(QueryBuilders.boolQuery())
                    .setPostFilter(QueryBuilders.idsQuery().addIds(ids))
                    .highlighter(hiBuilder)
                    .setFrom((pageNum - 1) * pageSize)
                    .setSize(pageSize)
                    // 设置是否按查询匹配度排序
                    .setExplain(true)
                    .execute()
                    .actionGet();
            //获取查询结果集
            SearchHits searchHits = response.getHits();
            fullTextVo = new FullTextVo();
            //遍历封装结果
            for (SearchHit hit : searchHits) {
                String documentId = hit.getSourceAsMap().get("documentId").toString();
                String versionId = hit.getId();
                ResultEsVo resultEsVo = new ResultEsVo(documentMapper2.getDocumentById(documentId));
                resultEsVo.setDocumentVersionId(versionId);
                String namePath = getNamePath(resultEsVo.getIdPath());
                resultEsVo.setNamePath(namePath);
                if (hit.getHighlightFields().containsKey("title")) {
                    String title = String.valueOf(hit.getHighlightFields().get("title").getFragments()[0]);
                    resultEsVo.setDocumentName(title);
                } else {
                    resultEsVo.setDocumentName(hit.getSourceAsMap().get("title").toString());
                }
                if (hit.getHighlightFields().containsKey("content")) {
                    String content = String.valueOf(hit.getHighlightFields().get("content").getFragments()[0]);
                    resultEsVo.setContent(content);
                }
                resultEsVos.add(resultEsVo);
            }
            fullTextVo.setResultSize(String.valueOf(searchHits.totalHits));
            fullTextVo.setResultEsVoList(resultEsVos);
            resultCode.setData(fullTextVo);
        }catch (IndexNotFoundException e1){
            logger.error(Messages.API_FULLTEXT_NOINDEX_MSG+e1);
            resultCode = ResultUtil.error(Messages.API_FULLTEXT_NOINDEX_CODE, Messages.API_FULLTEXT_NOINDEX_MSG);
        }catch (Exception e){
            resultCode = ResultUtil.error(Messages.API_FULLTEXT_SEARCH_CODE, Messages.API_FULLTEXT_SEARCH_MSG);
            logger.error(Messages.API_FULLTEXT_SEARCH_MSG+e);
        }
        return resultCode;
    }

    private ResultCode<FullTextVo> getOrgFiles(String param, Integer pageNum, Integer pageSize, String esIndex, String[] ids, HighlightBuilder hiBuilder, List<DocumentExtend> docs){
        ArrayList<String> docIds = new ArrayList<String>();
        ArrayList<String> versionIds = new ArrayList<>();
        ResultCode<FullTextVo> resultCode =null;
        ArrayList<ResultEsVo> resultEsVos = new ArrayList<>();
        try {
            resultCode = new ResultCode<>();
            FullTextVo fullTextVo = new FullTextVo();
            FilterAggregationBuilder docAgg = AggregationBuilders
                    .filter("idFilter", QueryBuilders.idsQuery().addIds(ids))
                    .subAggregation(
                            AggregationBuilders.terms("documentCount")
                                    .field("documentId")
                                    .size(pageNum * pageSize)
                                    .subAggregation(AggregationBuilders.topHits("top").size(1).highlighter(hiBuilder)));
            //设置搜索条件
            QueryBuilder qb2 = QueryBuilders.multiMatchQuery(param, "title", "content");

            SearchResponse response = transportClient
                    .prepareSearch(esIndex)
                    .setTypes("file")
                    .setQuery(qb2)
                    .setPostFilter(QueryBuilders.idsQuery().addIds(ids))
                    .addAggregation(docAgg)
                    // 设置是否按查询匹配度排序
                    .setExplain(true)
                    .execute()
                    .actionGet();
            InternalFilter filter = response.getAggregations().get("idFilter");
            Terms agg = filter.getAggregations().get("documentCount");
            long totalDocCount = agg.getBuckets().size() + agg.getSumOfOtherDocCounts();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = startIndex + pageSize;
            if(endIndex > totalDocCount){
                endIndex = (int)totalDocCount;
            }
            Map<Long, DocumentExtend> docMap = new HashMap<Long, DocumentExtend>();
            for(DocumentExtend doc : docs){
                docMap.put(doc.getId(), doc);
            }
            for (Terms.Bucket entry : agg.getBuckets().subList(startIndex, endIndex)) {
                TopHits topHits= entry.getAggregations().get("top");

                for (SearchHit hit: topHits.getHits()) {
                    String versionId = hit.getId();
                    String documentId = hit.getSourceAsMap().get("documentId").toString();
                    DocumentExtend doc = docMap.get(Long.parseLong(documentId));
                    ResultEsVo resultEsVo = new ResultEsVo(doc);
                    resultEsVo.setDocumentVersionId(versionId);
                    String versionStr = documentMapper2.getVersionById(versionId);
                    resultEsVo.setVersionStr(versionStr);
                    String namePath = getNamePath(resultEsVo.getIdPath());
                    resultEsVo.setNamePath(namePath);
                    if (hit.getHighlightFields().containsKey("title")) {
                        String title = String.valueOf(hit.getHighlightFields().get("title").getFragments()[0]);
                        resultEsVo.setDocumentName(title);
                    } else {
                        resultEsVo.setDocumentName(hit.getSourceAsMap().get("title").toString());
                    }
                    if (hit.getHighlightFields().containsKey("content")) {
                        String content = String.valueOf(hit.getHighlightFields().get("content").getFragments()[0]);
                        resultEsVo.setContent(content);
                    }
                    resultEsVos.add(resultEsVo);
                }
            }
            fullTextVo.setResultSize(String.valueOf(totalDocCount));
            fullTextVo.setResultEsVoList(resultEsVos);
            resultCode.setData(fullTextVo);
        }catch (IndexNotFoundException e1){
            logger.error(Messages.API_FULLTEXT_NOINDEX_MSG+e1);
            resultCode = ResultUtil.error(Messages.API_FULLTEXT_NOINDEX_CODE, Messages.API_FULLTEXT_NOINDEX_MSG);
        }catch (Exception e){
            resultCode = ResultUtil.error(Messages.API_FULLTEXT_SEARCH_CODE, Messages.API_FULLTEXT_SEARCH_MSG);
            logger.error(Messages.API_FULLTEXT_SEARCH_MSG+e);
        }
        return resultCode;
    }

    private String getNamePath(String idPath){
        String namePath = "/";
        List<String> ids = Arrays.asList(idPath.split("/"));
        List<String> names = documentMapper2.getNamesByIds( Arrays.asList(idPath.split("/")),StringUtils.join(ids, ',') );
        for(String name : names){
            namePath += name + "/";
        }
        return namePath;
    }

    @Override
    public boolean copyEsFile(String esIndex, String oldId, String newId,String documentId,String type) {
        try {
            GetResponse response = transportClient.prepareGet(esIndex, "file", oldId).get();
            if (response.isExists()){
                Map<String, Object> sourceAsMap = response.getSourceAsMap();
                createDocumentIndex(newId,documentId,sourceAsMap.get("title").toString(),type,sourceAsMap.get("documentType").toString(),sourceAsMap.get("content").toString());
                return true;
            }else {
                logger.info("原文件没有建立es索引");
                return false;
            }
        }catch (IndexNotFoundException e1) {
            logger.error(Messages.API_FULLTEXT_NOINDEX_MSG + e1);
            return false;
        }catch (Exception e){
            logger.error("复制es索引出错");
            return false;
        }
    }

    @Override
    public String[] getPersonalFileIdList(Long incId, Long userId) {
        List<String> personalFileIdList = documentMapper2.getPersonalFileIdList(incId, userId);
        return listToArray(personalFileIdList);
    }

    @Override
    public String[] getOrgFileIdList(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId, boolean isOrgAdmin) {
        List<DocumentExtend> documents = organizedFileService.getAllChildren(incId, userId, userGroupIds, deptId, parentId).getData();
        List<String> docIds = getDocIdList(documents, parentId);
        String condition = StringUtils.join(docIds, ',');
        List<String> orgIds = documentMapper2.getVersionIdsForFullText(incId, condition);
        return listToArray(orgIds);
    }

    @Override
    public String[] getExternalSpaceFileIdList(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId) {
        List<String> orgFileIdList = documentMapper2.getShareSpaceFileIdList(incId, userId, userGroupIds, deptId, parentId,3);
        return listToArray(orgFileIdList);
    }

    @Override
    public List<DocumentExtend>  getOrgFiles(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId, boolean isOrgAdmin) {
        List<DocumentExtend> orgFileIdList = organizedFileService.getAllChildren(incId, userId, userGroupIds, deptId, parentId).getData();
        return orgFileIdList;
    }
    @Override
    public String[] getArchivalFileIdList(Long incId, Long userId, List<Long> userGroupIds, Long deptId) {
        List<String> archivalFileIdList = documentMapper2.getArchivalFileIdList(incId, userId, userGroupIds, deptId);
        return listToArray(archivalFileIdList);
    }

    public List<String> getDocIdList(List<DocumentExtend> documents, Long parentId){
        List<String> strings = new ArrayList<>();
        if (documents!=null&&documents.size()>0){
            for (int i=0;i<documents.size();i++){
                if(!documents.get(i).getId().equals(parentId))
                    strings.add(documents.get(i).getId().toString());
            }
            return strings;
        }else {
            return strings;
        }
    }

    public String[] listToArray(List<String> list){
        if (list!=null&&list.size()>0){
            String[] strings = new String[list.size()];
            for (int i=0;i<list.size();i++){
                strings[i]=list.get(i).toString();
            }
            return strings;
        }else {
            return new String[0];
        }
    }

    @Override
    public  boolean isExistsIndex(String indexName) {
        try {
            IndicesExistsResponse response = transportClient
                    .admin()
                    .indices()
                    .exists(new IndicesExistsRequest()
                            .indices(new String[]{indexName})).actionGet();
            return response.isExists();
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean createIndex(String incId, Integer shards, Integer replicas) {
        try{
            Settings settings= Settings.builder().put("number_of_shards",shards)
                    .put("number_of_replicas", replicas).build();
            transportClient.admin().indices().prepareCreate(Global.getConfig("index.prefixion")+incId).setSettings(settings).execute().actionGet();  //创建一个空索引
            logger.info("创建一个空的索引成功");
            if (isExistsIndex(Global.getConfig("index.prefixion")+incId)){
                XContentBuilder builder = XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject("file")
                        .startObject("properties");
                builder.startObject("documentId")
                        .field("type", "string")
                        .field("index","not_analyzed")
                        .endObject();
                //搜索字段: text类型， ik_max_word分词
                builder.startObject("title")
                        .field("type", "text")
                        .field("analyzer", "ik_max_word")
                        .field("search_analyzer", "ik_max_word")
                        .field("store", "no")
                        .field("term_vector", "with_positions_offsets")
                        .field("boost", 5)
                        .endObject();
                builder.startObject("content")
                        .field("type", "text")
                        .field("store", "no")
                        .field("term_vector", "with_positions_offsets")
                        .field("analyzer", "ik_max_word")
                        .field("search_analyzer", "ik_max_word")
                        .endObject();
                builder.startObject("documentType")
                        .field("type", "string")
                        .field("index","not_analyzed")
                        .endObject();
                builder.startObject("type")
                        .field("type", "string")
                        .field("index","not_analyzed")
                        .endObject();
                //结束
                builder.endObject().endObject().endObject();
                PutMappingRequest mapping = Requests.putMappingRequest(Global.getConfig("index.prefixion")+incId)
                        .type("file").source(builder);
                transportClient.admin().indices().putMapping(mapping).actionGet();
                logger.info("向空索引中添加映射成功");
                return true;
            }else {
                logger.error("创建一个空索引失败请重新建立索引");
                return false;
            }
        }catch (Exception e){
            logger.error("创建一个完整索引失败"+e);
            return false;
        }
    }

    @Override
    public ResultCode deleteIndex(String incId) {
        ResultCode<Object> resultCode = new ResultCode<>();
        try {
            if (isExistsIndex(Global.getConfig("index.prefixion")+incId)){
                DeleteIndexResponse dResponse = transportClient.admin().indices().prepareDelete(Global.getConfig("index.prefixion")+incId)
                        .execute().actionGet();
                resultCode= ResultUtil.success();
                logger.info("删除索引成功");
            }else {
                resultCode= ResultUtil.error(Messages.API_FULLTEXT_NOINDEX_CODE, Messages.API_FULLTEXT_NOINDEX_MSG);
                logger.error("删除索引失败，索引不存在");
            }
        }catch (Exception e){
            logger.error("删除索引失败");
            resultCode= ResultUtil.error(Messages.API_FULLTEXT_NOINDEX_CODE, Messages.API_FULLTEXT_NOINDEX_MSG);
        }
        return resultCode;
    }
}
