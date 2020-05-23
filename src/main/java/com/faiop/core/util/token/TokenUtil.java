package com.faiop.core.util.token;

import com.auth0.jwt.JWT;
import com.faiop.core.util.UserContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author RM
 */
public class TokenUtil {
    public static String getTokenUserId() {
        // 从 http 请求头中取出 token
        Cookie[] cookies = UserContext.getRequest().getCookies();
        String token = null;
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }
        String userId = JWT.decode(token).getAudience().get(0);
        return userId;
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }
}
