package cn.thinkjoy.sms;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by gryang on 16/05/11.
 */
public class SMSStarter {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/*.xml");
        context.start();
        System.out.println("sms dubbo start ~~~~");
        System.in.read();
    }
}

