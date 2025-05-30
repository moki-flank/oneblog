package com.mojian.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.mojian.common.Result;
import com.mojian.dto.EmailRegisterDto;
import com.mojian.dto.LoginDTO;
import com.mojian.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthCallback;
import org.springframework.web.bind.annotation.*;
import com.mojian.dto.user.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Api(tags = "认证管理")
public class AuthController {

    private final AuthService authService;

    @RequestMapping("/api/auth/render/{source}")
    @ApiOperation(value = "获取第三方授权地址")
    public Result<String> renderAuth(HttpServletResponse response, @PathVariable String source) {
        return Result.success(authService.renderAuth(source));
    }

    @RequestMapping("/api/auth/callback/{source}")
    public void login(AuthCallback callback, @PathVariable String source, HttpServletResponse httpServletResponse) throws IOException {
        authService.authLogin(callback,source,httpServletResponse);
    }


    @ApiOperation(value = "用户登录")
    @PostMapping("/auth/login")
    public Result<LoginUserInfo> login(@RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @ApiOperation(value = "用户登出")
    @PostMapping("/auth/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success(null);
    }

    @ApiOperation(value = "发送注册邮箱验证码")
    @GetMapping("/api/sendEmailCode")
    public Result<Boolean> sendEmailCode(String email) throws MessagingException {
        return Result.success(authService.sendEmailCode(email));
    }

    @ApiOperation(value = "邮箱账号注册")
    @PostMapping("/api/email/register")
    public Result<Boolean> register(@RequestBody EmailRegisterDto dto){
        return Result.success(authService.register(dto));
    }

    @ApiOperation(value = "根据邮箱修改密码")
    @PostMapping("/api/email/forgot")
    public Result<Boolean> forgot(@RequestBody EmailRegisterDto dto){
        return Result.success(authService.forgot(dto));
    }

    @ApiOperation(value = "获取微信扫码登录验证码")
    @GetMapping("/api/wechat/getCode")
    public Result<String> getWechatLoginCode(){
        return Result.success(authService.getWechatLoginCode());
    }

    @ApiOperation(value = "获取微信扫码登录验证码")
    @GetMapping("/api/wechat/isLogin/{loginCode}")
    public Result<LoginUserInfo> getWechatIsLogin(@PathVariable String loginCode){
        return Result.success(authService.getWechatIsLogin(loginCode));
    }

    @ApiOperation(value = "微信小程序登录")
    @GetMapping("/api/wechat/appletLogin/{code}")
    public Result<LoginUserInfo> appletLogin(@PathVariable String code){
        return Result.success(authService.appletLogin(code));
    }

    @GetMapping("/auth/info")
    public Result<LoginUserInfo> getUserInfo() {
        return Result.success(authService.getLoginUserInfo());
    }

}
