package chatdemo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chatdemo.dao.UserInfoDao;
import chatdemo.entity.UserInfo;
import chatdemo.vo.ResponseJson;
import chatdemo.service.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;
    
    @Override
    public ResponseJson getUserInofById(String userId) {
        UserInfo userInfo = userInfoDao.getUserInfoById(userId);
        return new ResponseJson().success()
                .setData("userInfo", userInfo);
    }

}
