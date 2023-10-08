package chatdemo.service;

import javax.servlet.http.HttpSession;

import chatdemo.vo.ResponseJson;

public interface SecurityService {

    ResponseJson login(String username, String password, HttpSession session);
    
    ResponseJson logout(HttpSession session);
}
