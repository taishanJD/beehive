package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.RoleConstants;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.FullTextVo;
import com.quarkdata.yunpan.api.service.FullTextService;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.common.config.Global;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuda
 * Date : 2018/3/26
 */
@RestController
@RequestMapping(RouteKey.PREFIX_API+ RouteKey.FULLTEXT)
public class FullTextController {

    static Logger logger = LoggerFactory.getLogger(FullTextController.class);

    @Autowired
    private TransportClient client;

    @Autowired
    FullTextService fullTextService;

    @RequestMapping(value = "/search")
    public ResultCode search(@RequestParam(value = "param") String param,
                             @RequestParam(value = "type")String type,
                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "parentId", defaultValue = "0") Long parentId,
                             @RequestParam(value = "preTag", defaultValue = "") String preTag,
                             @RequestParam(value = "postTag", defaultValue = "") String postTag){
        ResultCode<FullTextVo> resultCode =null;
        try {
            // token中获取用户信息
            UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
            Users user = userInfoVO.getUser();
            Long incId=user.getIncid().longValue();
            Long userId=user.getUserId().longValue();
            List<Long> userGroupIds = getGroupIds(UserInfoUtil.getUserInfoVO()
                    .getGroupsList());
            Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId()
                    .longValue();
            Long roleId = UserInfoUtil.getUserInfoVO().getRole().getId();
            //约定索引名 quarkdata+组织id
            //TODO 权限id数组
            String[] ids=null;
            switch (type){
                case "0":
                    //ids=new String[0];
                    //ids=fullTextService.getOrgFileIdList(incId,userId,userGroupIds,deptId,parentId, isOrganizedAdmin(roleId));
                    List<DocumentExtend> documents = fullTextService.getOrgFiles(incId,userId,userGroupIds,deptId,parentId, isOrganizedAdmin(roleId));
                    return  fullTextService.searchOrgEs(incId,parentId, param, pageNum, pageSize, Global.getConfig("index.prefixion")+incId, documents, type, preTag, postTag);
                case "1":
                    //个人文件id数组
                    ids=fullTextService.getPersonalFileIdList(incId,userId);
                    break;
                case "2":
                    //归档文件id数组
                    ids=fullTextService.getArchivalFileIdList(incId,userId,userGroupIds,deptId);
                    break;
                case "3":
                    ids=new String[0];
                    ids=fullTextService.getExternalSpaceFileIdList(incId,userId,userGroupIds,deptId,parentId);
                    break;
                default:
                    ids=new String[0];
                    break;
            }
           resultCode = fullTextService.searchEs(param, pageNum, pageSize, Global.getConfig("index.prefixion")+incId,ids, type, preTag, postTag);
        }catch (Exception e){
            resultCode = ResultUtil.error(Messages.API_FULLTEXT_SEARCH_CODE, Messages.API_FULLTEXT_SEARCH_MSG);
            logger.error(Messages.API_FULLTEXT_SEARCH_MSG+e);
        }
        return resultCode;
    }

    private boolean isOrganizedAdmin(Long roleId) {
        boolean isOrganizedAdmin = false;
        if (roleId !=null && roleId.equals(RoleConstants.ORGANIZED_ADMIN_ROLE_ID)) {
            // 组织管理员
            isOrganizedAdmin = true;
        }
        return isOrganizedAdmin;
    }

    /**
     * 此为超级管理员设置项，慎用
     * @param incId
     * @param shards
     * @param replicas
     * @return
     */
    @RequestMapping(value = "/addIndex")
    public ResultCode addIndex(@RequestParam(value = "incId") String incId,
                               @RequestParam(value = "shards") Integer shards,
                               @RequestParam(value = "replicas") Integer replicas){
        ResultCode<Object> resultCode = new ResultCode<>();
        try {
            if (fullTextService.isExistsIndex(Global.getConfig("index.prefixion")+incId)){
                logger.error("新建失败,该组织已有索引");
                resultCode= ResultUtil.error(Messages.API_FULLTEXT_CREATE_INDEX_CODE, Messages.API_FULLTEXT_CREATE_INDEX_MSG);
            }else {
                if (fullTextService.createIndex(incId,shards,replicas)){
                    logger.info("新建索引成功");
                    resultCode= ResultUtil.success();
                }else {
                    logger.error("新建索引失败");
                    resultCode= ResultUtil.error(Messages.API_FULLTEXT_CREATE_NEWINDEX_CODE, Messages.API_FULLTEXT_CREATE_NEWINDEX_MSG);
                }
            }
        }catch (Exception e){
            logger.error("新建索引失败");
            resultCode= ResultUtil.error(Messages.API_FULLTEXT_CREATE_NEWINDEX_CODE, Messages.API_FULLTEXT_CREATE_NEWINDEX_MSG);
        }
        return resultCode;
    }

    /**
     * 此为超级管理员设置项，慎用
     * @param incId
     * @return
     */
    @RequestMapping(value = "/delete")
    public ResultCode deleteIndex(@RequestParam(value = "incId") String incId){
        return fullTextService.deleteIndex(incId);
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
