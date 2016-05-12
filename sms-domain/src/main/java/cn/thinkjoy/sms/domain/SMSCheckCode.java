package cn.thinkjoy.sms.domain;

import java.io.Serializable;

/**
 * Created by gryang on 16/05/11.
 */
public class SMSCheckCode implements Serializable {
    private static final long serialVersionUID = 7333932514146799165L;
    /*
    电话
     */
    private String phone;

    /*
    验证码
     */
    private String checkCode;

    /*
    渠道业务标识
     */
    private String bizTarget;

    public SMSCheckCode() {
    }

    public SMSCheckCode(String phone, String checkCode, String bizTarget) {
        this.phone = phone;
        this.checkCode = checkCode;
        this.bizTarget = bizTarget;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getBizTarget() {
        return bizTarget;
    }

    public void setBizTarget(String bizTarget) {
        this.bizTarget = bizTarget;
    }

    @Override
    public String toString() {
        return "SMSCheckCode{" +
                "phone='" + phone + '\'' +
                ", checkCode='" + checkCode + '\'' +
                ", bizTarget='" + bizTarget + '\'' +
                '}';
    }
}
