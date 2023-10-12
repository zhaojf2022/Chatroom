package chatdemo.dao;

import chatdemo.entity.UserInfo;

public interface UserInfoDao {

    void loadUserInfo();
    
    UserInfo getUserInfoByname(String username);
    
    UserInfo getUserInfoById(String userId);
}
