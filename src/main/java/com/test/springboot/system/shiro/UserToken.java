package com.test.springboot.system.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 自定义登录身份
 */
public class UserToken extends UsernamePasswordToken {
    //登录方式
    private LoginType loginType;
    //微信code
    private String code;

    // TODO 由于是demo方法，此处微信只传一个code参数，其他参数根据实际情况添加


    public UserToken(LoginType loginType, final String username, final String password) {
        super(username, password);
        this.loginType = loginType;
    }

    public UserToken(LoginType loginType, String username, String password, String code) {
        super(username, password);
        this.loginType = loginType;
        this.code = code;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
