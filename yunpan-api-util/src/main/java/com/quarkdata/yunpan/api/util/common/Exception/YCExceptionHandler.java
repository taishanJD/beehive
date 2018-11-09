package com.quarkdata.yunpan.api.util.common.Exception;

import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xiexl on 2018/1/11.
 * 自定义异常处理
 */

@ControllerAdvice
public class YCExceptionHandler {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     */
    @ResponseBody
    @ExceptionHandler(YCException.class)
    public ResultCode handleRRException(YCException e) {
        LOGGER.error(e.getMsg());
        return ResultUtil.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理文件操作权限异常
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(YCAuthorizationException.class)
    public ResultCode handleYCAuthorizationException(YCAuthorizationException e) {
        LOGGER.error(e.getMessage());
        return ResultUtil.error(Messages.API_AUTHEXCEPTION_CODE, Messages.API_AUTHEXCEPTION_MSG);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultCode handleException(Exception e) {
        e.printStackTrace();
        return ResultUtil.error(Messages.API_EXCEPTION_CODE, Messages.API_EXCEPTION_MSG);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissInputParamException(MissingServletRequestParameterException e, HttpServletResponse response) throws IOException {
        ResultCode<String> resultCode = new ResultCode<>();
        resultCode.setCode(Messages.MISSING_INPUT_CODE);
        resultCode.setMsg(Messages.MISSING_INPUT_MSG + ": " + e.getParameterName());
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.getWriter().print(JsonMapper.toJsonString(resultCode));
    }
}
