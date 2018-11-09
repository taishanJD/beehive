package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.aspect.isShare.IsShare;
import com.quarkdata.yunpan.api.message.MessageAnnotation;
import com.quarkdata.yunpan.api.message.MessageType;
import com.quarkdata.yunpan.api.message.MessageUtils;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.service.CollectionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(RouteKey.PREFIX_API + RouteKey.COLLECTION)
public class CollectionController extends BaseController {
	@Autowired
	private CollectionService collectionService;

	static Logger logger = LoggerFactory.getLogger(CollectionController.class);

	/**
	 * 添加收藏
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@MessageAnnotation
	@RequestMapping(value = "/add")
	@IsDelete(ids = "#{docIdStr}")
	@IsShare(documentIds = "#{docIdStr}")
	public ResultCode<String> addCollection(HttpServletRequest request, @RequestParam("documentId") String docIdStr) {
		ResultCode<String> result = new ResultCode<String>();
		ArrayList<Long> longs = new ArrayList<>();
		ArrayList<Receiver> receivers = new ArrayList<>();
		try {
			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
			long userId = user.getUser().getUserId();
			long incId = user.getUser().getIncid();

			collectionService.addCollection(request, incId, userId, Long.parseLong(docIdStr));
			//消息通知文件列表
			longs.add(Long.parseLong(docIdStr));
			//消息通知通知人
			Receiver receiver = new Receiver();
			receiver.setRecId(userId+"");
			receiver.setRecType("0");
			receiver.setPermission("3");
			receivers.add(receiver);

			result.setCode(0);
			result.setMsg("add success");
			logger.info("添加收藏成功");
		} catch (Exception e) {
			logger.error("add collection error", e);
			result.setCode(Messages.API_ADD_COLLECT_CODE);
			result.setMsg(Messages.API_ADD_COLLECT_MSG);
			return result;
		}
		//消息通知
		MessageUtils.addMessage(request,result.getCode().toString(), MessageType.Collection_success,longs,receivers);
		return result;
	}

	/**
	 * 取消收藏
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@MessageAnnotation
	@RequestMapping(value = "/delete")
	@IsDelete(ids = "#{documentIds}")
	@IsShare(documentIds = "#{documentIds}")
	public ResultCode<String> deleteCollection(HttpServletRequest request, String documentIds) {
		ResultCode<String> result = new ResultCode<String>();
		ArrayList<Long> longs = new ArrayList<>();
		ArrayList<Receiver> receivers = new ArrayList<>();
		try {
			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
			long userId = user.getUser().getUserId();
			long incId = user.getUser().getIncid();
			collectionService.deleteCollection(request, incId, userId, documentIds);

			String[] docIds = documentIds.split(",");
			for (String dString:docIds) {
				//消息通知文件列表
				longs.add(Long.parseLong(dString));
			}
			//消息通知通知人
			Receiver receiver = new Receiver();
			receiver.setRecId(userId + "");
			receiver.setRecType("0");
			receiver.setPermission("3");
			receivers.add(receiver);
			result.setCode(0);
			result.setMsg("delete success");
			logger.info("取消收藏成功");
		} catch (Exception e) {
			logger.error("delete collection error", e);
			result.setCode(Messages.API_DEL_COLLECT_CODE);
			result.setMsg(Messages.API_DEL_COLLECT_MSG);
			return result;
		}
		//消息通知
		MessageUtils.addMessage(request,result.getCode().toString(), MessageType.Collection_cancel,longs,receivers);
		return result;
	}

	/**
	 * 获取收藏列表,附标签
	 * 
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/get_list")
	public ResultCode<List<Map<String, Object>>> getCollectionList(@RequestParam(value = "documentName", required = false) String documentName, Long parentId, Integer pageNum, Integer pageSize) {
		ResultCode<List<Map<String, Object>>> result = new ResultCode<List<Map<String, Object>>>();
		try {
			// token中获取用户信息
			UserInfoVO user = UserInfoUtil.getUserInfoVO();
			long userId = user.getUser().getUserId();
			long incId = user.getUser().getIncid();
			List<Long> userGroupIds = getGroupIds(UserInfoUtil.getUserInfoVO()
					.getGroupsList());
			Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId()
					.longValue();
			List<Map<String, Object>> collectionVOs = collectionService.getCollectionList(userId, incId, parentId, documentName, pageNum, pageSize, userGroupIds, deptId);
			result.setCode(0);
			result.setMsg("get list success");
			result.setData(collectionVOs);
			logger.info("获取收藏列表成功");
		} catch (Exception e) {
			logger.error("get collect error", e);
			result.setCode(Messages.API_GET_COLLECT_CODE);
			result.setMsg(Messages.API_GET_COLLECT_MSG);
			return result;
		}
		return result;

	}
	
	private List<Long> getGroupIds(List<Group> groupsList) {
		List<Long> groupIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(groupsList)) {
			for (Group g : groupsList) {
				if(g !=null){
					groupIds.add(g.getId().longValue());
				}
			}
		}
		return groupIds;
	}
}
