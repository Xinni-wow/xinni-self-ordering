package com.xinni.service.serviceImpl;

import com.xinni.constant.StatusConstant;
import com.xinni.entity.Dish;
import com.xinni.mapper.DishMapper;
import com.xinni.service.DishService;
import com.xinni.service.RecommendationService;
import com.xinni.utils.DeepSeekClient;
import com.xinni.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private DishService dishService;

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Override
    public String recommendDishes(String userQuery) {
        // 1. 获取所有启售的菜品
        List<DishVO> availableDishes = getAllAvailableDishes();

        // 2. 转换为文本描述
        String dishesText = convertDishesToText(availableDishes);

        // 3. 构建提示词
        String prompt = buildPrompt(userQuery, dishesText);

        String result = deepSeekClient.getRecommendation(prompt);
        System.out.println("推荐结果："+ result);
        // 4. 调用DeepSeek API获取推荐结果
        return deepSeekClient.getRecommendation(prompt);
    }

    @Override
    public List<DishVO> getAllAvailableDishes() {
        Dish dish = new Dish();
        dish.setStatus(StatusConstant.ENABLE); // 只查询启售状态的菜品
        return dishService.getDishesWithFlavorById(dish);
    }

    @Override
    public String convertDishesToText(List<DishVO> dishes) {
        StringBuilder sb = new StringBuilder();
        sb.append("当前可提供的菜品如下：\n");

        for (DishVO dish : dishes) {
            sb.append("菜品名称：").append(dish.getName()).append("\n");
//            sb.append("分类：").append(getCategoryName(dish.getCategoryId())).append("\n");
            sb.append("价格：").append(dish.getPrice()).append("元\n");
            sb.append("描述：").append(dish.getDetail()).append("\n");

            // 添加口味信息
            if (dish.getFlavors() != null && !dish.getFlavors().isEmpty()) {
                sb.append("可选口味：");
                dish.getFlavors().forEach(flavor -> {
                    sb.append(flavor.getName()).append("(")
                            .append(flavor.getList()).append(")，");
                });
                sb.deleteCharAt(sb.length() - 1); // 移除最后一个逗号
                sb.append("\n");
            }

            sb.append("---\n");
        }

        return sb.toString();
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(String userQuery, String dishesText) {
        return "作为一名餐厅推荐员，请根据用户的需求和提供的菜品信息，推荐最合适的菜品。\n"
                + "用户需求：" + userQuery + "\n"
                + dishesText + "\n"
                + "请根据用户需求，从在售餐品中选择最合适的1-3个进行推荐，"
                + "并简要说明推荐理由。推荐格式清晰，易于阅读。如果用户需求与用餐无关，请回复‘很遗憾，我主要为您解决用餐方面的问题，您可以问我关于菜品推荐、口味选择等相关内容’。";
    }

    /**
     * 获取分类名称（需要根据实际代码实现，这里仅为示例）
     */
    private String getCategoryName(Integer categoryId) {
        // 实际实现中需要查询分类表获取名称
        // 这里仅为示例
        return "菜品分类" + categoryId;
    }
}
