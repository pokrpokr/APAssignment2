package models;

import database.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private long id;
    private String userName;
    private String password;

    static public ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();
        DB db = new DB();
        String sql = "SELECT * FROM users";
        ResultSet results = db.search(sql);
        try{
            while (results.next()){
                User user = new User(results.getLong("id"),results.getString("userName"));
                users.add(user);
            }
        }catch (Exception e){
        }

        return users;
    }

    static public User findUser(String userName){
        User user = null;
        DB db = new DB();
        return user;
    }

    public User(String name){ this.userName = name; }

    public User(long id, String userName){
        this.id = id;
        this.userName = userName;
    }
    protected void setId(long id){ this.id = id; }

    public boolean updateUserName(String newName) {
        this.userName = newName;
        if (userName.equals(newName)){
            return true;
        } else {
            return false;
        }
    }

    public long getId(){ return this.id; }

    public String getUserName(){ return this.userName; }
}
