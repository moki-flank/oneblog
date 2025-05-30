package com.mojian.controller;

import com.mojian.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: quequnlong
 * @date: 2024/12/29
 * @description:
 */
@Slf4j
@Api(tags = "微信接口相关控制器")
@RestController()
@RequestMapping("/wechat")
@RequiredArgsConstructor
public class WeChatController {

    private final WxMpService wxMpService;

    private final AuthService authService;

    private final Pattern pattern = Pattern.compile("(?i)^DL\\d{4}$");

    @ApiOperation(value = "微信公众号服务器配置校验token")
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String checkSignature(@RequestParam(name = "signature") String signature,
                                 @RequestParam(name = "timestamp") String timestamp,
                                 @RequestParam(name = "nonce") String nonce,
                                 @RequestParam(name = "echostr") String echostr) {
        log.info("公众号请求参数:signature:{},timestamp:{},nonce:{},echostr:{}", signature, timestamp, nonce, echostr);
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "Invalid signature";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String handleMsg(HttpServletRequest request) {

        try {
            WxMpXmlMessage message = WxMpXmlMessage.fromXml(request.getInputStream());
            String content = message.getContent();
            log.info("公众号请求类型:{};内容为:{}", message.getMsgType(), content);
            if (WxConsts.XmlMsgType.TEXT.equals(message.getMsgType())){
//                if ("验证码".equals(content)) {
//                    String code = RandomUtil.generationNumberChar(6);
//                    String msg = MessageFormat.format("您的本次验证码:{0},该验证码30分钟内有效。", code);
//                    redisService.setCacheObject(RedisConstants.WECHAT_CODE+code,code,30, TimeUnit.MINUTES);
//                    return returnMsg(msg, message);
//                }
                //登录逻辑
                if (content.contains("DL")) {
                    Matcher matcher = pattern.matcher(content);
                    if (!matcher.matches()) {
                        return returnMsg("验证不正确或已过期", message);
                    }else {
                        String msg = authService.wechatLogin(message);
                        return returnMsg(msg, message);
                    }
                }


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    /**
     * 返回消息
     * @param msg 消息内容
     * @param message
     * @return
     */
    private static String returnMsg(String msg, WxMpXmlMessage message) {
        WxMpXmlOutTextMessage outMessage = WxMpXmlOutTextMessage.TEXT().content(msg).fromUser(message.getToUser()).toUser(message.getFromUser()).build();
        return outMessage.toXml();
    }
}

