package chatDemo.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import chatDemo.vo.ResponseJson;

public interface FileUploadService {

    ResponseJson upload(MultipartFile file, HttpServletRequest request);
}
