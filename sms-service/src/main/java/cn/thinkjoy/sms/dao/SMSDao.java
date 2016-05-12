package cn.thinkjoy.sms.dao;

import cn.thinkjoy.sms.domain.APPSMSChannel;
import cn.thinkjoy.sms.domain.SMSSend;
import cn.thinkjoy.sms.domain.SMSStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by gryang on 16/05/11.
 */
public interface SMSDao {
    /**
     * 保存短信信息
     * @param status
     * @return
     */
    boolean saveCheckCode(SMSStatus status);

    /**
     * 保存发送状态
     * @param status
     * @return
     */
    boolean saveSMSStatus(SMSStatus status);

    /**
     * 获取短信发送渠道信息集合
     * @return
     */
    List<SMSSend> getSendChannels();

    /**
     * 记录渠道变更信息
     * @param source
     * @param target
     * @return
     */
    boolean saveChannelChange(
            @Param("source") String source,
            @Param("target") String target
    );

    /**
     * 获取应用短信渠道关系
     * @param app
     * @return
     */
    List<APPSMSChannel> getAPPSMSChannel(@Param("app") String app);
}
