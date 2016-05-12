package cn.thinkjoy.sms.domain;

import java.io.Serializable;

/**
 * Created by gryang on 16/05/11.
 */
public class SMSChannel implements Serializable {
    private static final long serialVersionUID = 8467975548471765580L;

    /*
    渠道名称
     */
    private String channelName;

    /*
    渠道描述
     */
    private String channelDesc;

    public SMSChannel() {
    }

    public SMSChannel(String channelName, String channelDesc) {
        this.channelName = channelName;
        this.channelDesc = channelDesc;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
    }

    @Override
    public String toString() {
        return "SMSChannel{" +
                "channelName='" + channelName + '\'' +
                ", channelDesc='" + channelDesc + '\'' +
                '}';
    }
}
