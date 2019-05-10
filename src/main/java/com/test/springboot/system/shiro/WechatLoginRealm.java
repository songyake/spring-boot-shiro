package com.test.springboot.system.shiro;

import com.test.springboot.model.User;
import com.test.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 微信登录realm
 */
@Slf4j
public class WechatLoginRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public String getName() {
        return LoginType.WECHAT_LOGIN.getType();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof UserToken) {
            return ((UserToken) token).getLoginType() == LoginType.WECHAT_LOGIN;
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
        log.info("---------------- 微信登录 ----------------------");
        UserToken token = (UserToken) authcToken;
        String code = token.getCode();

        String openid = getOpenid(code);

        if(StringUtils.isEmpty(openid)){
            log.debug("微信授权登录失败，未获得openid");
            throw new AuthenticationException();
        }
        User user = userService.getByOpenid(openid);
        if(user == null){
            // TODO 获取微信昵称、头像等信息，并完成注册用户，此处省略
        }
        // 用户为禁用状态
        if(user.getLoginFlag().equals("0")){
            throw new DisabledAccountException();
        }
        // 完成登录，此处已经不需要做密码校验
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user, //用户
                code, //密码
                getName()  //realm name
        );
        return authenticationInfo;
    }

    private String getOpenid(String code){
        // 这里假装是一个通过code获取openid的方法，具体实现由各位自己去实现，此处不做扩展
        if(StringUtils.isNotEmpty(code)){
            return "sdfuh81238917jhoijiosdsgsdfljiofds";
        }
        return null;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }
}
