package chatdemo.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import chatdemo.vo.ResponseJson;

import java.io.IOException;

public interface FileUploadService {

    ResponseJson upload(MultipartFile file, HttpServletRequest request);
    ResponseJson uploadFile(HttpServletRequest request) throws IOException;

}
