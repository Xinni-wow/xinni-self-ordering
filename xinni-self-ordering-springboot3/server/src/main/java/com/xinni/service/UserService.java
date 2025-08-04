package com.xinni.service;

import com.xinni.dto.UserDTO;
import com.xinni.dto.UserLoginDTO;
import com.xinni.entity.User;

public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);

    User getUser(Integer id);

    void update(UserDTO userDTO);
}
