package com.xinni.service;

import com.xinni.dto.CategoryDTO;
import com.xinni.dto.CategoryTypePageDTO;
import com.xinni.entity.Category;
import com.xinni.result.PageResult;

import java.util.List;

public interface CategoryService {
    void addCategory(CategoryDTO categoryDTO);

    PageResult getPageList(CategoryTypePageDTO categoryTypePageDTO);

    List<Category> getList(Integer type);

    Category getById(Integer id);
    void onOff(Integer id);

    void udpate(CategoryDTO categoryDTO);

    void delete(Integer id);

}
