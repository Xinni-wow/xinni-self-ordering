package com.xinni;

import com.xinni.utils.DeepSeekClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootTest
public class DeepSeekTest {

    @Autowired
    private DeepSeekClient deepSeekClient;
    
    // Mock掉WebSocket相关的Bean，避免在测试环境中初始化
    @MockBean
    private ServerEndpointExporter serverEndpointExporter;

    @Test
    public void testDeepSeekAPI() {
        System.out.println("开始测试DeepSeek API调用...");
        
        // 构造一个简单的测试提示词
        String testPrompt = "请推荐一些适合夏天吃的清淡菜品，并简要说明理由。";
        
        try {
            long startTime = System.currentTimeMillis();
            String result = deepSeekClient.getRecommendation(testPrompt);
            long endTime = System.currentTimeMillis();
            
            System.out.println("API调用成功！");
            System.out.println("耗时: " + (endTime - startTime) + "ms");
            System.out.println("推荐结果:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("API调用失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

