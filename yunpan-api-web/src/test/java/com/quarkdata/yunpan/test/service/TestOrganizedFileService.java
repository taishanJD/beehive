package com.quarkdata.yunpan.test.service;

import com.quarkdata.yunpan.api.dal.dao.DocumentMapper2;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.service.OrganizedFileService;
import com.quarkdata.yunpan.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TestOrganizedFileService extends BaseTest{
	
	@Autowired
	OrganizedFileService organizedFileService;
	@Autowired
	DocumentMapper2 documentMapper2;
    @Test
    public void getOrganizedFiles() {
    	Long incId=10L;
		Long userId=1L;
		Long parentId=0L;
		Long userGroupId=0L;
		Long deptId=0L;
		List<Document> personalFiles = organizedFileService.getOrganizedFiles(incId, userId, userGroupId, deptId, parentId);
//		List<Document> personalFiles = organizedFileService.getOrganizedFiles(incId, userId, null,null, parentId);
		System.out.println(personalFiles);
    }
    @Test
    public void getOrganizedFiles2() {
    	Long incId=10L;
		Long userId=1L;
		Long parentId=0L;
//		Long userGroupId=0L;
		List<Long> userGroupIds=new ArrayList<>();
		userGroupIds.add(0L);
		userGroupIds.add(1L);
		
		Long deptId=0L;
		Long dirId=5L;
		ResultCode<List<DocumentExtend>> files = organizedFileService.getOrganizedFilesCarryCol_tags(incId, userId, userGroupIds, deptId, parentId,null, Long.valueOf(0), false, 1, 10);
//		List<DocumentExtend> organizedFilesCarryCol_tags = documentMapper2.getOrganizedFilesCarryCol_tags(incId, userId, userGroupIds, deptId, parentId,null);
		// long checkUploadPrivilege = documentMapper2.checkUploadPrivilege(userId, userGroupIds, deptId, dirId);
		System.out.println(files);
    }

}
