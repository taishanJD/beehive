package com.quarkdata.yunpan.api.util;

import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;

/**
 * @author maorl
 * @date 12/6/17.
 */
public class ResultUtil {

    public static ResultCode success(Object object) {
        ResultCode result = new ResultCode();
        result.setCode(Messages.SUCCESS_CODE);
        result.setMsg(Messages.SUCCESS_MSG);
        result.setData(object);
        return result;
    }

    public static ResultCode success() {
        return success(null);
    }

    public static ResultCode error(Integer code, String msg) {
        ResultCode result = new ResultCode();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
