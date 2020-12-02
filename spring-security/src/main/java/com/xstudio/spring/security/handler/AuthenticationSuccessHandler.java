package com.xstudio.spring.security.handler;

import com.xstudio.core.ApiResponse;
import com.xstudio.http.RequestUtil;
import com.xstudio.spring.security.UserDetails;
import org.apache.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证成功
 *
 * @author xiaobiao
 * @version 1.0.0
 * @date 2020/12/01
 */
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException {
        // 输出登录提示信息
        response.setStatus(HttpStatus.SC_OK);
        ApiResponse<Object> msg = new ApiResponse<>();
        msg.setData(setData(authentication));
        // 返回json
        RequestUtil.writeJson(response, msg);
    }

    public Object setData(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getDetails();
        }

        return null;
    }
}