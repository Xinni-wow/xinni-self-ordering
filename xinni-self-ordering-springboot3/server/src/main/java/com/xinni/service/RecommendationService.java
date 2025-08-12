package com.xinni.service;

import com.xinni.entity.Dish;
import com.xinni.vo.DishVO;
import java.util.List;

public interface RecommendationService {
    /**
     * 根据用户需求推荐菜品
     * @param userQuery 用户的用餐需求
     * @return 推荐结果
     */
    String recommendDishes(String userQuery);

    /**
     * 获取所有启售的菜品
     * @return 启售菜品列表
     */
    List<DishVO> getAllAvailableDishes();

    /**
     * 将菜品列表转换为文本描述
     * @param dishes 菜品列表
     * @return 文本描述
     */
    String convertDishesToText(List<DishVO> dishes);
}