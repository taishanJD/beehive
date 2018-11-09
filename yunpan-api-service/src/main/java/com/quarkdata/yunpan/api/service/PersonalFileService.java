package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentZtreeVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 个人文件
 * 
 * @author typ
 * 	2017年12月11日
 */
public interface PersonalFileService extends BaseLogService {

	/**
	 * 获取个人文件列表
	 * 
	 * @param incId
	 * @param userId
	 * @param parentId
	 * @return
	 */
	List<Document> getPersonalFiles(Long incId, Long userId, Long parentId);
	
	/**
	 * 获取个人文件列表
	 * 	携带收藏和标签信息
	 * 
	 * @param incId
	 * @param userId
	 * @param parentId
	 * @param documentName
	 * @return
	 */
	ResultCode<List<DocumentExtend>> getPersonalFilesCarryCol_tags(Long incId, Long userId, Long parentId,String documentName, Long isExact, Integer pageNum, Integer pageSize);

	/**
	 * 上传文件
	 *
     * @param request
     * @param file
     * @param fileName    如果不为空，则以此文件名为准，如果为空，则以file对象中的文件名为准
     * @param isOverwrite    是否覆盖，1-是：版本+1,0-否：文件名重复则返回提示信息
     * @param parentId
     * @param incId
     * @userId 当前用户的id
	 * @return
	 */
	ResultCode<DocumentExtend> uploadFileApp(HttpServletRequest request, MultipartFile file, String fileName, int isOverwrite, Long parentId, Long userId, String userName, Long incId)throws IOException;

	/**
	 * 上传文件
	 *
	 * @param request
	 * @param file
	 * @param fileName    如果不为空，则以此文件名为准，如果为空，则以file对象中的文件名为准
	 * @param isOverwrite    是否覆盖，1-是：版本+1,0-否：文件名重复则返回提示信息
	 * @param parentId
	 * @param incId
	 * @userId 当前用户的id
	 * @return
	 */
	ResultCode<DocumentExtend> uploadFile(HttpServletRequest request, MultipartFile file, String fileName, int isOverwrite, Long parentId, Long userId, String userName, Long incId)throws IOException;
	
	/**
	 * 创建文件夹
	 *
     * @param request
     * @param documentName
     * @param parentId
     * @param userId
     * @param userName
     * @param incId
     * @return
	 */
	ResultCode<Document> createDir(HttpServletRequest request, String documentName, Long parentId, Long userId, String userName, Long incId)throws YunPanApiException;

	/**
	 * 根据父id获取文件夹
	 * @param incId
	 * @param userId
	 * @param parentId
	 * @return
	 */
	ResultCode<List<DocumentZtreeVO>> getPersonalDirectoryByParentId(Long incId, Long userId, Long parentId);

	ResultCode<String> getPersonalFileCount(Long incId, Long userId);

	ResultCode<List<Document>> getAllChildren(Long incId, Long userId, Long parentId);
}
