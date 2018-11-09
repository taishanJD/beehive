package com.quarkdata.yunpan.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by yanyq1129@thundersoft.com on 2018/10/8.
 */
@Component
public class SmsUtils {

    private static Logger logger = LoggerFactory.getLogger(SmsUtils.class);

    /**
     * 阿里云短信服務
     */
    public static void sendALiYunSms() {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
        //替换成你的AK
        final String accessKeyId = "LTAIihr1PkHT6GAY";//你的accessKeyId,参考本文档步骤2
        final String accessKeySecret = "DadR2KKSyTWQ5tdWH4UhGxjSoqZ6zc";//你的accessKeySecret，参考本文档步骤2
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
        request.setPhoneNumbers("1500000000");
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("云通信");
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode("SMS_1000000");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            //请求成功
        }
    }

    // ==================================== 赛邮云通信 =========================================

    private static String TIMESTAMP;
    private static String URL;
    private static String appid;
    private static String appkey;
    private static String project;
    private static String project_secret;
    private static String signtype;

    @Value("${com.submail.timestamp}")
    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    @Value("${com.submail.url}")
    public void setURL(String URL) {
        this.URL = URL;
    }

    @Value("${com.submail.appid}")
    public void setAppid(String appid) {
        this.appid = appid;
    }

    @Value("${com.submail.appkey}")
    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    @Value("${com.submial.project.link}")
    public void setProject(String project) {
        this.project = project;
    }

    @Value("${com.submial.project.link.secret}")
    public void setProject_secret(String project_secret) {
        this.project_secret = project_secret;
    }

    @Value("${com.submail.signtype}")
    public void setSigntype(String signtype) {
        this.signtype = signtype;
    }

    /**
     * 賽邮-短信通
     */
    public static void sendSaiYouSmsOfLink(String to, String name, String code, Boolean isSecret, String fetchCode) {
        TreeMap<String, Object> requestData = new TreeMap();
        JSONObject toJSON = new JSONObject();
        JSONObject varsJSON = new JSONObject();
        ArrayList<String> multi = new ArrayList();
        toJSON.put("to", to);
        varsJSON.put("code", code);
        varsJSON.put("name", name);
        if(isSecret) {
            varsJSON.put("fetchcode", fetchCode);
        }
        toJSON.put("vars", varsJSON);
        multi.add(toJSON.toString());
        varsJSON.clear();
        toJSON.clear();
        requestData.put("appid", appid);
        requestData.put("project", isSecret ? project_secret : project);
        if (!multi.isEmpty()) {
            requestData.put("multi", multi.toString());
        }

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        ContentType contentType = ContentType.create("text/plain", "UTF-8");
        Iterator var13 = requestData.entrySet().iterator();

        while(var13.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var13.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                builder.addTextBody(key, String.valueOf(value), contentType);
            }
        }

        if (!signtype.equals("md5") && !signtype.equals("sha1")) {
            builder.addTextBody("signature", appkey, contentType);
        } else {
            String timestamp = getTimestamp();
            requestData.put("timestamp", timestamp);
            requestData.put("sign_type", signtype);
            String signStr = appid + appkey + RequestEncoder.formatRequest(requestData) + appid + appkey;
            builder.addTextBody("timestamp", timestamp);
            builder.addTextBody("sign_type", signtype);
            builder.addTextBody("signature", RequestEncoder.encode(signtype, signStr), contentType);
        }

        HttpPost httpPost = new HttpPost(URL);
        httpPost.addHeader("charset", "UTF-8");
        httpPost.setEntity(builder.build());

        try {
            CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
            HttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                String jsonStr = EntityUtils.toString(httpEntity, "UTF-8");
                System.out.println(jsonStr);
                List<Map> result = JSON.parseArray(jsonStr, Map.class);
                if(result != null && result.size() > 0 && !result.get(0).get("status").equals("success")) {
                    logger.error("短信发送失败");
                }
            }
        } catch (ClientProtocolException var17) {
            var17.printStackTrace();
            logger.error("短信发送失败");
        } catch (IOException var18) {
            var18.printStackTrace();
            logger.error("短信发送失败");
        }
    }

    /**
     * 获取时间戳
     * @return
     */
    private static String getTimestamp(){
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(TIMESTAMP);
        try{
            HttpResponse response = closeableHttpClient.execute(httpget);
            HttpEntity httpEntity = response.getEntity();
            String jsonStr = EntityUtils.toString(httpEntity,"UTF-8");
            if(jsonStr != null){
                JSONObject json = JSONObject.parseObject(jsonStr);
                return json.getString("timestamp");
            }
            closeableHttpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
