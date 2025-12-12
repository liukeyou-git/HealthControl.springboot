package com.example.web.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.web.tools.dto.ResponseData;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 全局响应处理切面
 */
@ControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategy.UPPER_CAMEL_CASE);

    @Value("${server.port:7245}")
    private String serverPort;

    @Value("${server.ip:http://localhost:7245}")
    private String serverIp;

    public GlobalResponseAdvice() {
    }

    /**
     * 是否开启支持
     *
     * @param returnType    返回的类型
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 对写入body之前进行拦截拦截处理
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        Object result;
        if (body == null) {
            result = ResponseData.OfSuccess();
        } else if (body instanceof ResponseData<?>) {
            result = body;
        } else {
            result = ResponseData.GetResponseDataInstance(body, "成功", true);
        }

        // 处理响应内容中的URL替换
        if (result instanceof ResponseData<?>) {
            try {
                ResponseData<?> responseData = (ResponseData<?>) result;
                Object data = responseData.getData();
                if (data != null) {
                    // 使用配置过的 ObjectMapper 进行序列化和反序列化
                    String jsonStr = objectMapper.writeValueAsString(result);

                    if (jsonStr.contains("http://localhost:" + serverPort + "/")) {
                        String newJsonStr = jsonStr.replace("http://localhost:" + serverPort + "/", serverIp + "/");
                        return objectMapper.readValue(newJsonStr, ResponseData.class);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}