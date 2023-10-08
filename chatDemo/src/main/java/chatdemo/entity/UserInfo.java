package chatdemo.entity;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {

    private String userId;
    private String username;
    private String password;
    private String avatarUrl;
    private List<UserInfo> friendList;
    private List<GroupInfo> groupList;
    
    public UserInfo() {
        super();
    }
    
    public UserInfo(String userId, String username, String password, String avatarUrl) {
        super();
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }

}
