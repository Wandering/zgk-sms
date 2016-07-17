package cn.thinkjoy.sms.impl;

import cn.thinkjoy.sms.api.SMSService;
import cn.thinkjoy.sms.dao.SMSDao;
import cn.thinkjoy.sms.domain.SMSCheckCode;
import cn.thinkjoy.sms.domain.SMSStatus;
import cn.thinkjoy.sms.util.EncryptUtil;
import com.alibaba.fastjson.JSON;
import com.cloopen.rest.sdk.utils.DateUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gryang on 16/05/11.
 */
@Service("ucpaasSMSService")
public class UcpaasSMSServiceImpl implements SMSService {

    private static final Logger logger = LoggerFactory.getLogger(UcpaasSMSServiceImpl.class);

    //云之讯短信key
    public final static String smsKey = "ucpaas";

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final String accountSid = "220c952ee4e96f405fd0629e11e9fdc4";
    private static final String token = "13f74832478149d28df14d8d3df9afeb";
    private static final String appId = "79613f16972e4a01ab3af13ca2b721d9";
    private static final String templateId="25682";

    private EncryptUtil encryptUtil = new EncryptUtil();

    @Autowired
    private SMSDao smsDao;

    @Override
    public boolean sendSMS(SMSCheckCode smsCheckCode, boolean isChange) {
        try {
            String timestamp = DateUtil.dateToStr(new Date(),
                    DateUtil.DATE_TIME_NO_SLASH);// 时间戳

            String url = getStringBuffer()
                    .append("/Messages/templateSMS")
                    .append("?sig=").append(getSignature(timestamp)).toString();

            Map<String, Object> map = new HashMap<>();
            map.put("appId", appId);
            map.put("templateId", templateId);
            map.put("to", smsCheckCode.getPhone());
            map.put("param", smsCheckCode.getCheckCode());

            String body = JSON.toJSONString(map);
            body="{\"templateSMS\":"+body+"}";

            BasicHttpEntity requestBody = new BasicHttpEntity();
            requestBody.setContent(new ByteArrayInputStream(body.getBytes("UTF-8")));
            requestBody.setContentLength(body.getBytes("UTF-8").length);
            HttpResponse response = Request.Post(url)
                    .setHeader("Accept", "application/json")
                    .setHeader("Content-Type", "application/json;charset=utf-8")
                    .setHeader("Authorization", encryptUtil.base64Encoder(accountSid + ":" + timestamp))
                    .body(requestBody)
                    .socketTimeout(6000)
                    .connectTimeout(6000)
                    .execute()
                    .returnResponse();

            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);

            Map<String, Object> responseMap = JSON.parseObject(responseBody, Map.class);

            Map<String, Object> resp = (Map<String, Object>) responseMap.get("resp");
            String respCode = resp.get("respCode").toString();
            Map<String, Object> templateSMS = (Map<String, Object>) resp.get("templateSMS");
            String smsId = templateSMS.get("smsId").toString();
            System.err.println("respCode:" + respCode + " smsId:" + smsId);

            final SMSStatus status = new SMSStatus(
                    smsCheckCode.getBizTarget(),
                    smsCheckCode.getPhone(),
                    smsCheckCode.getCheckCode(),
                    SMS_TXT,
                    getErrorInfo(respCode),
                    respCode,
                    smsKey);

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
                        transactionStatus.setRollbackOnly();
                        return false;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendVoiceSMS(SMSCheckCode smsCheckCode, boolean isChange) {
        try {
            String timestamp = DateUtil.dateToStr(new Date(),
                    DateUtil.DATE_TIME_NO_SLASH);// 时间戳

            String url = getStringBuffer()
                    .append("/Calls/voiceCode")
                    .append("?sig=").append(getSignature(timestamp)).toString();

            Map<String, Object> map = new HashMap<>();
            map.put("appId", appId);
            map.put("verifyCode", smsCheckCode.getCheckCode());
            map.put("to", smsCheckCode.getPhone());

            String body = JSON.toJSONString(map);
            body="{\"voiceCode\":"+body+"}";

            BasicHttpEntity requestBody = new BasicHttpEntity();
            requestBody.setContent(new ByteArrayInputStream(body.getBytes("UTF-8")));
            requestBody.setContentLength(body.getBytes("UTF-8").length);
            HttpResponse response = Request.Post(url)
                    .setHeader("Accept", "application/json")
                    .setHeader("Content-Type", "application/json;charset=utf-8")
                    .setHeader("Authorization", encryptUtil.base64Encoder(accountSid + ":" + timestamp))
                    .body(requestBody)
                    .socketTimeout(6000)
                    .connectTimeout(6000)
                    .execute()
                    .returnResponse();

            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);

            Map<String, Object> responseMap = JSON.parseObject(responseBody, Map.class);

            Map<String, Object> resp = (Map<String, Object>) responseMap.get("resp");
            String respCode = resp.get("respCode").toString();

            final SMSStatus status = new SMSStatus(
                    smsCheckCode.getBizTarget(),
                    smsCheckCode.getPhone(),
                    smsCheckCode.getCheckCode(),
                    SMS_VOICE,
                    getErrorInfo(respCode),
                    respCode,
                    smsKey);

//            System.err.println(status.toString());
//            return false;

            return transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(TransactionStatus transactionStatus) {
                    try {
                        smsDao.saveCheckCode(status);
                        return smsDao.saveSMSStatus(status);
                    } catch (Exception e) {
                        transactionStatus.setRollbackOnly();
                        return false;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private String getErrorInfo(String response) {
        switch (response) {
            case "000000":
                return "success";
            case "100000":
                return "金额不为整数";
            case "100001":
                return "余额不足";
            case "100002":
                return "数字非法";
            case "100003":
                return "不允许有空值";
            case "100004":
                return "枚举类型取值错误";
            case "100005":
                return "访问IP不合法";
            case "100006":
                return "手机号不合法";
            case "100500":
                return "HTTP状态码不等于200";
            case "100007":
                return "查无数据";
            case "100008":
                return "手机号码为空";
            case "100009":
                return "手机号为受保护的号码";
            case "100010":
                return "登录邮箱或手机号为空";
            case "100011":
                return "邮箱不合法";
            case "100012":
                return "密码不能为空";
            case "100013":
                return "没有测试子账号";
            case "100014":
                return "金额过大,不要超过12位数字";
            case "100015":
                return "号码不合法";
            case "100016":
                return "余额被冻结";
            case "100017":
                return "余额已注销";
            case "100018":
                return "通话时长需大于60秒";
            case "100019":
                return "应用余额不足";
            case "100699":
                return "系统内部错误";


            case "104102":
                return "验证码为空";
            case "104103":
                return "显示号码不合法";
            case "104104":
                return "语音验证码位4-8位数字";
            case "104106":
                return "语音通知类型错误";
            case "104107":
                return "语音通知内容为空";
            case "104108":
                return "语音ID非法";
            case "104109":
                return "文本内容存储失败";
            case "104110":
                return "语音文件不存在或未审核";


            case "105100":
                return "短信服务请求异常";
            case "105101":
                return "url关键参数为空";
            case "105102":
                return "号码不合法";
            case "105103":
                return "没有通道类别";
            case "105104":
                return "该类别为冻结状态";
            case "105105":
                return "没有足够金额";
            case "105106":
                return "不是国内手机号码并且不是国际电话";
            case "105107":
                return "黑名单";
            case "105108":
                return "含非法关键字";
            case "105109":
                return "该通道类型没有第三方通道";
            case "105110":
                return "短信模板ID不存在";
            case "105111":
                return "短信模板未审核通过";
            case "105112":
                return "短信模板替换个数与实际参数个数不匹配";
            case "105113":
                return "短信模板ID为空";
            case "105114":
                return "短信内容为空";
            case "105115":
                return "短信类型长度应为1";
            case "105116":
                return "同一天同一用户不能发超过3条相同的短信";
            case "105117":
                return "模板ID含非法字符";
            case "105118":
                return "短信模板有替换内容，但参数为空";
            case "105119":
                return "短信模板替换内容过长，不能超过70个字符";
            case "105120":
                return "手机号码不能超过100个";
            case "105121":
                return "短信模板已删除";
            case "105122":
                return "同一天同一用户不能发超过N条验证码(n为用户自己配置)";
            default:
                return "未知异常";
        }
    }

    private String getSignature(String timestamp) throws Exception{
        String sig = accountSid + token + timestamp;
        String signature = encryptUtil.md5Digest(sig);
        return signature;
    }

    private StringBuffer getStringBuffer() {
        StringBuffer sb = new StringBuffer("https://api.ucpaas.com/2014-06-30/Accounts/");
        sb.append(accountSid);
        return sb;
    }

    public static void main(String[] args) {
        new UcpaasSMSServiceImpl().sendSMS(new SMSCheckCode("18629242959", "1111", "zgk",null), false);
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        smsServiceNew.registerSMSProvider(smsKey, this);
//    }
}
