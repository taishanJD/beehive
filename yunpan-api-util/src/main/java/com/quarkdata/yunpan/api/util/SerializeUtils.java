package com.quarkdata.yunpan.api.util;

import java.io.*;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/10.
 */
public class SerializeUtils {

    public static byte[] serialize(Object obj){
        byte[] bytes = null;
        try {
            ByteArrayOutputStream baos=new ByteArrayOutputStream();;
            ObjectOutputStream oos=new ObjectOutputStream(baos);
            oos.writeObject(obj);
            bytes=baos.toByteArray();
            baos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
    public static Object deSerialize(byte[] bytes){
        Object obj=null;
        try {
            ByteArrayInputStream bais=new ByteArrayInputStream(bytes);
            ObjectInputStream ois=new ObjectInputStream(bais);
            obj=ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
