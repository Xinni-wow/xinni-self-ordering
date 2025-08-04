package com.xinni.service;

import com.xinni.dto.DishDTO;
import com.xinni.dto.DishPageDTO;
import com.xinni.entity.Dish;
import com.xinni.result.PageResult;
import com.xinni.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDishWithFlavor(DishDTO dishDTO);

    PageResult getPageList(DishPageDTO dishPageDTO);

    DishVO getDishWithFlavorById(Integer id);

    void updateDishWithFlavor(DishDTO dishDTO);

    void deleteBatch(List<Integer> ids);

    void onOff(Integer id);

    List<DishVO> getDishesWithFlavorById(Dish dish);
}
