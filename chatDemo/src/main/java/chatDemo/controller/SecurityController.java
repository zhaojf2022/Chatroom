package chatDemo.controller;

import javax.servlet.http.HttpSession;

import chatDemo.common.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import chatDemo.vo.ResponseJson;
import chatDemo.service.SecurityService;

@Controller
@RequestMapping("/")
public class SecurityController {

    private final Logger logger = LoggerFactory.getLogger(SecurityController.class);
    @Autowired
    SecurityService securityService;
    
    @RequestMapping(value = {"login", "/"}, method = RequestMethod.GET)
    public String toLogin() {
        return "login";
    }
    
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson login(HttpSession session,
            @RequestParam String username,
            @RequestParam String password) {
        logger.info("收到登录请求：username= " + username + ". password= " + password);
        return securityService.login(username, password, session);
    }
    
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson logout(HttpSession session) {

        logger.info("用户登出：username= " + session.getAttribute("username") );
        return securityService.logout(session);
    }
}
