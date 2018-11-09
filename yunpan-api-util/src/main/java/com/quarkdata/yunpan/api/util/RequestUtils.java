package com.quarkdata.yunpan.api.util;

import com.quarkdata.yunpan.api.model.common.PlatformConstants;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static String isClient(HttpServletRequest request) {
        String client = request.getHeader("Client");
        if(client == null) {
            client = request.getParameter("client");
        }
        if(client == null) {
            return "";
        } else if(client.toLowerCase().equals(PlatformConstants.ANDROID)) {
            return PlatformConstants.ANDROID;
        } else if(client.toLowerCase().equals(PlatformConstants.IOS)) {
            return PlatformConstants.IOS;
        } else if(client.toLowerCase().equals(PlatformConstants.PC)) {
            return PlatformConstants.PC;
        } else if(client.toLowerCase().equals(PlatformConstants.MAC)) {
            return PlatformConstants.MAC;
        }
        return "";
    }

}
