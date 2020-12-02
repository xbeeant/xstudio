package com.xstudio.serializer;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身份证序列化
 * 保留前六位和后四位，中间值使用*替代
 * 例如： 123456789012345678 序列化后变成 12************5678
 *
 * @author xiaobiao
 * @version 2020/2/2
 */
public class IdCardValueSerializer implements JsonSerializer<String> {
    /**
     * 大部分敏感词汇在10个以内，直接返回缓存的字符串
     */
    private static final Map<Integer, String> STAR_ARR = new HashMap<Integer, String>() {
        {
            put(12, "************");
            put(10, "************");
        }
    };

    /**
     * 匹配表达式
     */
    private static final String REGEX = "(\\d{2})(\\d+)(\\d{4})";

    /**
     * pattern
     */
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public JsonElement serialize(String value, Type typeOfSrc, JsonSerializationContext context) {
        Matcher matcher = PATTERN.matcher(value);
        if (matcher.find()) {
            StringBuilder sb = new StringBuilder();
            sb.append(matcher.group(1));
            int length = matcher.group(2).length();
            sb.append(STAR_ARR.get(length));
            sb.append(matcher.group(3));
            return new JsonPrimitive(sb.toString());
        }
        return new JsonPrimitive(value);
    }
}
