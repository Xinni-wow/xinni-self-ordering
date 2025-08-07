package com.xinni.vo;

import com.xinni.entity.DishFlavor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetmealDishVO implements Serializable {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer copies;
    private Integer setmealId;
    private Integer dishId;
    private String pic;
    private String detail;
    private List<DishFlavor> flavors = new ArrayList<>();
}