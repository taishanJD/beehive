package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.log.LogAnnotation;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.IncConfig;
import com.quarkdata.yunpan.api.model.dataobj.IncConfigExample;
import com.quarkdata.yunpan.api.service.FileService;
import com.quarkdata.yunpan.api.util.S3Utils;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import com.quarkdata.yunpan.api.util.common.config.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by xiexl on 2018/1/10.
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API +"/"+ RouteKey.FILE)
public class FileController extends BaseController {


    @Autowired
    private FileService fileService;

    /**
     * 企业logo上传接口
     * @param file
     * @return
     */
//    @LogAnnotation
    @ResponseBody
    @RequestMapping(value = "/upload")
    public ResultCode upload(@RequestParam("file") MultipartFile file) throws IOException {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();

        ResultCode<String> result = new ResultCode<String>();
        if(incId==null)
        {
            throw new YCException(Messages.API_FILE_UPLOAD_PARAM_MSG,Messages.API_FILE_UPLOAD_PARAM_CODE);
        }
        if (file.isEmpty()) {
            throw new YCException(Messages.API_FILE_UPLOAD_NOTFOUND_MSG,Messages.API_FILE_UPLOAD_NOTFOUND_CODE);
        }
        if(file.getSize()> Constants.LOGO_SIZE*1024)
        {
            throw new YCException(Messages.API_FILE_UPLOAD_SIZE_MSG,Messages.API_FILE_UPLOAD_SIZE_CODE);
        }
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if(!Constants.LOGO_SUFFIX.toLowerCase().contains(extension.toLowerCase()))
        {
            throw new YCException(Messages.API_FILE_UPLOAD_SUFFIX_MSG,Messages.API_FILE_UPLOAD_SUFFIX_CODE);
        }

        BufferedImage bufferedImg = ImageIO.read(file.getInputStream());
        int width=bufferedImg.getWidth();
        int height=bufferedImg.getHeight();
        if(Constants.LOGO_WIDTH!=width || Constants.LOGO_HEIGHT!=height)
        {
            throw new YCException(Messages.API_FILE_UPLOAD_LW_MSG,Messages.API_FILE_UPLOAD_LW_CODE);
        }
		try {
            IncConfig config=new IncConfig();
            config.setIncId(incId);
            config.setLogo(file.getBytes());
            fileService.update(config);
            result.setCode(Messages.SUCCESS_CODE);
            result.setMsg(Messages.SUCCESS_MSG);
            return result;
		} catch (Exception e) {
            throw new YCException(Messages.API_FILE_UPLOAD_FAIL_MSG,Messages.API_FILE_UPLOAD_FAIL_CODE);
		}


    }

    /**
     * 测试logo显示
     * @param response
     * @throws IOException
     */
//    @LogAnnotation
    @ResponseBody
    @RequestMapping(value = "/show")
    public void showLogo(HttpServletResponse response) throws IOException {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        if(incId==null)
        {
            throw new YCException(Messages.API_FILE_UPLOAD_PARAM_MSG,Messages.API_FILE_UPLOAD_PARAM_CODE);
        }
        IncConfig config=new IncConfig();
        config.setIncId(incId);
        byte[] logo=fileService.showLogo(config);
        OutputStream out=null;
        response.setContentType("image/jpeg");
        out=response.getOutputStream();
        out.write(logo);
        out.flush();
        out.close();
    }
}
