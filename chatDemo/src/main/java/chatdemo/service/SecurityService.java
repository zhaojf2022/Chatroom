package chatdemo.service;

import jakarta.servlet.http.HttpSession;

import chatdemo.vo.ResponseJson;

public interface SecurityService {

    ResponseJson login(String username, String password, HttpSession session);
    
    ResponseJson logout(HttpSession session);
}
