package models;

import java.util.ArrayList;

public abstract class Post {
    private int id;
    private int creatorId;
    private String title;
    private String description;
    private String idStr;
    private String creatorName;
    private String status;
    private ArrayList<Reply> replies;
    private boolean isDeleted = false;

    public Post(int user_id, String[] args) {
        this.creatorId = user_id;
        this.idStr = args[0];
        this.title = args[1];
        this.description = args[2];
        this.creatorName = args[3];
        this.status = "OPEN";
        this.replies = new ArrayList<Reply>();
    }

    abstract public boolean handleReply(Reply reply);

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
    public boolean deletePost() {
        this.isDeleted = true;
        return isDeleted;
    }

    //Close post
    public boolean closePost() {
        this.status = "CLOSED";
        return true;
    }

    public String getPostName() {
        return this.idStr;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public ArrayList<Reply> getReplies() {
        return this.replies;
    }

    public boolean setReplies(Reply reply) {
        try {
            this.replies.add(reply);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public boolean isClosed() {
        return this.status.equals("CLOSED");
    }
}
