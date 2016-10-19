package cn.thinkjoy.sms.domain;

import java.io.Serializable;

/**
 * Created by gryang on 16/05/11.
 */
public class SMSSendVipCard implements Serializable {
    private static final long serialVersionUID = 7333932514146799165L;
    /**
     * 电话
     */
    private String phone;

    /**
     * 卡号
     */
    private String cardNumber;

    /**
     * 密码
     */
    private String password;

    /**
     * 渠道业务标识
     */
    private String bizTarget;

    /**
     * 发送状态
     */
    private String sendResult;


    /**
     * 状态码
     */
    private String resultCode;


    public SMSSendVipCard() {
    }

    public SMSSendVipCard(String phone, String cardNumber, String password, String bizTarget) {
        this.phone = phone;
        this.cardNumber = cardNumber;
        this.password = password;
        this.bizTarget = bizTarget;
    }

    public SMSSendVipCard(String phone, String cardNumber, String password, String bizTarget, String sendResult, String resultCode) {
        this.phone = phone;
        this.cardNumber = cardNumber;
        this.password = password;
        this.bizTarget = bizTarget;
        this.sendResult = sendResult;
        this.resultCode = resultCode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBizTarget() {
        return bizTarget;
    }

    public void setBizTarget(String bizTarget) {
        this.bizTarget = bizTarget;
    }

    public String getSendResult() {
        return sendResult;
    }

    public void setSendResult(String sendResult) {
        this.sendResult = sendResult;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "SMSSendVipCard{" +
                "phone='" + phone + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", password='" + password + '\'' +
                ", bizTarget='" + bizTarget + '\'' +
                ", sendResult='" + sendResult + '\'' +
                ", resultCode='" + resultCode + '\'' +
                '}';
    }
}
