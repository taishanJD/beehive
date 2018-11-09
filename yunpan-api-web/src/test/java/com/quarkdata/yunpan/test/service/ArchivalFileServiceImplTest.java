package com.quarkdata.yunpan.test.service;



import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.dal.dao.DocumentMapper2;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.service.ArchivalFileService;
import com.quarkdata.yunpan.api.service.PersonalFileService;
import com.quarkdata.yunpan.api.util.EmailUtil;
import com.quarkdata.yunpan.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


public class ArchivalFileServiceImplTest extends BaseTest {
    @Autowired
    private ArchivalFileService archivalFileService;
    @Autowired
    private PersonalFileService personalFileService;
    @Autowired
    private DocumentMapper2 documentMapper2;




    @Test
    public void ListFile(){
        //TODO
        Long incId=1L;
        Long userId=1L;
        Long parentId=0L;
        String documentType="dir";
        String documentName=null;
        String editDay=null;
//        ResultCode<List<DocumentExtend>> resultCode =
//                archivalFileService.getArchivalFile(incId,userId,parentId,documentType,documentName,editDay);
//        System.out.println(JsonMapper.toJsonString(resultCode));
    }
    @Test
    public void testArchivl(){
        Users users = new Users();
        users.setUserId(1);
        users.setIncid(1);
        users.setDisplayName("admin1");
        users.setUserName("dada");
        Receiver receiver = new Receiver();
        receiver.setRecId("52");
        receiver.setRecType("user");
        receiver.setPermission("0");
        List<Receiver> list= new ArrayList<>();
        list.add(receiver);
        ArrayList<Long> documents = new ArrayList<>();
        /*documents.add(197L);
        documents.add(195L);*/
        documents.add(335L);
        documents.add(330L);

       /* Document document = documentMapper.selectByPrimaryKey(11L);
        documents.add(document);*/
        /*ResultCode dada = archivalFileService.manualCreateArchivalFile(list, "dada","bnfiuhgbweiubfgiuw", users, documents, "0");
        System.out.println(dada);*/

    }
    @Test
    public void testauto(){
        /*Users users = new Users();
        users.setId(52);
        users.setIncid(1);
        users.setDisplayName("admin1");
        users.setUserName("dada");
        Receiver receiver = new Receiver();
        receiver.setRecId("52");
        receiver.setRecType("1");
        receiver.setPermission("0");
        List<Receiver> list= new ArrayList<>();*/
        /*list.add(receiver);*/
        ArrayList<Long> documents = new ArrayList<>();
        documents.add(609L);
        ArrayList<String> list1 = new ArrayList<>();
       // list1.add("txt");
        /*list1.add("zip");
        list1.add("jpg");*/
        /*list1.add("avi");
        list1.add("txt");*/
        ArrayList<Long> lis = new ArrayList<>();
        lis.add(649L);

        ArrayList<Long> longs1 = new ArrayList<>();
        /*longs1.add(1L);*/


        ResultCode resultCode = archivalFileService.autoCreateArchivalFile(lis, null, list1, longs1);
        System.out.println(resultCode);

        ResultCode resultCode1 = archivalFileService.manualProList(lis);
        System.out.println(resultCode1);
        // List<Long> docIdLists= archivalFileService.autoCreateArchivalFile(lis,list, "haha", users,null, null, null, "0");
        // ResultCode resultCode = archivalFileService.manualCreateArchivalFile(list,"dadada",users, docIdLists,"1");
        //System.out.println( resultCode);
    }
    @Test
    public void testmail(){
        String[] address={"986491633@qq.com","986491634@qq.com"};
        String sender="liuda15003403971@sina.com";
        String title="使用忘记密码找回功能";
        String text="恭喜你获取您的新密码，你的新密码是"+"123456"+"，请你尽快到云盘官网去完善你的个人信息;网址:http://localhost:8080/yunpan-api-share";
//        EmailUtil.send(address, text, title, null, null);

        System.out.println("发送邮件成功");
    }

    @Test
    public void test1(){
       /* HashMap<String, Integer> map = new HashMap<>();
        map.put("liuda",1);
        map.put("jialei",1);
        System.out.println(map.get("liuda")==null);
        if (map.get("liuda")!=null){
            map.put("liuda",map.get("liuda")+1);
        }
        System.out.println(map.get("liuda"));*/
        List idPathList = documentMapper2.selectByPath("/649");
        System.out.println(idPathList);
    }


}