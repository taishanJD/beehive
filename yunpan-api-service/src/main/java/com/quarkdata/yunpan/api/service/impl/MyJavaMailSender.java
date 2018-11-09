package com.quarkdata.yunpan.api.service.impl;

import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MyJavaMailSender extends JavaMailSenderImpl {

    private static MyJavaMailSender instance;

    private MyJavaMailSender(){

    }

    public static MyJavaMailSender getInstance() {
        if (instance == null) {
            synchronized (MyJavaMailSender.class) {
                if (instance == null) {
                    instance = new MyJavaMailSender();
                }
            }
        }
        return instance;
    }
}
