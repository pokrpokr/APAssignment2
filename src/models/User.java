package models;

import Exceptions.ExistException;
import database.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {
    private long id;
    private String userName;
    // TODO in the future, user can sign/log in with password
    private String password;

    static public ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();
        DB db = new DB();
        String sql = "SELECT * FROM users";
        try{
            ResultSet results = db.search(sql);
            while (results.next()){
                User user = new User(results.getLong("id"),results.getString("userName"));
                users.add(user);
            }
        }catch (Exception e){
        }

        return users;
    }
    // TODO check if user exist
    static public boolean isExist(String userName){
        DB db = new DB();
        String sql = "SELECT * FROM users where userName = '" + userName + "'";
        try{
            ResultSet results = db.search(sql);
            while (results.next()){
                return true;
            }
        }catch (Exception e){
        }
        return false;
    }

    static public User findUser(String userName){
        DB db = new DB();
        String sql = "SELECT * FROM users WHERE userName = '" + userName + "'";
        try{
            ResultSet results = db.search(sql);
            while (results.next()){
                User user = new User();
                user.setId(results.getLong("id"));
                user.setUserName(results.getString("userName"));
                return user;
            }
        }catch (Exception e){
        }
        return null;
    }

    static public User insertUser(String userName) throws ExistException {
        DB db = new DB();
        String sql = "INSERT INTO users(id,userName,password) VALUES(NULL,'"+ userName +"',NULL)";
        try {
            ResultSet results = db.insert(sql);
            while(results.next()){
                User user = new User(results.getLong(1), userName);
                return user;
            }
        } catch (ExistException ee){
            throw new ExistException("user already signup!");
        } catch (SQLException se) {

        }
        return null;
    }

    public User(){};

    public User(String name){ this.userName = name; }

    public User(long id, String userName){
        this.id = id;
        this.userName = userName;
    }

    protected void setId(long id){ this.id = id; }

    protected void setUserName(String userName){ this.userName = userName; }

    //TODO for letting user edit profile
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
