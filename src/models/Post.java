package models;

import database.DB;
import javafx.geometry.Pos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    protected enum Type {Event, Sale, Job};

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

    abstract public boolean handleReply(Reply reply);

    static public ArrayList<Post> getPosts(){
        ArrayList<Post> posts = new ArrayList<>();
        DB db = new DB();
        String sql = "SELECT * FROM posts WHERE isDeleted = 0";
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

    public String getPostDetails(String currentUser) {
        String format = "%-20s%s";
        String information = String.format(format, "Id:", id) + "\n"
                + String.format(format, "Title", title) + "\n"
                + String.format(format, "Description:", description) + "\n"
                + String.format(format, "Creator ID:", creatorId) + "\n"
                + String.format(format, "Status:", status) + "\n";
        return information;
    }

    //Soft delete
    public boolean deletePost() throws SQLException {
        if (this.isDeleted) { return true; }
        DB db = new DB();
        String sql = "UPDATE posts SET isDeleted = "+ classToDBTransDelValue(this.isDeleted) + " WHERE id = "+ this.id;
        return db.update(sql);
    }

    //Close post
    public boolean closePost() throws SQLException {
        if (this.status.equals("CLOSED")) { return true; }
        DB db = new DB();
        String sql = "UPDATE posts SET status = 'CLOSED' WHERE id = "+ this.id;
        return db.update(sql);
    }

    public static boolean updatePost(String sql) throws SQLException {
        DB db = new DB();
        return db.update(sql);
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

//    public void setIsDeleted(int isDeleted) {
//        if (isDeleted == 1){
//            this.isDeleted = true;
//        } else {
//            this.isDeleted = false;
//        }
//    }



//    public boolean setReplies(Reply reply) {
//        try {
//            this.replies.add(reply);
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public boolean isClosed() {
        return this.status.equals("CLOSED");
    }

    static public boolean dbToClassTransDelValue(int isDeleted) {
        if (isDeleted == 1){
            return true;
        } else {
            return false;
        }
    }

    static public int classToDBTransDelValue(boolean isDeleted) {
        if (isDeleted) {
            return 1;
        } else {
            return 0;
        }
    }
}
