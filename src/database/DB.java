/*
  DB methods: search, insert, update, count(counting rows of tables), close(closing connection)
 */

package database;
import Exceptions.ExistException;

import java.sql.*;

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

	public ResultSet search(String sql) throws SQLException {
		ResultSet resultSet = null;
		resultSet = stmt.executeQuery(sql);
		this.close();
		return  resultSet;
	}

	public ResultSet insert(String sql) throws ExistException, SQLException {
		ResultSet resultSet = null;
		try{
			int result = stmt.executeUpdate(sql);
			con.commit();
			if (result == 0){
				return null;
			}
			resultSet = stmt.executeQuery("CALL IDENTITY();");
		} catch (SQLIntegrityConstraintViolationException une) {
			throw new ExistException();
		}
		this.close();
		return resultSet;
	}

	public boolean update(String sql) throws SQLException {
			int result = stmt.executeUpdate(sql);
			con.commit();
			this.close();
			if (result <= 0){
				return false;
			} else {
				return true;
			}
	}

	// For posts table
	public int count(String tbName, String type) throws SQLException {
		String sql;
		if (type.isBlank()) {
			sql = "SELECT COUNT(*) FROM " + tbName;
		} else {
			sql = "SELECT COUNT(*) FROM " + tbName + " WHERE type = '"+ type +"'";
		}
		int count = -1;
		ResultSet resultSet = stmt.executeQuery(sql);
		while(resultSet.next()){
			count = resultSet.getInt(1);
		}
		this.close();
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
