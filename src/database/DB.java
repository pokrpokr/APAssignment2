package database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	private Connection con = null;
	private Statement  stmt = null;

	public DB() {
		final String DB_NAME = "uniLinkDB";
		try{
			this.con = connectionTest.getConnection(DB_NAME);
			this.stmt = con.createStatement();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ResultSet search(String sql){
		ResultSet resultSet = null;
		try {
			resultSet = stmt.executeQuery(sql);
		} catch (SQLException se) {}
		return  resultSet;
	}

	public long count(String tbName){
		String sql = "SELECT COUNT(*) FROM " + tbName;
		long count = 0;
		try {
			ResultSet resultSet = stmt.executeQuery(sql);
			count = resultSet.getLong("total");
		} catch (SQLException se) {
		}

		return count;
	}

	public void close(){
		try {
			con.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}
