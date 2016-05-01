package com.hybrid.customlogin;

public class Card_Object {
    private String link;
    private String title;
    private String description;
    private int up;
    private int down;
    private int id;
    private int comment_count;
    private  String user_name ;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public Card_Object(String link,String title,String description,int up,int down,int id,int comment){
        this.link=link;
        this.title=title;
        this.description=description;
        this.up=up;
        this.down=down;

        this.id=id;
        this.comment_count=comment;
    }
    public Card_Object(String link,String title,String description,int up,int down,int id,int comment,String user_name){
        this.link=link;
        this.title=title;
        this.description=description;
        this.up=up;
        this.down=down;
        this.user_name=user_name;
        this.id=id;
        this.comment_count=comment;
    }
    public Card_Object(Card_Object c){
        this.link=c.getLink();
        this.title=c.getTitle();
        this.description=c.getDescription();
        this.up=c.getUp();
        this.down=c.getDown();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }
}

