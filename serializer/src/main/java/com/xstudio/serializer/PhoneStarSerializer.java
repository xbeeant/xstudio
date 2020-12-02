package com.xstudio.serializer;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 手机号加星序列化，对手机号中间4位进行加星操作<br>
 * 例如： 12345678901 加星后 123****8901<br>
 *
 * @author xiaobiao
 * @version 2020/2/2
 */
public class PhoneStarSerializer implements JsonSerializer<String> {
    @Override
    public JsonElement serialize(String value, Type typeOfSrc, JsonSerializationContext context) {
        String text = value.replaceAll("(\\d{3})\\d+(\\d{4})", "$1****$2");
        return new JsonPrimitive(text);
    }
}
