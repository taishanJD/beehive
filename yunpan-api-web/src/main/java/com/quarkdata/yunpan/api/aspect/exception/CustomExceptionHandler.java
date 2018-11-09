package com.quarkdata.yunpan.api.aspect.exception;

import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yanyq1129@thundersoft.com on 2018/6/20.
 */
@ControllerAdvice
public class CustomExceptionHandler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = FileIsDeleteException.class)
    public void fileNotExistHandler(HttpServletResponse response) throws IOException {
        ResultCode<String> result = new ResultCode<>();
        result.setCode(Messages.FILE_IS_DELETE_CODE);
        result.setMsg(Messages.FILE_IS_DELETE_MSG);
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().print(JsonMapper.toJsonString(result));
    }

    @ExceptionHandler(value = PerUserSpaceNotEnoughException.class)
    public void isEnoughPerUserSpaceHandler(HttpServletResponse response) throws IOException {
        ResultCode<String> result = new ResultCode<>();
        result.setCode(Messages.NO_ENOUGH_PERSONAL_SPACE_CODE);
        result.setMsg(Messages.NO_ENOUGH_PERSONAL_SPACE_MSG);
        logger.error("<<<<<< 上传文件: 个人空间不足 <<<<<<");
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
            response.setStatus(HttpStatus.SC_OK);
        response.getWriter().print(JsonMapper.toJsonString(result));
    }

    @ExceptionHandler(value = IncSpaceNotEnopughException.class)
    public void isEnoughIncSpaceHandler(HttpServletResponse response) throws IOException {
        ResultCode<String> result = new ResultCode<>();
        result.setCode(Messages.NO_ENOUGH_INC_SPACE_CODE);
        result.setMsg(Messages.NO_ENOUGH_INC_SPACE_MSG);
        logger.error("<<<<<< 上传文件: 组织空间不足 <<<<<<");
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.SC_OK);
        response.getWriter().print(JsonMapper.toJsonString(result));
    }

    @ExceptionHandler(value = FileIsNotShareException.class)
    public void fileNotShareHandler(HttpServletResponse response) throws IOException {
        ResultCode<String> result = new ResultCode<>();
        result.setCode(Messages.FILE_NOT_SHARE_TO_YOU_CODE);
        result.setMsg(Messages.FILE_NOT_SHARE_TO_YOU_MSG);
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.SC_OK);
        response.getWriter().print(JsonMapper.toJsonString(result));
    }

    @ExceptionHandler(value = FileIsLockedException.class)
    public void fileIsLockedHandler(HttpServletResponse response) throws IOException {
        ResultCode<String> result = new ResultCode<>();
        result.setCode(Messages.DOCUMENT_STATUS_LOCK_CODE);
        result.setMsg(Messages.DOCUMENT_STATUS_LOCK_MSG);
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().print(JsonMapper.toJsonString(result));
    }

}
