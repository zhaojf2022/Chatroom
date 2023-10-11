package chatdemo.web.controller;


import jakarta.servlet.http.HttpSession;

import chatdemo.service.impl.FileUploadServiceImpl;
import chatdemo.service.impl.UserInfoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import chatdemo.vo.ResponseJson;
import chatdemo.util.Constant;
import org.springframework.web.bind.annotation.RequestMethod;
@CrossOrigin
@Controller
@RequestMapping("/chatroom")
public class ChatroomController {

    @Autowired
    UserInfoServiceImpl userInfoService;

    @Autowired
    private FileUploadServiceImpl fileUploadService;

    private final Logger logger = LoggerFactory.getLogger(ChatroomController.class);

   /**
     * 描述：登录成功后，调用此接口进行页面跳转
     * @return String
     */
    @CrossOrigin
    @GetMapping("tochatroom")
    public String toChatroom() {
        logger.info("跳转到聊天室页面");
        return "chatroom";
    }
    
    /**
     * 描述：登录成功跳转页面后，调用此接口获取用户信息
     * @param session HttpSession
     * @return  ResponseJson
     */
    @CrossOrigin
    @PostMapping("getUserInfo")
    @ResponseBody
    public ResponseJson getUserInfo(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        return userInfoService.getByUserId((String)userId);
    }
}
