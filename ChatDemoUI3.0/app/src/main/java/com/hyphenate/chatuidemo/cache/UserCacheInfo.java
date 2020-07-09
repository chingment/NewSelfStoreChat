package com.hyphenate.chatuidemo.cache;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 用户缓存信息类
 * 需要ormlite库支持
 * Created by Martin on 2017/4/24.
 */
@DatabaseTable(tableName="UserCacheInfo")
public class UserCacheInfo{

    @DatabaseField(generatedId=true)
    private int id;

    /*环信ID*/
    @DatabaseField(index = true)
    private String userName;

    /*昵称*/
    @DatabaseField
    private String nickName;

    /*头像*/
    @DatabaseField
    private String avatar;

    /*数据过期时间*/
    @DatabaseField(canBeNull = false)
    private long expiredDate;

    // 必须顶一个无参数的构造函数，否则会报【virtual method】异常
    UserCacheInfo(){}

    /*将json字符串转换成model*/
    public static UserCacheInfo parse(String jsonStr) {
        return (new Gson()).fromJson(jsonStr, UserCacheInfo.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(long expiredDate) {
        this.expiredDate = expiredDate;
    }

}
