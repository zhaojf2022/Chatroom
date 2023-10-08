package chatdemo.service;

import chatdemo.vo.ResponseJson;

public interface UserInfoService {

    ResponseJson getByUserId(String userId);
}
