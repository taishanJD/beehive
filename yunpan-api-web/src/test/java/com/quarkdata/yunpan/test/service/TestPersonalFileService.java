package com.quarkdata.yunpan.test.service;

import com.quarkdata.yunpan.api.dal.dao.DocumentMapper2;
import com.quarkdata.yunpan.api.dal.dao.DocumentPermissionMapper2;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.service.PersonalFileService;
import com.quarkdata.yunpan.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试PersonalFileService
 * 
 * @author typ
 * 	2017年12月11日 
 */

public class TestPersonalFileService extends BaseTest{

    @Autowired
    PersonalFileService personalFileService;
    @Autowired
    DocumentMapper2 documentMapper2;
    @Autowired
    DocumentPermissionMapper2 documentPermissionMapper2;
    @Test
    public void getPersonalFiles() {
    	Long incId=10L;
		Long userId=1L;
		Long parentId=0L;
		List<Document> personalFiles = personalFileService.getPersonalFiles(incId, userId, parentId);
		System.out.println(personalFiles);
    }
    
    @Test
    public void getPersonalFiles2() {
    	Long incId=10L;
		Long userId=1L;
		Long parentId=0L;
    	List<DocumentExtend> personalFilesCarryCol_tags = documentMapper2.getPersonalFilesCarryCol_tags(incId, userId, parentId,null, Long.valueOf(0), 1, 10);
    	System.out.println(personalFilesCarryCol_tags);
    }
    
    @Test
    public void test1(){
    	String[] docIds=new String[]{"1","2"};
		Long userId=1l;
		List<Long> userGroupIds=new ArrayList<>();
		userGroupIds.add(1L);
		Long deptId=1L;
		String permission = documentPermissionMapper2.getRecentPermission(docIds, userId, userGroupIds, deptId);
		System.out.println(permission);
    }
    
    @Test
    public void testgetAdminFileList(){
    	/*Long incId=1L;
		Long parentId=0L;
		List<DocumentExtend> adminFileList = documentMapper2.getAdminFileList(incId, parentId);
		System.out.println(adminFileList);*/
    }
}
