package com.xinni.service;

import com.xinni.dto.SetmealDTO;
import com.xinni.dto.SetmealPageDTO;
import com.xinni.entity.Setmeal;
import com.xinni.result.PageResult;
import com.xinni.vo.DishItemVO;
import com.xinni.vo.SetmealVO;
import com.xinni.vo.SetmealWithPicVO;

import java.util.List;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult getPageList(SetmealPageDTO setmealPageDTO);

    SetmealVO getSetmealById(Integer id);

    void onOff(Integer id);

    void update(SetmealDTO setmealDTO);

    void deleteBatch(List<Integer> ids);

    List<Setmeal> getList(Integer categoryId);

    List<DishItemVO> getSetmealDishesById(Integer id);

    SetmealWithPicVO getSetmealWithPic(Integer id);
}
