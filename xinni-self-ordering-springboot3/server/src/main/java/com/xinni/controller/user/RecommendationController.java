package com.xinni.controller.user;

import com.xinni.result.Result;
import com.xinni.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("userRecommendationController")
@RequestMapping("/user/recommendation")
@Slf4j
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 根据用户需求推荐菜品
     */
    @GetMapping("/dish")
    public Result<String> recommendDishes(@RequestParam String query) {
        log.info("用户用餐需求：{}", query);
        String recommendation = recommendationService.recommendDishes(query);
        return Result.success(recommendation);
    }
}