package com.xinni.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DeepSeekClient {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.model:DeepSeek-V3}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getRecommendation(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加system角色消息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            // 系统角色中定义固定规则
            systemMessage.put("content", "你是一名餐厅推荐员，负责根据用户需求和提供的菜品信息推荐最合适的菜品。" +
                    "推荐规则：1. 从在售餐品中选择1-3个最合适的菜品；2. 需简要说明推荐理由；3. 格式清晰易读；" +
                    "4. 若用户需求与用餐推荐无关，请回复‘很遗憾，我主要为您解决用餐方面的问题，您可以问我关于菜品推荐、口味选择等相关内容’。");
            messages.add(systemMessage);
            
            // 添加user角色消息
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.6);
            requestBody.put("stream", false);

            // 设置请求头
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + apiKey);

            org.springframework.http.HttpEntity<Map<String, Object>> request =
                    new org.springframework.http.HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(apiUrl, request, String.class);

            // 解析响应
            JSONObject jsonObject = JSON.parseObject(response);
            JSONArray choices = jsonObject.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                return choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                log.error("DeepSeek API返回空结果: {}", response);
                return "推荐服务暂时不可用，请稍后再试";
            }
        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
            return "推荐服务暂时不可用，请稍后再试";
        }
    }
}
