package cn.thinkjoy.sms.impl;

import cn.thinkjoy.sms.api.SMSService;
import cn.thinkjoy.sms.dao.SMSDao;
import cn.thinkjoy.sms.domain.SMSCheckCode;
import cn.thinkjoy.sms.domain.SMSSend;
import cn.thinkjoy.sms.domain.SMSSendVipCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gryang on 16/05/11.
 */
@Service("smsService")
public class SMSServiceImpl implements SMSService, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(SMSServiceImpl.class);

    @Autowired
    private CloopenSMSServiceImpl cloopenSMSService;

    @Autowired
    private UcpaasSMSServiceImpl ucpaasSMSService;

    @Autowired
    private SMSDao smsDao;

    private List<SMSSend> sendList = new ArrayList<>();

    private List<SMSSend> sendNewList = new ArrayList<>();

    private boolean threadFlag = true;

    /**
     * true sendList
     * false sendNewList
     */
    private AtomicBoolean needChange = new AtomicBoolean();

    @Override
    public boolean sendSMS(SMSCheckCode smsCheckCode, boolean isChange) {

        if (smsCheckCode.getBizTarget() == null) {
            logger.error("短信发送异常，业务标识缺失，内容如下：" + smsCheckCode.toString());
            return false;
        }

        if (smsCheckCode.getCheckCode() == null || "".equals(smsCheckCode.getCheckCode())) {
            logger.error("短信发送异常，验证码为空，内容如下：" + smsCheckCode.toString());
            return false;
        }

        String smsKey = getChannel(smsCheckCode.getBizTarget());
        switch (smsKey) {
            case CloopenSMSServiceImpl.smsKey:

                if (!isChange) {
                    return cloopenSMSService.sendSMS(smsCheckCode, isChange);
                }
                smsDao.saveChannelChange(CloopenSMSServiceImpl.smsKey, UcpaasSMSServiceImpl.smsKey);
                return ucpaasSMSService.sendSMS(smsCheckCode, isChange);

            case UcpaasSMSServiceImpl.smsKey:

                if (!isChange) {
                    return ucpaasSMSService.sendSMS(smsCheckCode, isChange);
                }
                smsDao.saveChannelChange(UcpaasSMSServiceImpl.smsKey, CloopenSMSServiceImpl.smsKey);
                return cloopenSMSService.sendSMS(smsCheckCode, isChange);

            default:
                return cloopenSMSService.sendSMS(smsCheckCode, isChange);
        }

    }

    @Override
    public boolean sendVoiceSMS(SMSCheckCode smsCheckCode, boolean isChange) {
        if (smsCheckCode.getBizTarget() == null) {
            logger.error("短信发送异常，业务标识缺失，内容如下：" + smsCheckCode.toString());
            return false;
        }

        if (smsCheckCode.getCheckCode() == null || "".equals(smsCheckCode.getCheckCode())) {
            logger.error("短信发送异常，验证码为空，内容如下：" + smsCheckCode.toString());
            return false;
        }

        switch (getChannel(smsCheckCode.getBizTarget())) {
            case CloopenSMSServiceImpl.smsKey:

                if (!isChange) {
                    return cloopenSMSService.sendVoiceSMS(smsCheckCode, isChange);
                }
                smsDao.saveChannelChange(CloopenSMSServiceImpl.smsKey, UcpaasSMSServiceImpl.smsKey);
                return ucpaasSMSService.sendVoiceSMS(smsCheckCode, isChange);

            case UcpaasSMSServiceImpl.smsKey:

                if (!isChange) {
                    return ucpaasSMSService.sendVoiceSMS(smsCheckCode, isChange);
                }
                smsDao.saveChannelChange(UcpaasSMSServiceImpl.smsKey, CloopenSMSServiceImpl.smsKey);
                return cloopenSMSService.sendVoiceSMS(smsCheckCode, isChange);

            default:
                return cloopenSMSService.sendVoiceSMS(smsCheckCode, isChange);
        }
    }

    /**
     * 发送vip卡号
     *
     * @param smsSendVipCard
     * @return
     */
    @Override
    public boolean sendVipCard(SMSSendVipCard smsSendVipCard) {
        return cloopenSMSService.sendVipCard(smsSendVipCard);
    }

    private String getChannel(String target) {
        if (needChange.get()) {
            for(SMSSend send : sendNewList) {
                if (send.getBizTarget().equals(target)) {
                    return send.getSendChannel();
                }
            }
        } else {
            for(SMSSend send : sendList) {
                if (send.getBizTarget().equals(target)) {
                    return send.getSendChannel();
                }
            }
        }

        return "";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        sendList.addAll(smsDao.getSendChannels());
        needChange.set(false);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (threadFlag) {
                    try {
                        Thread.sleep(60000);
                        if (needChange.get()) {
                            sendList.clear();
                            sendList.addAll(smsDao.getSendChannels());
                            needChange.set(false);
                        } else {
                            sendNewList.clear();
                            sendNewList.addAll(smsDao.getSendChannels());
                            needChange.set(true);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setName("SMS channel update Thread");
        thread.start();
    }

    @Override
    public void destroy() throws Exception {
        threadFlag = false;
    }
}
