package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDeleteType;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Tag;
import com.quarkdata.yunpan.api.model.vo.TagVO;
import com.quarkdata.yunpan.api.service.TagService;
import com.quarkdata.yunpan.api.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(RouteKey.PREFIX_API + RouteKey.TAG)
public class TagColltroller extends BaseController {

	@Autowired
	private TagService tagService;

	static Logger logger = LoggerFactory.getLogger(TagColltroller.class);

	/**
	 * 标签库中添加标签&&文档添加新标签&&文档添加已有标签
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/add")
	@IsDelete(ids = "#{docIdStr}")
	@CheckDocPermission(ids = "#{docIdStr}", permission = DocumentPermissionConstants.PermissionIndex.TAG)
	public ResultCode<String> addTag(HttpServletRequest request, @RequestParam("docId") String docIdStr) {
		ResultCode<String> result = new ResultCode<String>();
		try {

			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
			long userId = user.getUser().getUserId();
			long incId = user.getUser().getIncid();

			String tagIdStr = request.getParameter("tagId");
			String tagName = request.getParameter("tagName");

			if (StringUtils.isNotBlank(tagName)) {
				Long tagId = -1L;

				int exist = tagService.isTagNameNotExist_1(incId, userId, tagName);
				switch (exist) {
				case -1:// 标签名不在
					tagId = tagService.addTag(request, incId, userId, tagName);
					logger.info("标签库添加了 " + tagName + " 标签,id==" + tagId);
					break;
				case 1:// 标签名在，只是被删了
					tagId = tagService.setIsDeleteByTagName(incId, userId, tagName);// 重新启用
					break;
				case 0:// 标签名在，也没被删
					result.setCode(Messages.TAG_NAME_EXIST_CODE);
					result.setMsg(Messages.TAG_NAME_EXIST_MSG);
					return result;
				default:
					break;
				}
				if (StringUtils.isNotBlank(docIdStr)) {
					Long docId = Long.valueOf(docIdStr);
					tagService.addDocTag(request, incId, userId, tagId, docId);
					logger.info("文档 " + docId + " 添加了已有标签 " + tagId);
				}
			}

			if (StringUtils.isNotBlank(tagIdStr) && StringUtils.isNotBlank(docIdStr)) {
				Long docId = Long.valueOf(docIdStr);
				Long tagId = Long.valueOf(tagIdStr);
				tagService.addDocTag(request, incId, userId, tagId, docId);
				logger.info("文档 " + docId + " 添加了已有标签 " + tagId);
			}
			result.setCode(0);
			result.setMsg("add success");
			logger.info("添加标签成功");
		} catch (Exception e) {
			logger.error("token out of time error", e);
			result.setCode(Messages.TOKEN_OUT_OF_TIME_CODE);
			result.setMsg(Messages.TOKEN_OUT_OF_TIME_MSG);
			return result;
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/delete")
	@IsDelete(ids = "#{docIdStr}")
	@CheckDocPermission(ids = "#{docIdStr}", permission = DocumentPermissionConstants.PermissionIndex.TAG)
	public ResultCode<String> deleteTag(HttpServletRequest request, @RequestParam("docId") String docIdStr) {
		ResultCode<String> result = new ResultCode<String>();
		try {
			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
			long userId = user.getUser().getUserId();
			long incId = user.getUser().getIncid();

			String tagIdStr = request.getParameter("tagId");

			Long tagId = Long.valueOf(tagIdStr);
			Long docId;
			if (StringUtils.isNotBlank(docIdStr)) {
				docId = Long.valueOf(docIdStr);
				tagService.deleteDocTag(request, incId, userId, tagId, docId);
				result.setCode(0);
				result.setMsg("add success");
				logger.info("删除文档 " + docId + " 标签成功");
			} else {
				tagService.deleteTag(incId, userId, tagId);
				logger.info("删除标签库 " + tagId + " 标签成功");
			}
			result.setCode(0);
			result.setMsg("delete success");
		} catch (Exception e) {
			logger.error("token out of time error", e);
			result.setCode(Messages.TOKEN_OUT_OF_TIME_CODE);
			result.setMsg(Messages.TOKEN_OUT_OF_TIME_MSG);
			return result;
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/check_tag")
	public ResultCode<Integer> checkTagRelation(HttpServletRequest request){
		ResultCode<Integer> result = new ResultCode<>();
		try {
			// token中获取用户信息
			UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
			long userId = userInfoVO.getUser().getUserId();
			long incId = userInfoVO.getUser().getIncid();
            List<Long> groupIds = UserInfoUtil.getGroupIds();
            Department department = userInfoVO.getDepartment();
            String tagIdStr = request.getParameter("tagId");
			List<TagVO> resultList = tagService.getDocListByTagId(incId, userId, groupIds, department == null ? null : department.getId().longValue(), new Long(tagIdStr), "");
			result.setCode(0);
			result.setData(0);
			if(CollectionUtils.isNotEmpty(resultList)) {
				result.setData(resultList.size());
			}
		} catch (Exception e) {
			logger.error("token out of time error", e);
			result.setCode(Messages.TOKEN_OUT_OF_TIME_CODE);
			result.setMsg(Messages.TOKEN_OUT_OF_TIME_MSG);
			return result;
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/get_top_list")
	public ResultCode<List<Tag>> getTopTenTagList(HttpServletRequest request) {
		ResultCode<List<Tag>> result = new ResultCode<List<Tag>>();
		try {
			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
			long userId = user.getUser().getUserId();
			long incId = user.getUser().getIncid();

			logger.info("开始获取user id==" + userId + "的top 10列表！");
			List<Tag> topTags = tagService.getTopTenTagList(incId, userId);
			logger.info("获取结束！");
			// String jsonString = JsonMapper.toJsonString(topTags);
			result.setData(topTags);
		} catch (Exception e) {
			logger.error("get top ten list error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/get_list")
	public ResultCode<List<Tag>> getTagList(HttpServletRequest request) {
		ResultCode<List<Tag>> result = new ResultCode<List<Tag>>();
		try {
			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
			long userId = user.getUser().getUserId();
			long incId = user.getUser().getIncid();

			String filter = request.getParameter("filter");

			logger.info("开始获取用户id==" + userId + "的所有未删除标签");
			List<Tag> allTags = tagService.getAllTagList(incId, userId, filter);
			logger.info("查询完毕！");
			// String jsonString = JsonMapper.toJsonString(allTags);
			result.setData(allTags);
		} catch (Exception e) {
			logger.error("get all list error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/get_doc_list")
	public ResultCode<List<TagVO>> getDocListByTag(HttpServletRequest request) {
		ResultCode<List<TagVO>> result = new ResultCode<List<TagVO>>();
		try {
			// token中获取用户信息
			UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
			long userId = userInfoVO.getUser().getUserId();
			long incId = userInfoVO.getUser().getIncid();
            List<Long> groupIds = UserInfoUtil.getGroupIds();
            Department department = userInfoVO.getDepartment();

			String tagIdStr = request.getParameter("tagId");
			String filter = request.getParameter("filter");

			if (StringUtils.isNotBlank(tagIdStr)) {
				Long tagId = Long.valueOf(tagIdStr);
				logger.info("开始查询tagId == " + tagId + "下的文档列表！");
                List<TagVO> resultList = tagService.getDocListByTagId(incId, userId, groupIds, department == null ? null : department.getId().longValue(), tagId, filter);
				logger.info("查询完毕！");
				// String data = JsonMapper.toJsonString(resultList);
				result.setData(resultList);
			}
		} catch (Exception e) {
			logger.error("get top ten list error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/check")
	public ResultCode<Integer> isTagNameExist(HttpServletRequest request) {
		ResultCode<Integer> result = new ResultCode<Integer>();
		try {
			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
			long userId = user.getUser().getUserId();
			long incId = user.getUser().getIncid();

			String tagName = request.getParameter("tagName");

			if (StringUtils.isNotBlank(tagName)) {
				logger.info("开始查询tagName == " + tagName + "是否存在");
				// data ===  -1: tagName不存在 ；1：tagName存在但被删除；0：tagName存在且没被删除
				int data = tagService.isTagNameNotExist_1(incId, userId, tagName);
				logger.info("查询完毕！===" + data);
				result.setData(data);
			}
		} catch (Exception e) {
			logger.error("check tag name error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/get_tag")
	public ResultCode<Tag> getTagById(HttpServletRequest request) {
		ResultCode<Tag> result = new ResultCode<Tag>();
		try {
			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
//			long userId = user.getUser().getId();
//			long incId = user.getUser().getIncid();

			String tagIdStr = request.getParameter("tagId");

			if (StringUtils.isNotBlank(tagIdStr)) {
				Long tagId = Long.valueOf(tagIdStr);
				logger.info("开始查询tagId == " + tagId + "的tag");
				Tag tag = tagService.getTagById(tagId);
				logger.info("查询完毕！");
				// String data = JsonMapper.toJsonString(resultList);
				result.setData(tag);
			}
		} catch (Exception e) {
			logger.error("get tag error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

	/**
	 * 更新tag
	 * @param tag
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/update",method = RequestMethod.POST)
	@IsDelete(ids = "#{tag.id}", className = IsDeleteType.Tag)
	public ResultCode<String> update(@RequestBody Tag tag,HttpServletRequest request){
		ResultCode<String> resultCode=new ResultCode<>();
		if (tag!=null && !StringUtils.isBlank(tag.getTagName())){
			tagService.updateTag(tag);
		}
		return resultCode;
	}
}
