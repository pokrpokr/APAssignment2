package models;

import Exceptions.ExistException;
import database.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Reply {
    private long id;
    private long creatorId;
    private String creatorName;
    private long postId;
    private double value;

    public Reply(long postId, long creatorId, String creatorName, double value) {
        this.postId = postId;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.value = value;
    }

    public Reply(long id, long postId, long creatorId, String creatorName, double value) {
        this.id = id;
        this.postId = postId;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.value = value;
    }

    public Reply saveReply() throws ExistException, SQLException {
        DB db = new DB();
        String sql = "INSERT INTO replies(postId, creatorId, creatorName, value) VALUES ("+ this.getPostId() +", "+ this.getCreatorId()
                +", '"+ this.getCreatorName() +"', "+ this.getValue() +")";
        try{
            ResultSet rs = db.insert(sql);
            while(rs.next()) {
                this.setId(rs.getLong(1));
                return this;
            }
        } catch (ExistException e) {
            throw new ExistException("Reply already exist!");
        }
        return null;
    }

    public boolean deleteReply() throws SQLException {
        DB db = new DB();
        String sql = "DELETE FROM replies WHERE id = "+this.getId();
        return db.update(sql);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public long getPostId() {
        return	postId;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public double getValue() {
        return value;
    }

    public void setPostId(long pId) {
            this.postId = pId;
    }

    public void setCreatorId(long creatorId) {
            this.creatorId = creatorId;
    }

    public void setValue(Double value) {
            this.value = value;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorName() {
        return this.creatorName;
    }
}
