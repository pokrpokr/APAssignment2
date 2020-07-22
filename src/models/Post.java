package models;

import Exceptions.DBSaveException;
import Exceptions.ExistException;
import Exceptions.ValidationException;
import database.DB;
import javafx.geometry.Pos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Post {
    private long id;
    private long creatorId;
    private String title;
    private String description;
    private String idStr;
    private String creatorName;
    private String status;
    private String imageUrl;
    private boolean isDeleted = false;

    public enum Type {Event, Sale, Job};

    public Post(long user_id, String creatorName, String idStr, String title, String description, String imageUrl) {
        this.creatorId   = user_id;
        this.creatorName = creatorName;
        this.idStr       = idStr;
        this.title       = title;
        this.description = description;
        this.imageUrl    = imageUrl;
        this.status      = "OPEN";
    }
    
    //For data from database
    public Post(long id, long user_id, boolean isDeleted, String[] args) {
    	this.id          = id;
        this.creatorId   = user_id;
        this.creatorName = args[0];
        this.idStr       = args[1];
        this.title       = args[2];
        this.description = args[3];
        this.imageUrl    = args[4];
        this.status      = args[5];
        this.isDeleted   = isDeleted;
    }

    abstract public boolean handleReply(double offer) throws SQLException, ValidationException, ExistException, DBSaveException;

    static public ArrayList<Post> getPosts(HashMap<String,String> options){
        String optionString = "";
        for (String key :options.keySet()) {
            if (key.equals("creatorId")){
                optionString = " "+ optionString + key + "= " + Integer.valueOf(options.get(key)) + " and ";
            } else {
                optionString = " "+ optionString + key + "= '" + options.get(key) + "' and ";
            }
        }
        ArrayList<Post> posts = new ArrayList<>();
        DB db = new DB();
        String sql = "SELECT * FROM posts WHERE"+ optionString +" isDeleted = 0";
        try{
            ResultSet results = db.search(sql);
            while (results.next()){
                String type = results.getString("type");
                if (type.equals(Type.Event.toString())){
                    posts.add(Event.constructEvent(results));
                } else if (type.equals(Type.Sale.toString())) {
                    posts.add(Sale.constructSale(results));
                } else if (type.equals(Type.Job.toString())) {
                    posts.add(Job.constructJob(results));
                }
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return posts;
    }

    public ArrayList<String> getReplies() throws SQLException {
        ArrayList<String> replies = new ArrayList<>();
        DB db = new DB();
        String sql = "SELECT * FROM replies WHERE postId = " + this.getPostID();
        ResultSet rs = db.search(sql);
        while(rs.next()) {
            String reply = "User name: " + rs.getString("creatorName") + "Offered price: $" + rs.getDouble("value");
            replies.add(reply);
        }
        return replies;
    };

    //Soft delete
    public boolean deletePost() throws SQLException {
        if (this.isDeleted) { return true; }
        DB db = new DB();
        String sql = "UPDATE posts SET isDeleted = 1 WHERE id = "+ this.id;
        return db.update(sql);
    }

    //Close post
    public boolean closePost() throws SQLException {
        if (this.status.equals("CLOSED")) { return true; }
        DB db = new DB();
        String sql = "UPDATE posts SET status = 'CLOSED' WHERE id = "+ this.id;
        return db.update(sql);
    }

    //set close
    public  void setClose(){
        this.status = "CLOSED";
    }

    public static boolean updatePost(String sql) throws SQLException {
        DB db = new DB();
        return db.update(sql);
    }
    //set filter data
    static public ArrayList<String> getStatusA(){
        ArrayList<String> statusA = new ArrayList<>();
        statusA.add("All");
        statusA.add("OPEN");
        statusA.add("CLOSED");
        return statusA;
    }

    static public ArrayList<String> getTypes(){
        ArrayList<String> types = new ArrayList<>();
        types.add("All");
        types.add(Type.Event.toString());
        types.add(Type.Sale.toString());
        types.add(Type.Job.toString());
        return types;
    }

    public long getPostID(){ return this.id; };

    public void setPostID(long id){ this.id = id; }

    public String getPostName() { return this.idStr; }

    public String getTitle() { return this.title; }

    public String getDescription() { return this.description; }

    public long getCreatorID() { return this.creatorId; }

    public String getCreatorName() { return this.creatorName; }

    public String getStatus() { return this.status; }

    public String getImage() { return this.imageUrl; }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public boolean isClosed() {
        return this.status.equals("CLOSED");
    }

    // convert int value to boolean for reading from database
    static public boolean dbToClassTransDelValue(int isDeleted) {
        if (isDeleted == 1){
            return true;
        } else {
            return false;
        }
    }
    // convert boolean value to int for saving into database
    static public int classToDBTransDelValue(boolean isDeleted) {
        if (isDeleted) {
            return 1;
        } else {
            return 0;
        }
    }
}
