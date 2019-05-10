package com.test.springboot.system.shiro;

import com.test.springboot.model.User;
import com.test.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 手机验证码登录realm
 */
@Slf4j
public class UserPhoneRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public String getName() {
        return LoginType.USER_PHONE.getType();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof UserToken) {
            return ((UserToken) token).getLoginType() == LoginType.USER_PHONE;
        } else {
            return false;
        }
    }


    @Override
    public void setAuthorizationCacheName(String authorizationCacheName) {
        super.setAuthorizationCacheName(authorizationCacheName);
    }

    @Override
    protected void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 认证信息.(身份验证) : Authentication 是用来验证用户身份
     *
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        log.info("---------------- 手机验证码登录 ----------------------");
        UserToken token = (UserToken) authcToken;
        String phone = token.getUsername();
        // 手机验证码
        String validCode = String.valueOf(token.getPassword());

        // 这里假装从redis中获取了验证码为 123456，并对比密码是否正确
        if(!"123456".equals(validCode)){
            log.debug("验证码错误，手机号为：{}", phone);
            throw new IncorrectCredentialsException();
        }

        User user = userService.getByPhone(phone);
        if(user == null){
            throw new UnknownAccountException();
        }
        // 用户为禁用状态
        if(user.getLoginFlag().equals("0")){
            throw new DisabledAccountException();
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user, //用户
                validCode, //密码
                getName()  //realm name
        );
        return authenticationInfo;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

}
