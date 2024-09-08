package com.piyush.a02_buzzlink_chat_application;

public class CommunityUser {
    private String userId;
    private String userName;
    private String status;
    private String profilepic;

    public CommunityUser() {
        // Default constructor required for calls to DataSnapshot.getValue(CommunityUser.class)
    }

    public CommunityUser(String userId, String userName, String status, String profilepic) {
        this.userId = userId;
        this.userName = userName;
        this.status = status;
        this.profilepic = profilepic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }
}
