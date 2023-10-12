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

@CrossOrigin
@Controller
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
    @GetMapping("/chatroom")
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
    @PostMapping("/chatroom/getUserInfo")
    @ResponseBody
    public ResponseJson getUserInfo(HttpSession session) {

        // 获取请求信息中 ‘userId" 键的值
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        // 根据ID查询到用户信息后放入响应对象中返回
        return userInfoService.getUserInofById((String)userId);
    }
}
