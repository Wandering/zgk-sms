package cn.thinkjoy.sms;

import cn.thinkjoy.sms.api.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by gryang on 16/05/11.
 */
public class SMSStarter {

    @Autowired
    SMSService smsService;

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/*.xml");
        context.start();
        System.out.println("sms dubbo start ~~~~");
        System.in.read();
    }
}

