package chatdemo.web.controller;

import jakarta.servlet.http.HttpSession;

import chatdemo.service.impl.SecurityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import chatdemo.vo.ResponseJson;

@Controller
public class SecurityController {

    private final Logger logger = LoggerFactory.getLogger(SecurityController.class);
    @Autowired
    SecurityServiceImpl securityService;

    @CrossOrigin
    @GetMapping("/tologin")
    public String toLogin() {
        logger.info("跳转到登录页面");
        return "login";
    }

    @CrossOrigin
    @PostMapping("/login")
    @ResponseBody
    public ResponseJson login(HttpSession session,
            @RequestParam String username,
            @RequestParam String password) {
        logger.info("收到登录请求：username= " + username + ". password= " + password);
        return securityService.login(username, password, session);
    }

    @CrossOrigin
    @PostMapping("/logout")
    @ResponseBody
    public ResponseJson logout(HttpSession session) {

        logger.info("用户登出：username= " + session.getAttribute("username") );
        return securityService.logout(session);
    }
}
