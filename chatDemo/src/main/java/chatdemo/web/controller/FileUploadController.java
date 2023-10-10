package chatdemo.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import chatdemo.service.FileUploadService;
import chatdemo.vo.ResponseJson;


@Controller
@RequestMapping("/chatroom")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;


    /**
     * 文件上传传
     * @param file MultipartFile
     * @param request HttpServletRequest
     * @return ResponseJson
     */
    @CrossOrigin
    @RequestMapping(value = "/upload", method = POST)
    @ResponseBody
    public ResponseJson upload(
            @RequestParam(value = "file", required = true) MultipartFile file,
            HttpServletRequest request) {
        return fileUploadService.upload(file, request);
    }
}