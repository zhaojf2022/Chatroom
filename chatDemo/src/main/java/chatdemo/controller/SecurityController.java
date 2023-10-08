package chatdemo.controller;

import javax.servlet.http.HttpSession;

import chatdemo.service.impl.SecurityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import chatdemo.vo.ResponseJson;
import chatdemo.service.SecurityService;

@Controller
public class SecurityController {

    private final Logger logger = LoggerFactory.getLogger(SecurityController.class);
    @Autowired
    SecurityServiceImpl securityService;

    @CrossOrigin
    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String toLogin() {
        logger.info("跳转到登录页面");
        return "login";
    }

    @CrossOrigin
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson login(HttpSession session,
            @RequestParam String username,
            @RequestParam String password) {
        logger.info("收到登录请求：username= " + username + ". password= " + password);
        return securityService.login(username, password, session);
    }

    @CrossOrigin
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson logout(HttpSession session) {

        logger.info("用户登出：username= " + session.getAttribute("username") );
        return securityService.logout(session);
    }
}
