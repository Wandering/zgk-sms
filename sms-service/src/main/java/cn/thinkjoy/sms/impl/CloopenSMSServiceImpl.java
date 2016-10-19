package cn.thinkjoy.sms.impl;

import cn.thinkjoy.sms.api.SMSService;
import cn.thinkjoy.sms.dao.SMSDao;
import cn.thinkjoy.sms.domain.SMSCheckCode;
import cn.thinkjoy.sms.domain.SMSSendVipCard;
import cn.thinkjoy.sms.domain.SMSStatus;
import com.cloopen.rest.sdk.CCPRestSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gryang on 16/05/11.
 */
@Service("cloopenSMSService")
public class CloopenSMSServiceImpl implements SMSService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(CloopenSMSServiceImpl.class);

    private CCPRestSDK restAPI;

    //容联短信key
    public final String smsKey = "cloopen";

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SMSDao smsDao;

    @Override
    public boolean sendSMS(SMSCheckCode smsCheckCode, boolean isChange) {

        HashMap<String, Object> result = restAPI.sendTemplateSMS(
            smsCheckCode.getPhone(), "86254", new String[]{smsCheckCode.getCheckCode()});
        String statusCode = result.get("statusCode").toString();

        final SMSStatus status = new SMSStatus(
                smsCheckCode.getBizTarget(),
                smsCheckCode.getPhone(),
                smsCheckCode.getCheckCode(),
                SMS_TXT,
                getSMSCodeResult(statusCode),
                statusCode,
                smsKey);

        logger.info("cloopen status is:" + status);

        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    smsDao.saveCheckCode(status);
                    if(!status.getResultCode().equals("000000")){
                        return false;
                    }
                    return smsDao.saveSMSStatus(status);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    transactionStatus.setRollbackOnly();
                    return false;
                }
            }
        });
    }

    @Override
    public boolean sendVoiceSMS(SMSCheckCode smsCheckCode, boolean isChange) {
        logger.info("voice sms info is:" + smsCheckCode.toString());

        if (smsCheckCode.getCheckCode() == null) {
            return false;
        }

        HashMap<String, Object> result = restAPI.voiceVerify(
                smsCheckCode.getCheckCode(),
                smsCheckCode.getPhone(),"", "3","http://172.16.130.66:8080/sms/voiceVerify");

        String statusCode = result.get("statusCode").toString();

        Map<String, Object> data = (Map<String, Object>) result.get("data");
        Map<String, Object> voiceVerify = (Map<String, Object>) data.get("VoiceVerify");
        String callSid = voiceVerify.get("callSid").toString();

        final SMSStatus status = new SMSStatus(
                smsCheckCode.getBizTarget(),
                smsCheckCode.getPhone(),
                smsCheckCode.getCheckCode(),
                getSMSCodeResult(statusCode),
                statusCode,
                smsKey,
                SMS_VOICE, callSid);

        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    smsDao.saveCheckCode(status);
                    return smsDao.saveSMSStatus(status);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    transactionStatus.setRollbackOnly();
                    return false;
                }
            }
        });
    }

    /**
     * 发送vip卡号
     *
     * @param smsSendVipCard
     * @return
     */
    @Override
    public boolean sendVipCard(SMSSendVipCard smsSendVipCard) {

        HashMap<String, Object> result = restAPI.sendTemplateSMS(
                smsSendVipCard.getPhone(), "124342", new String[]{smsSendVipCard.getCardNumber(),smsSendVipCard.getPassword()});
        String statusCode = result.get("statusCode").toString();


        final SMSSendVipCard sendVipCard = new SMSSendVipCard(smsSendVipCard.getPhone(),
                smsSendVipCard.getCardNumber(),smsSendVipCard.getPassword(),
                smsSendVipCard.getBizTarget(),
                getSMSCodeResult(statusCode),statusCode);


        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    smsDao.saveVipCard(sendVipCard);
                    if(!sendVipCard.getResultCode().equals("000000")){
                        return false;
                    }
                    return smsDao.saveVipCard(sendVipCard);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    transactionStatus.setRollbackOnly();
                    return false;
                }
            }
        });
    }

    /**
     * 获取错误码对应的文字描述
     * @param statusCode
     * @return
     */
    private String getSMSCodeResult(String statusCode) {
        switch (statusCode) {
            case "000000":
                return "success";
            case "112300":
                return ErrorCode.ERROR_112300.getCode();
            case "112301":
                return ErrorCode.ERROR_112301.getCode();
            case "112302":
                return ErrorCode.ERROR_112302.getCode();
            case "112303":
                return ErrorCode.ERROR_112303.getCode();
            case "112304":
                return ErrorCode.ERROR_112304.getCode();
            case "112305":
                return ErrorCode.ERROR_112305.getCode();
            case "112306":
                return ErrorCode.ERROR_112306.getCode();
            case "112307":
                return ErrorCode.ERROR_112307.getCode();
            case "112308":
                return ErrorCode.ERROR_112308.getCode();
            case "112309":
                return ErrorCode.ERROR_112309.getCode();
            case "112310":
                return ErrorCode.ERROR_112310.getCode();
            case "112311":
                return ErrorCode.ERROR_112311.getCode();
            case "160021":
                return ErrorCode.ERROR_160021.getCode();
            default:
                logger.error("error code is:" + statusCode);
                return "未知异常";
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a48b5515493a1b7015498c52ade066a", "c4e1c9418e5e4921bbd120598e0324dd");// 初始化主帐号名称和主帐号令牌
        restAPI.setAppId("aaf98f8954939ed501549e768a470e41");// 初始化应用ID

    }


    public static void main(String[] args) {
//        restAPI = new CCPRestSDK();
//        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
//        restAPI.setAccount("8a48b5515493a1b7015498c52ade066a", "c4e1c9418e5e4921bbd120598e0324dd");// 初始化主帐号名称和主帐号令牌
//        restAPI.setAppId("aaf98f8954939ed501549e768a470e41");// 初始化应用ID
        new CloopenSMSServiceImpl().sendVipCard(new SMSSendVipCard("18629242959","1231212313","123123", "zgk"));
    }

    public enum ErrorCode {
        ERROR_112300("接收短信的手机号码为空"),
        ERROR_112301("短信正文为空"),
        ERROR_112302("群发短信已暂停"),
        ERROR_112303("应用未开通短信功能"),
        ERROR_112304("短信内容的编码转换有误"),
        ERROR_112305("应用未上线，短信接收号码外呼受限"),
        ERROR_112306("接收模板短信的手机号码为空"),
        ERROR_112307("模板短信模板ID为空"),
        ERROR_112308("模板短信模板data参数为空"),
        ERROR_112309("模板短信内容的编码转换有误"),
        ERROR_112310("应用未上线，模板短信接收号码外呼受限"),
        ERROR_112311("短信模板不存在"),
        ERROR_160021("同样手机相同内容限定时间内只能发送一次");

        private String code;

        ErrorCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}

