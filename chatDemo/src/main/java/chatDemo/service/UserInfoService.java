package chatDemo.service;

import chatDemo.vo.ResponseJson;

public interface UserInfoService {

    ResponseJson getByUserId(String userId);
}
