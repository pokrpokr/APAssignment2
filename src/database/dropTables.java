/*
  this file is for droping tables of unilink system
 */

package database;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class dropTables {
    public static void main(String[] args) {
        final String DB_NAME = "uniLinkDB";
        final String TABLE_NAME_1 = "USERS";
        final String TABLE_NAME_2 = "REPLIES";
        final String TABLE_NAME_3 = "POSTS";

        try (Connection con = connectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();
        ) {
            int result_1 = stmt.executeUpdate("DROP TABLE " + TABLE_NAME_1.toLowerCase() );

            if(result_1 == 0) {
                System.out.println("Table " + TABLE_NAME_1 + " has been deleted successfully");
            } else {
                System.out.println("Table " + TABLE_NAME_1 + " was not deleted");
            }

            int result_2 = stmt.executeUpdate("DROP TABLE " + TABLE_NAME_2.toLowerCase());

            if(result_2 == 0) {
                System.out.println("Table " + TABLE_NAME_2 + " has been deleted successfully");
            } else {
                System.out.println("Table " + TABLE_NAME_2 + " was not deleted");
            }

            int result_3 = stmt.executeUpdate("DROP TABLE " + TABLE_NAME_3.toLowerCase());

            if(result_3 == 0) {
                System.out.println("Table " + TABLE_NAME_3 + " has been deleted successfully");
            } else {
                System.out.println("Table " + TABLE_NAME_3 + " was not deleted");
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
