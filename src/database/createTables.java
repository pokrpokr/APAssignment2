package database;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class createTables {
    public static void main(String[] args) throws SQLException {

        final String DB_NAME = "uniLinkDB";
        final String TABLE_NAME_1 = "USERS";
        final String TABLE_NAME_2 = "REPLIES";
        final String TABLE_NAME_3 = "POSTS";

        //use try-with-resources Statement
        try (Connection con = connectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();
        ) {
            int c_result_1 = stmt.executeUpdate("CREATE TABLE users ("
                    + "id INTEGER GENERATED BY DEFAULT AS IDENTITY,"
                    + "userName VARCHAR(255) NOT NULL,"
                    + "password VARCHAR(255) DEFAULT NULL,"
                    + "PRIMARY KEY (id))");
            if(c_result_1 == 0) {
                System.out.println("Table " + TABLE_NAME_1 + " has been created successfully");
            } else {
                System.out.println("Table " + TABLE_NAME_1 + " is not created");
            }

            int c_result_2 = stmt.executeUpdate("CREATE TABLE replies ("
                    + "id INTEGER GENERATED BY DEFAULT AS IDENTITY,"
                    + "post_id INTEGER NOT NULL,"
                    + "creator_id INTEGER NOT NULL,"
                    + "creator_name VARCHAR(255) NOT NULL,"
                    + "value DOUBLE NOT NULL,"
                    + "PRIMARY KEY (id))");

            if(c_result_2 == 0) {
                System.out.println("Table " + TABLE_NAME_2 + " has been created successfully");
            } else {
                System.out.println("Table " + TABLE_NAME_2 + " is not created");
            }

            int c_result_3 = stmt.executeUpdate("CREATE TABLE posts("
                    + "id INTEGER GENERATED BY DEFAULT AS IDENTITY,"
                    + "creatorId INTEGER NOT NULL,"
                    + "idStr VARCHAR(50) NOT NULL,"
                    + "creatorName VARCHAR(255) NOT NULL,"
                    + "title VARCHAR (100) NOT NULL,"
                    + "description VARCHAR(1000) NOT NULL,"
                    + "status VARCHAR(10),"
                    + "isDeleted BIT DEFAULT 0,"
                    + "type VARCHAR(10) NOT NUll,"
                    + "venue VARCHAR(50) DEFAULT NULL,"
                    + "date VARCHAR(50) DEFAULT NULL,"
                    + "capacity INTEGER DEFAULT NULL,"
                    + "attCount INTEGER DEFAULT NULL,"
                    + "proposedPrice DOUBLE DEFAULT NULL,"
                    + "lowestOffer DOUBLE DEFAULT NULL,"
                    + "askingPrice DOUBLE DEFAULT NULL,"
                    + "highestOffer DOUBLE DEFAULT NULL,"
                    + "minimumRaise DOUBLE DEFAULT NULL,"
                    + "PRIMARY KEY (id))");

            if(c_result_3 == 0) {
                System.out.println("Table " + TABLE_NAME_3 + " has been created successfully");
            } else {
                System.out.println("Table " + TABLE_NAME_3 + " is not created");
            }

            int add_index = stmt.executeUpdate("CREATE INDEX UID ON posts (creatorId)");
            if (add_index == 0){
                System.out.println("Table " + TABLE_NAME_3 + " add index successfully");
            } else {
                System.out.println("Table " + TABLE_NAME_3 + " add index failed");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
