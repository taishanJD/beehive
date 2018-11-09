package com.quarkdata.yunpan.api.controller.api;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.aspect.isEnoughSpace.IsEnoughSpace;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentZtreeVO;
import com.quarkdata.yunpan.api.service.PersonalFileService;
import com.quarkdata.yunpan.api.util.EsUtils;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import com.quarkdata.yunpan.api.util.common.sensitive.SensitiveWordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 个人文件
 *
 * @author typ 2017年12月11日
 */
@RequestMapping(RouteKey.PREFIX_API + "/personal/file")
@RestController
public class PersonalFileController extends BaseController {

    @Autowired
    private PersonalFileService personalFileService;

    /**
     * 个人文件列表
     *
     * @param documentName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @IsDelete(ids = "#{parentId}")
    public ResultCode<List<DocumentExtend>> list(
            String documentName,
            @RequestParam(value = "isExact", required = false, defaultValue = "0") Long isExact,
            @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId,
            Integer pageNum, Integer pageSize) {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();

        ResultCode<List<DocumentExtend>> resultCode = null;
        try {
            resultCode = personalFileService.getPersonalFilesCarryCol_tags(incId, userId, parentId, documentName, isExact, pageNum, pageSize);
        } catch (Exception e) {
            logger.error("get personal document list", e);
            resultCode = new ResultCode<List<DocumentExtend>>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 上传文件
     *
     * @param file
     * @param fileName    如果不为空，则以此文件名为准，如果为空，则以file对象中的文件名为准
     * @param isOverwrite 是否覆盖，是：版本+1,否：文件名重复则返回提示信息
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upload_file", method = RequestMethod.POST)
    @IsDelete(ids = "#{parentId}")
    @IsEnoughSpace(type = DocumentConstants.DOCUMENT_TYPE_PERSONAL, docSize = "#{file.size}")
    public ResultCode<DocumentExtend> uploadFile(
            @RequestParam(value = "file", required = true) MultipartFile file,
            String fileName,
            @RequestParam(value = "isOverwrite", required = false, defaultValue = "0") int isOverwrite,
            @RequestParam(value = "parentId", required = true) Long parentId,
            HttpServletRequest request) throws IOException {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
        String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();

//         测试敏感词替换速度
        String content = EsUtils.readFile(file);

//        long startTime = System.currentTimeMillis();
//        SensitiveFilter.DEFAULT.filter(content, '*');
//        long endTime = System.currentTimeMillis();
//        logger.info("=================={}, SensitiveFilter耗时: {} ms=====================", file.getOriginalFilename(), endTime - startTime);
//
        long startTime2 = System.currentTimeMillis();
        boolean contains = SensitiveWordUtil.contains(content);
        long endTime2 = System.currentTimeMillis();
        logger.info("=================={}, 文档大小: {},  SensitiveWordUtil检测是否包含敏感词耗时: {} ms======================",
                file.getOriginalFilename(), file.getSize(), endTime2 - startTime2);
        if(contains) {
            Set<String> sensitiveWord = SensitiveWordUtil.getSensitiveWord(content);
//            logger.error("ㄚㄚㄚㄚㄚ 敏感词: {} ㄚㄚㄚㄚㄚ", sensitiveWord);
            throw new YCException(Messages.CONTAINS_SENSITIVE_WORD_MSG + sensitiveWord, Messages.CONTAINS_SENSITIVE_WORD_CODE);
        }
        logger.info("start upload file,params --> "
                        + "originalFilename:{},fileName:{},"
                        + "isOverwrite:{},parentId:{},userId:{},"
                        + "userName:{},incId:{}", file.getOriginalFilename(), fileName,
                isOverwrite, parentId, userId, userName, incId);
        ResultCode<DocumentExtend> resultCode = null;

        try {

            resultCode = personalFileService.uploadFile(request, file, fileName,
                    isOverwrite, parentId, userId, userName, incId);
            logger.info("after upload file,result --> " + JSON.toJSONString(resultCode));
        } catch (DuplicateKeyException e) {
            logger.info("处理插入重复文件");
            resultCode = new ResultCode<DocumentExtend>();
            resultCode.setCode(Messages.API_INSERT_ERROR_CODE);
            resultCode.setMsg(Messages.API_INSERT_ERROR_MSG);
        } catch (Exception e) {
            logger.error("upload personal file", e);
            resultCode = new ResultCode<DocumentExtend>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 上传文件_App
     *
     * @param file
     * @param fileName    如果不为空，则以此文件名为准，如果为空，则以file对象中的文件名为准
     * @param isOverwrite 是否覆盖，是：版本+1,否：文件名重复则返回提示信息
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upload_file_app", method = RequestMethod.POST)
    @IsDelete(ids = "#{parentId}")
    @IsEnoughSpace(type = DocumentConstants.DOCUMENT_TYPE_PERSONAL, docSize = "#{file.size}")
    public ResultCode<DocumentExtend> uploadFileApp(
            @RequestParam(value = "file", required = true) MultipartFile file,
            String fileName,
            @RequestParam(value = "isOverwrite", required = false, defaultValue = "0") int isOverwrite,
            @RequestParam(value = "parentId", required = true) Long parentId,
            HttpServletRequest request) throws IOException {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
        String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();

        logger.info("start upload file,params --> "
                        + "originalFilename:{},fileName:{},"
                        + "isOverwrite:{},parentId:{},userId:{},"
                        + "userName:{},incId:{}", file.getOriginalFilename(), fileName,
                isOverwrite, parentId, userId, userName, incId);
        ResultCode<DocumentExtend> resultCode = null;

        try {

            resultCode = personalFileService.uploadFileApp(request, file, fileName,
                    isOverwrite, parentId, userId, userName, incId);
            logger.info("after upload file,result --> " + JSON.toJSONString(resultCode));
        } catch (DuplicateKeyException e) {
            logger.info("处理插入重复文件");
            resultCode = new ResultCode<DocumentExtend>();
            resultCode.setCode(Messages.API_INSERT_ERROR_CODE);
            resultCode.setMsg(Messages.API_INSERT_ERROR_MSG);
        } catch (Exception e) {
            logger.error("upload personal file", e);
            resultCode = new ResultCode<DocumentExtend>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 创建个人文件夹
     *
     * @param documentName
     * @param parentId
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/create_dir", method = RequestMethod.POST)
    @IsDelete(ids = "#{parentId}")
    public ResultCode<Document> createDir(
            @RequestParam(value = "documentName", required = true) String documentName,
            @RequestParam(value = "parentId", required = true) Long parentId,
            HttpServletRequest request) throws IOException {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
        String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
        ResultCode<Document> resultCode = null;
        if (StringUtils.isBlank(documentName)) {
            resultCode = new ResultCode<>();
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG);
            return resultCode;
        }

        logger.info("start create dir,params -->"
                        + "documentName:{},parentId:{},userId:{},userName:{},incId:{}",
                documentName, parentId, userId, userName, incId);
        try {
            resultCode = personalFileService.createDir(request, documentName.trim(), parentId,
                    userId, userName, incId);
            logger.info("after create dir,result --> " + JSON.toJSONString(resultCode));
        } catch (YunPanApiException e) {
            logger.error("new personal dir,errCode is {},errMsg is {}",
                    e.getErrCode(), e.getErrMsg());
            resultCode = new ResultCode<Document>();
            resultCode.setCode(e.getErrCode());
            resultCode.setMsg(e.getErrMsg());
        } catch (Exception e) {
            logger.error("new personal dir", e);
            resultCode = new ResultCode<Document>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 根据parentId获取个人文件子文件夹目录
     *
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/dir")
    @ResponseBody
    @IsDelete(ids = "#{parentId}")
    public ResultCode<List<DocumentZtreeVO>> getPersonalDirectoryByParentId(
            @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId) {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
        ResultCode<List<DocumentZtreeVO>> resultCode = null;
        try {
            resultCode = personalFileService.getPersonalDirectoryByParentId(
                    incId, userId, parentId);
        } catch (Exception e) {
            logger.error("get personal directory list", e);
            resultCode = new ResultCode<List<DocumentZtreeVO>>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 根据parentId获取个人文件数量
     *
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/getCount")
    @ResponseBody
    public ResultCode<String> getCount(
            @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId) {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
        ResultCode<String> resultCode = null;
        try {
            resultCode = personalFileService.getPersonalFileCount(incId, userId);
        } catch (Exception e) {
            logger.error("get personal directory list", e);
            resultCode = new ResultCode<>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 根据parentId获取文件
     *
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/getAllChildren")
    @ResponseBody
    @IsDelete(ids = "#{parentId}")
    public ResultCode<List<Document>> getAllChildren(
            @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId) {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
        ResultCode<List<Document>> resultCode = null;
        try {
            resultCode = personalFileService.getAllChildren(incId, userId, parentId);
        } catch (Exception e) {
            logger.error("get personal all children", e);
            resultCode = new ResultCode<>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }
}
