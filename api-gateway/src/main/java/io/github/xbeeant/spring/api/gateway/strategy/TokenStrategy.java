package io.github.xbeeant.spring.api.gateway.strategy;

import io.github.xbeeant.http.Requests;
import io.github.xbeeant.spring.redis.RedisHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * token限制策略，当无法获取token时，会进行IP限制
 * token获取：从请求参数中获取token参数 或  从 header中获取 token 参数
 *
 * @author xiaobiao
 * @version 2020/2/12
 */
public class TokenStrategy extends AbstractStrategy {
    @Override
    public boolean check(String key, HttpServletRequest request, int times, TimeUnit unit, RedisTemplate<Object, Object> redisTemplate) {

        String token = request.getParameter("token");
        if (StringUtils.isEmpty(token)) {
            token = request.getHeader("token");
        }
        if (StringUtils.isEmpty(token)) {
            token = Requests.getIp(request);
        }

        Long incr = RedisHelper.incr(key + token, 1, unit, redisTemplate);
        return incr < times;
    }
}
