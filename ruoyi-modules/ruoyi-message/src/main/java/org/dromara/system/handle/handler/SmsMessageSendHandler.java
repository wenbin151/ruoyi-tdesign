package org.dromara.system.handle.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.dromara.common.core.enums.CommonStatusEnum;
import org.dromara.common.core.enums.MessageSupplierTypeEnum;
import org.dromara.common.core.enums.MessageTemplateMode;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.aliyun.config.AlibabaFactory;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.cloopen.config.CloopenConfig;
import org.dromara.sms4j.cloopen.config.CloopenFactory;
import org.dromara.sms4j.ctyun.config.CtyunConfig;
import org.dromara.sms4j.ctyun.config.CtyunFactory;
import org.dromara.sms4j.emay.config.EmayConfig;
import org.dromara.sms4j.emay.config.EmayFactory;
import org.dromara.sms4j.huawei.config.HuaweiConfig;
import org.dromara.sms4j.huawei.config.HuaweiFactory;
import org.dromara.sms4j.jdcloud.config.JdCloudConfig;
import org.dromara.sms4j.jdcloud.config.JdCloudFactory;
import org.dromara.sms4j.netease.config.NeteaseConfig;
import org.dromara.sms4j.netease.config.NeteaseFactory;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.dromara.sms4j.tencent.config.TencentFactory;
import org.dromara.sms4j.unisms.config.UniConfig;
import org.dromara.sms4j.unisms.config.UniFactory;
import org.dromara.sms4j.yunpian.config.YunPianFactory;
import org.dromara.sms4j.yunpian.config.YunpianConfig;
import org.dromara.sms4j.zhutong.config.ZhutongConfig;
import org.dromara.sms4j.zhutong.config.ZhutongFactory;
import org.dromara.system.domain.SysMessageConfig;
import org.dromara.system.domain.SysMessageTemplate;
import org.dromara.system.handle.BaseMessageSendHandler;
import org.dromara.system.service.ISysMessageLogService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信消息发送处理类
 *
 * @author hexm
 * @date 2023/07/24 11:00
 */
@Component
public class SmsMessageSendHandler extends BaseMessageSendHandler {

    public SmsMessageSendHandler(ISysMessageLogService messageLogService) {
        super(messageLogService);
    }

    /**
     * 获取消息类型
     */
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.SMS;
    }

    /**
     * 发送消息
     *
     * @param account  账号
     * @param message  消息变量
     * @param template 模板对象
     * @param config   消息配置
     */
    @Override
    public void send(List<String> account, Map<String, Object> message, SysMessageTemplate template, SysMessageConfig config) {
        LinkedHashMap<String, String> outputVars = getOutputVars(template, message);
        String content = getContent(template, outputVars);
        MessageSupplierTypeEnum supplierType = MessageSupplierTypeEnum.valueOf(config.getSupplierType());
        SmsBlend smsBlend = switch (supplierType) {
            case ALIBABA -> {
                AlibabaConfig alibabaConfig = JSONUtil.toBean(config.getConfigJson(), AlibabaConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    alibabaConfig.setSignature(template.getSignature());
                }
                yield AlibabaFactory.instance().createSms(alibabaConfig);
            }
            case HUAWEI -> {
                HuaweiConfig huaweiConfig = JSONUtil.toBean(config.getConfigJson(), HuaweiConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    huaweiConfig.setSignature(template.getSignature());
                }
                yield HuaweiFactory.instance().createSms(huaweiConfig);
            }
            case TENCENT -> {
                TencentConfig tencentConfig = JSONUtil.toBean(config.getConfigJson(), TencentConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    tencentConfig.setSignature(template.getSignature());
                }
                yield TencentFactory.instance().createSms(tencentConfig);
            }
            case YUNPIAN -> {
                YunpianConfig yunpianConfig = JSONUtil.toBean(config.getConfigJson(), YunpianConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    yunpianConfig.setSignature(template.getSignature());
                }
                yield YunPianFactory.instance().createSms(yunpianConfig);
            }
            case UNI_SMS -> {
                UniConfig uniConfig = JSONUtil.toBean(config.getConfigJson(), UniConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    uniConfig.setSignature(template.getSignature());
                }
                yield UniFactory.instance().createSms(uniConfig);
            }
            case JD_CLOUD -> {
                JdCloudConfig jdCloudConfig = JSONUtil.toBean(config.getConfigJson(), JdCloudConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    jdCloudConfig.setSignature(template.getSignature());
                }
                yield JdCloudFactory.instance().createSms(jdCloudConfig);
            }
            case CLOOPEN -> {
                CloopenConfig cloopenConfig = JSONUtil.toBean(config.getConfigJson(), CloopenConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    cloopenConfig.setSignature(template.getSignature());
                }
                yield CloopenFactory.instance().createSms(cloopenConfig);
            }
            case EMAY -> {
                EmayConfig emayConfig = JSONUtil.toBean(config.getConfigJson(), EmayConfig.class, true);
                yield EmayFactory.instance().createSms(emayConfig);
            }
            case CTYUN -> {
                CtyunConfig ctyunConfig = JSONUtil.toBean(config.getConfigJson(), CtyunConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    ctyunConfig.setSignature(template.getSignature());
                }
                yield CtyunFactory.instance().createSms(ctyunConfig);
            }
            case NETEASE -> {
                NeteaseConfig neteaseConfig = JSONUtil.toBean(config.getConfigJson(), NeteaseConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    neteaseConfig.setSignature(template.getSignature());
                }
                yield NeteaseFactory.instance().createSms(neteaseConfig);
            }
            case ZHUTONG -> {
                ZhutongConfig zhutongConfig = JSONUtil.toBean(config.getConfigJson(), ZhutongConfig.class, true);
                if (StrUtil.isNotBlank(template.getSignature())) {
                    zhutongConfig.setSignature(template.getSignature());
                }
                yield ZhutongFactory.instance().createSms(zhutongConfig);
            }
            default -> throw new ServiceException("不支持的消息类型");
        };
        MessageTemplateMode templateMode = MessageTemplateMode.valueOf(template.getTemplateMode());
        // 消息发送方式
        SmsResponse response;
        switch (templateMode) {
            case TEMPLATE_ID -> {
                if (account.size() == 1) {
                    response = smsBlend.sendMessage(account.get(0), template.getTemplateId(), outputVars);
                } else {
                    response = smsBlend.massTexting(account, template.getTemplateId(), outputVars);
                }
            }
            case TEMPLATE_CONTENT -> {
                if (account.size() == 1) {
                    response = smsBlend.sendMessage(account.get(0), content);
                } else {
                    response = smsBlend.massTexting(account, content);
                }
            }
            default -> throw new ServiceException("不支持的消息模板模式：" + templateMode);
        }
        // 记录发送记录
        saveLog(account, template, config, content, log -> {
            log.setIsSuccess(response.isSuccess() ? CommonStatusEnum.SUCCESS.getCodeNum() : CommonStatusEnum.FAIL.getCodeNum());
            log.setResponseBody(StrUtil.maxLength(JsonUtils.toJsonString(response.getData()), 1000));
        });
    }
}
