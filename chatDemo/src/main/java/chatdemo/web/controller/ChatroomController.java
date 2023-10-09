package chatdemo.web.controller;


import javax.servlet.http.HttpSession;

import chatdemo.service.impl.FileUploadServiceImpl;
import chatdemo.service.impl.UserInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import chatdemo.vo.ResponseJson;
import chatdemo.util.Constant;

@Controller
@RequestMapping("/chatroom")
public class ChatroomController {

    @Autowired
    UserInfoServiceImpl userInfoService;

    @Autowired
    private FileUploadServiceImpl fileUploadService;
    
    /**
     * 描述：登录成功后，调用此接口进行页面跳转
     * @return String
     */
    @CrossOrigin
    @GetMapping("toChatroom")
    public String toChatroom() {
        return "chatroom";
    }
    
    /**
     * 描述：登录成功跳转页面后，调用此接口获取用户信息
     * @param session HttpSession
     * @return  ResponseJson
     */
    @CrossOrigin
    @RequestMapping(value = "get_userinfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson getUserInfo(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        return userInfoService.getByUserId((String)userId);
    }
}
