package com.xinni.service;

import com.xinni.vo.BusinessDataVO;
import com.xinni.vo.DishOverViewVO;
import com.xinni.vo.OrderOverViewVO;
import com.xinni.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    OrderOverViewVO getOrderOverView();

    DishOverViewVO getDishOverView();

    SetmealOverViewVO getSetmealOverView();
}
