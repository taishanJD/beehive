package com.quarkdata.yunpan.api.util.document;

import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;

/**
 * Created by yanyq1129@thundersoft.com on 2018/9/10.
 */
public class DocumentPermissionUtils {

    public static Boolean hasPermission(DocumentPermission documentPermission, Integer permissionIndex) {
        if(DocumentPermissionConstants.HAS_PERMISSION.equals(documentPermission.getPermission().charAt(permissionIndex))) {
            return true;
        }
        return false;
    }
}
