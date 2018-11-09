package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.vo.TagVO;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.TagService;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class TagServiceImpl extends BaseLogServiceImpl implements TagService {

	@Autowired
	TagMapper tagMapper;

	@Autowired
	TagMapper2 tagMapper2;
	
	@Autowired
	TagDocumentRelMapper tagDocumentRelMapper;
	
	@Autowired
	TagDocumentRelMapper2 tagDocumentRelMapper2;

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
	 * 标签库中添加标签
	 */
	@Override
	public Long addTag(HttpServletRequest request, Long incId, Long userId, String tagName) {
		// TODO Auto-generated method stub
		Tag tag = new Tag();
		tag.setIncId(incId);
		tag.setCreateUser(userId);
		tag.setTagName(tagName);
		tag.setCreateTime(new Date());
		tagMapper.insertSelective(tag);
		return tag.getId();
	}
	
	/**
	 * 文档添加已有标签
	 */
	@Override
	public void addDocTag(HttpServletRequest request, Long incId, Long userId, Long tagId, Long docId) {
		// TODO Auto-generated method stub
		TagDocumentRel tdr = new TagDocumentRel();
		tdr.setIncId(incId);
		tdr.setDocumentId(docId);
		tdr.setTagId(tagId);
		tdr.setCreateUser(userId);
		tdr.setCreateTime(new Date());
		tagDocumentRelMapper.insertSelective(tdr);

		// 记录操作日志
		Tag tag = this.tagMapper.selectByPrimaryKey(tdr.getTagId());
		Document document = this.documentMapper.selectByPrimaryKey(tdr.getDocumentId());
		this.addDocumentLog(request, document.getId().toString(), ActionType.ADD_TAG, document.getDocumentName() + "(" + tag.getTagName() + ")", tdr.getCreateTime());
	}

	/**
	 * 删除标签库中的标签
	 */
	@Override
	public void deleteTag(Long incId, Long userId, Long tagId) {
		// TODO Auto-generated method stub
//		TagExample te = new TagExample();
//		te.createCriteria().andIncIdEqualTo(incId).andCreateUserEqualTo(userId).andIdEqualTo(tagId);
//		tagMapper.deleteByExample(te);
		//tag表is_delete字段改为1
		tagMapper2.delTagByTagId(incId,userId,tagId);
		//doc_tag表删除所有有关该tagId的记录
		TagDocumentRelExample tdre = new TagDocumentRelExample();
		tdre.createCriteria().andIncIdEqualTo(incId).andCreateUserEqualTo(userId).andTagIdEqualTo(tagId);
		tagDocumentRelMapper.deleteByExample(tdre);
	}
	
	/**
	 * 删除文件标签
	 */
	@Override
	public void deleteDocTag(HttpServletRequest request, Long incId, Long userId, Long tagId, Long docId) {
		// TODO Auto-generated method stub
		TagDocumentRelExample tdre = new TagDocumentRelExample();
		tdre.createCriteria().andIncIdEqualTo(incId).andCreateUserEqualTo(userId).andTagIdEqualTo(tagId).andDocumentIdEqualTo(docId);
		tagDocumentRelMapper.deleteByExample(tdre);

		// 记录操作日志
		Tag tag = this.tagMapper.selectByPrimaryKey(tagId);
		Document document = this.documentMapper.selectByPrimaryKey(docId);
		this.addDocumentLog(request, document.getId().toString(), ActionType.DELETE_TAG, document.getDocumentName() + "(" + tag.getTagName() + ")", new Date());

	}


	@Override
	public List<Tag> getTopTenTagList(Long incId, Long userId) {
		List<Long> topTenTagIdList = tagDocumentRelMapper2.getTopTenTagIdList(incId, userId);
		if (null == topTenTagIdList || topTenTagIdList.isEmpty()) {
			return null;
		}
		return tagMapper2.getTopTenTagList(incId, userId, topTenTagIdList);
	}

	@Override
	public List<Tag> getAllTagList(Long incId, Long userId,String filter) {
		return tagMapper2.getAllTags(incId, userId,filter);
	}

	@Override
	public List<TagVO> getDocListByTagId(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long tagId,String filter) {
		List<TagVO> docListByTagId = tagDocumentRelMapper2.getDocListByTagId(incId, userId, tagId, filter);
		// 根据权限过滤数据
        ListIterator<TagVO> tagVOListIterator = docListByTagId.listIterator();
        while(tagVOListIterator.hasNext()) {
            TagVO tagVO = tagVOListIterator.next();
            String[] ids = tagVO.getIdPath().split("/");
            StringBuffer sb = new StringBuffer();
            for (String id: ids) {
                if (StringUtils.isNotBlank(id)) {
                    Document document = this.documentMapper.selectByPrimaryKey(Long.valueOf(id));
                    if(document != null) {
                        sb.append("/" + document.getDocumentName());
                    }
                }
            }
            String permission = this.documentPermissionService.getPermission(tagVO.getIdPath(), userId, userGroupIds, deptId);
            if(permission == null) {
                tagVOListIterator.remove();
            } else {
                if(tagVO.getType().equals(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)) {
                    tagVO.setSource("0");
                    tagVO.setNamePath("/共享空间" + sb.toString());
                }
                if(tagVO.getType().equals(DocumentConstants.DOCUMENT_TYPE_ARCHIVE)) {
                    tagVO.setSource("2");
                    tagVO.setNamePath("/归档文件" + sb.toString());
                }
            }
        }
        return docListByTagId;
	}

	@Override
	public Long setIsDeleteByTagName(Long incId, Long userId, String tagName) {
		TagExample example = new TagExample();
		example.createCriteria().andIncIdEqualTo(incId).andCreateUserEqualTo(userId).andTagNameEqualTo(tagName);
		List<Tag> list = tagMapper.selectByExample(example);
		list.get(0).setIsDelete("0");
		tagMapper.updateByPrimaryKey(list.get(0));
		return list.get(0).getId();
//		tagMapper2.setIsDeleteByTagName(incId, userId, tagName);
	}

	@Override
	public int isTagNameNotExist_1(Long incId, Long userId, String tagName) {
		TagExample example = new TagExample();
		example.createCriteria().andIncIdEqualTo(incId).andCreateUserEqualTo(userId).andTagNameEqualTo(tagName);
		List<Tag> list = tagMapper.selectByExample(example);
		if (null == list||list.isEmpty()) {//tagName不存在
			return -1;
		}
		String isDelete = list.get(0).getIsDelete();
		if ("1".equals(isDelete)) {//tagName存在,只是被删了
			return 1;
		}
		return 0; //tagName存在,且没有被删
	}

	@Override
	public Tag getTagById(Long tagId) {
		return tagMapper.selectByPrimaryKey(tagId);
	}


	@Override
	public void updateTag(Tag tag) {
		//获取登录用户信息
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
		Users user = userInfoVO.getUser();
		//查出id对应的源tag
		Tag originalTag=tagMapper.selectByPrimaryKey(tag.getId());
		if (originalTag!=null && originalTag.getTagName().equalsIgnoreCase(tag.getTagName())){
			throw new YCException(Messages.TAG_NAME_EXIST_MSG
					, Messages.TAG_NAME_EXIST_CODE);
		}else {
			tagMapper.updateByPrimaryKeySelective(tag);
		}
	}
}
