package cn.thinkjoy.sms.api;

import cn.thinkjoy.sms.domain.SMSCheckCode;
import cn.thinkjoy.sms.domain.SMSSendVipCard;

/**
 * Created by gryang on 16/05/11.
 */
public interface SMSService {

    public static final String SMS_TXT = "txt";
    public static final String SMS_VOICE = "voice";

    /**
     * 发送短信
     * @param smsCheckCode   短信内容
     * @param isChange  是否改变发送渠道
     * @return
     */
    boolean sendSMS(SMSCheckCode smsCheckCode, boolean isChange);

    /**
     * 发送语音短信
     * @param smsCheckCode
     * @param isChange
     * @return
     */
    boolean sendVoiceSMS(SMSCheckCode smsCheckCode, boolean isChange);


    /**
     * 发送vip卡号
     * @return
     */
    boolean sendVipCard(SMSSendVipCard smsSendVipCard);
}
