package chatDemo.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import chatDemo.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import chatDemo.vo.ResponseJson;
import chatDemo.service.UserInfoService;
import chatDemo.util.Constant;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/chatroom")
public class ChatroomController {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    private FileUploadService fileUploadService;
    
    /**
     * 描述：登录成功后，调用此接口进行页面跳转
     * @return String
     */
    @GetMapping
    public String toChatroom() {
        return "chatroom";
    }
    
    /**
     * 描述：登录成功跳转页面后，调用此接口获取用户信息
     * @param session HttpSession
     * @return  ResponseJson
     */
    @RequestMapping(value = "get_userinfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson getUserInfo(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        return userInfoService.getByUserId((String)userId);
    }

    @RequestMapping(value = "upload", method = POST)
    @ResponseBody
    public ResponseJson upload(
            @RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request) {
        return fileUploadService.upload(file, request);
    }
}
