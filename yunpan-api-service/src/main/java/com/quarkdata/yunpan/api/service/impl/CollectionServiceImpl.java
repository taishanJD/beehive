package com.quarkdata.yunpan.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.dataobj.CollectDocumentRel;
import com.quarkdata.yunpan.api.model.dataobj.CollectDocumentRelExample;
import com.quarkdata.yunpan.api.model.vo.CollectionVO;
import com.quarkdata.yunpan.api.service.CollectionService;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class CollectionServiceImpl extends BaseLogServiceImpl implements CollectionService{
	
	static Logger logger = Logger.getLogger(TestServiceImpl.class);
	
	@Autowired
	CollectDocumentRelMapper collectDocumentRelMapper;
	
	@Autowired
	CollectDocumentRelMapper2 collectDocumentRelMapper2;
	
	@Autowired
	private DocumentPermissionMapper2 documentPermissionMapper2;

	@Autowired
	private DocumentShareMapper2 documentShareMapper2;

	@Autowired
	private DocumentMapper documentMapper;

	@Autowired
	private DocumentPermissionService documentPermissionService;

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

	/**
	 * 添加收藏
	 */
	@Override
	public void addCollection(HttpServletRequest request, Long incId, Long userId, Long documentId) {
		// TODO Auto-generated method stub
		CollectDocumentRel collectDocumentRel = new CollectDocumentRel();
		collectDocumentRel.setDocumentId(documentId);
		collectDocumentRel.setUserId(userId);
		collectDocumentRel.setIncId(incId);
		collectDocumentRelMapper.insertSelective(collectDocumentRel);

		// 记录操作日志
		this.addDocumentLog(request, documentId.toString(), ActionType.COLLECTION,
				this.documentMapper.selectByPrimaryKey(documentId).getDocumentName(), new Date());
	}

	/**
	 * 取消收藏
	 */
	@Override
	public void deleteCollection(HttpServletRequest request, Long incId, Long userId, String documentIds) {
		// TODO Auto-generated method stub
		String[] docIdStr = documentIds.split(",");
		for (String dString:docIdStr) {
			CollectDocumentRelExample collectDocumentRelExample = new CollectDocumentRelExample();
			Long documentId = Long.valueOf(dString);
			collectDocumentRelExample.createCriteria().andDocumentIdEqualTo(documentId).andIncIdEqualTo(incId).andUserIdEqualTo(userId);
			collectDocumentRelMapper.deleteByExample(collectDocumentRelExample);

			// 记录操作日志
			this.addDocumentLog(request, documentId.toString(), ActionType.DELETE_COLLECTION,
					this.documentMapper.selectByPrimaryKey(documentId).getDocumentName(), new Date());
		}
		
	}

	/**
	 * 获取收藏列表,附标签信息
	 */
	@Override
	public List<Map<String,Object>> getCollectionList(Long userId, Long incId, Long parentId, String documentName, Integer pageNum, Integer pageSize, List<Long> userGroupIds, Long deptId) {
		// TODO Auto-generated method stub
		if(parentId != null && parentId == 0) {
			parentId = null;
		}
		if(StringUtils.isBlank(documentName)) {
			documentName = null;
		}
		Integer startNum = null;
		if(pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0 ) {
			startNum = (pageNum - 1) * pageSize;
		} else {
			pageSize = null;
		}
		List<CollectionVO> bundle = collectDocumentRelMapper2.getCollectionList(userId, incId, parentId, documentName, startNum, pageSize);
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>> ();
        ListIterator<CollectionVO> iterator = bundle.listIterator();
        while(iterator.hasNext()) {
            CollectionVO cVo = iterator.next();
            Map<String,Object> resultMap = new HashMap<> ();
            String jsonString = JSON.toJSONString(cVo);
            resultMap = JSON.parseObject(jsonString);
            String[] ids = cVo.getIdPath().split("/");
            StringBuffer stringBuffer = new StringBuffer();
            for (String idStr:ids) {
                if (StringUtils.isNotBlank(idStr)) {
                    Long id = Long.valueOf(idStr);
                    String docName = collectDocumentRelMapper2.getDocumentName(id);
                    stringBuffer.append("/" + docName);
                }
            }
            Map<String, Object> params = new HashMap<>();
            params.put("incId", incId);
            params.put("userId", userId);
            params.put("groupIds", userGroupIds);
            params.put("deptId", deptId);
            params.put("docId", cVo.getId());
            int isShared = this.documentShareMapper2.isSharedToMeWithoutMyExclude(params);
            String type = cVo.getType();
            if ("0".equals(type) && 0 == isShared) {
                String namePath = "/共享空间" + stringBuffer.toString();
                resultMap.put("namePath", namePath);
//				String permission = this.documentPermissionService.getPermission(cVo.getIdPath(), userId, userGroupIds, deptId);
                String permission = this.documentPermissionService.getPermissionByDocumentId(cVo.getId(), incId, userId, userGroupIds, deptId, UserInfoUtil.isSystemAdmin());
                if(permission != null) {
					resultMap.put("permission", permission);
					resultMap.put("source", "0");
					result.add(resultMap);
				}
            } else if ("1".equals(type) && 0 == isShared && cVo.getCreateUser().equals(userId)) {
                String namePath = "/个人文件" + stringBuffer.toString();
                resultMap.put("namePath", namePath);
                resultMap.put("permission", "1");
                resultMap.put("source", "1");
                result.add(resultMap);
            } else if ("1".equals(type) && 0 == isShared && !cVo.getCreateUser().equals(userId)) {
                iterator.remove();
            } else if (0 != isShared){
                String namePath = "/与我共享" + stringBuffer.toString();
                resultMap.put("namePath", namePath);
                List<String> permissionList = collectDocumentRelMapper2.getPermissionType(cVo.getId(), userId );
                if(permissionList.contains("1")){
                    resultMap.put("permission", "1");
                } else {
                    resultMap.put("permission", "0");
                }
                resultMap.put("source", "3");
                result.add(resultMap);
            }
        }
		return result;
	}
	
}
