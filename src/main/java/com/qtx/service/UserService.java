package com.qtx.service;

import com.qtx.po.User;

public interface UserService {
    User checkUser(String username,String password);
}
