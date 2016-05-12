package cn.thinkjoy.sms.domain;

import java.io.Serializable;

/**
 * Created by gryang on 16/05/11.
 */
public class APPSMSChannel implements Serializable {

    private static final long serialVersionUID = 5180849578204261081L;

    private String app;

    private String smsKey;

    private String nextSmsKey;

    private String mobile;

    private String unicom;

    private String telecom;

    private String hasVoice;

    private String isDefault;

    public APPSMSChannel() {
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getSmsKey() {
        return smsKey;
    }

    public void setSmsKey(String smsKey) {
        this.smsKey = smsKey;
    }

    public String getNextSmsKey() {
        return nextSmsKey;
    }

    public void setNextSmsKey(String nextSmsKey) {
        this.nextSmsKey = nextSmsKey;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUnicom() {
        return unicom;
    }

    public void setUnicom(String unicom) {
        this.unicom = unicom;
    }

    public String getTelecom() {
        return telecom;
    }

    public void setTelecom(String telecom) {
        this.telecom = telecom;
    }

    public String getHasVoice() {
        return hasVoice;
    }

    public void setHasVoice(String hasVoice) {
        this.hasVoice = hasVoice;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "APPSMSChannel{" +
                "app='" + app + '\'' +
                ", smsKey='" + smsKey + '\'' +
                ", nextSmsKey='" + nextSmsKey + '\'' +
                ", mobile='" + mobile + '\'' +
                ", unicom='" + unicom + '\'' +
                ", telecom='" + telecom + '\'' +
                ", hasVoice='" + hasVoice + '\'' +
                ", isDefault='" + isDefault + '\'' +
                '}';
    }
}
