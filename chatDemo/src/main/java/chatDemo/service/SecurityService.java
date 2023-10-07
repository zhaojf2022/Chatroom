package chatDemo.service;

import javax.servlet.http.HttpSession;

import chatDemo.vo.ResponseJson;

public interface SecurityService {

    ResponseJson login(String username, String password, HttpSession session);
    
    ResponseJson logout(HttpSession session);
}
