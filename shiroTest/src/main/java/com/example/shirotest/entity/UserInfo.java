package com.example.shirotest.entity;

import lombok.Data;
@Data
public class UserInfo {
    private Long id; // 主键.

    private String username; // 登录账户,唯一.

    private String name; // 名称(匿名或真实姓名),用于UI显示

    private String password; // 密码.

    private String salt; // 加密密码的盐

//    @JsonIgnoreProperties(value = {"userInfos"})
//    @ManyToMany(fetch = FetchType.EAGER) // 立即从数据库中进行加载数据
//    @JoinTable(name = "SysUserRole", joinColumns = @JoinColumn(name = "uid"), inverseJoinColumns = @JoinColumn(name = "roleId"))
//    private List<SysRole> roles; // 一个用户具有多个角色

    private String roles;
}