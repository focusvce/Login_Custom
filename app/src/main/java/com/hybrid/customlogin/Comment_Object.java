package com.hybrid.customlogin;


public class Comment_Object {
    private String user_name;
    private String comment;
    private int uid;
    private int pid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Comment_Object(String user_name,String comment,int uid,int pid){
        this.user_name=user_name;
        this.comment=comment;
        this.uid=uid;
        this.pid=pid;
    }
    public Comment_Object(String comment,String user_name){
        this.comment = comment;
        this.user_name= user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
