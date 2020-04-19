package com.xstudio.spring.api.gateway.strategy;

import com.xstudio.spring.redis.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 会话限制策略
 *
 * @author xiaobiao
 * @version 2020/2/12
 */
public class SessionStrategy extends AbstractStrategy {

    @Override
    public boolean check(String key, HttpServletRequest request, int times, TimeUnit unit, RedisTemplate<Object, Object> redisTemplate) {
        String sessionId = request.getSession().getId();
        Long incr = RedisUtil.incr(key + sessionId, 1, unit, redisTemplate);
        return incr < times;
    }
}
