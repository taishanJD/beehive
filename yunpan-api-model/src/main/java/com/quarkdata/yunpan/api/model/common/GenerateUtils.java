package com.quarkdata.yunpan.api.model.common;

import java.util.Random;

public class GenerateUtils {

    public static String generatePsw(int len,boolean isNumberic){
        String base = null;
        if(isNumberic){
            base = "0123456789";
        }else{
//            base = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            base = "0123456789abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
        }

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
