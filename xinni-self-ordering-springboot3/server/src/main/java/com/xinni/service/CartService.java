package com.xinni.service;

import com.xinni.dto.CartDTO;
import com.xinni.entity.Cart;

import java.util.List;

public interface CartService {
    void add(CartDTO cartDTO);

    List<Cart> getList();

    void clean();

    void sub(CartDTO cartDTO);
}
