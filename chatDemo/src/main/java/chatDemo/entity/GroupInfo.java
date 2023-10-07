package chatDemo.entity;

import lombok.Data;

import java.util.List;

@Data
public class GroupInfo {

    private String groupId;
    private String groupName;
    private String groupAvatarUrl;
    private List<UserInfo> members;
    
    public GroupInfo(String groupId, String groupName, String groupAvatarUrl, List<UserInfo> members) {
        super();
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupAvatarUrl = groupAvatarUrl;
        this.members = members;
    }

}
