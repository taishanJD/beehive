package com.quarkdata.yunpan.api.message;

import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVO;
import org.apache.commons.collections.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author liuda
 * @Description 消息通知工具类
 */
public class MessageUtils {
    /**
     *
     * @param request
     * @param notice "1"操作不成功   "0"操作成功
     * @param messageType 操作的类型
     * @param documentIds  操作的文档集合，单条文档或多条文档
     * @param receiverList 消息通知需要提醒的人 Receiver类型{recId:接受者id  recType：接受者类型     permission:给与的权限}
     */
    public static void addMessage(HttpServletRequest request,String notice ,String messageType,List<Long> documentIds, List<Receiver> receiverList) {
        request.setAttribute("notice ",notice );
        request.setAttribute("MessageType",messageType);
        request.setAttribute("documentIds",documentIds);
        request.setAttribute("receiverList",receiverList);
    }

    public static void createMessage(HttpServletRequest request, Integer notice, String messageType, Long docId, List<DocumentPermissionVO> permissions) {
        List<Long> documentIds = new ArrayList<>();
        documentIds.add(docId);
        ArrayList<Receiver> receivers = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(permissions)) {
            for(DocumentPermissionVO documentPermissionVO: permissions) {
                Receiver receiver = new Receiver();
                receiver.setRecId(documentPermissionVO.getId().toString());
                receiver.setRecType(documentPermissionVO.get_type());
                receiver.setPermission(documentPermissionVO.getPermission());
                receivers.add(receiver);
            }
            MessageUtils.addMessage(request, String.valueOf(notice), messageType, documentIds, receivers);
        }
    }

    /**
     * 创建组织空间,创建组织文件夹,设置组织文件权限时添加消息通知
     * @param request
     * @param notice
     * @param messageType
     * @param documentIds
     * @param permissions
     */
    public static void createMessage(HttpServletRequest request, Integer notice, String messageType, List<Long> documentIds, List<DocumentPrivilegeVO> permissions) {
        ArrayList<Receiver> receivers = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(permissions)) {
            for(DocumentPrivilegeVO documentPrivilegeVO: permissions) {
                if(!(documentPrivilegeVO.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)) && documentPrivilegeVO.getReceiverId().equals(UserInfoUtil.getUserInfoVO().getUser().getUserId())) {
                    Receiver receiver = new Receiver();
                    receiver.setRecId(documentPrivilegeVO.getReceiverId().toString());
                    receiver.setRecType(documentPrivilegeVO.getReceiverType());
                    receiver.setPermission(documentPrivilegeVO.getPermission());
                    receivers.add(receiver);
                }
            }
            MessageUtils.addMessage(request, String.valueOf(notice), messageType, documentIds, receivers);
        }
    }
}
