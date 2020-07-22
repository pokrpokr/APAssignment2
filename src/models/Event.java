package models;

import Exceptions.AlreadyDeleteException;
import Exceptions.DBSaveException;
import Exceptions.ExistException;
import controller.MainViewController;
import database.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Event extends Post {
	private String venue;
	private String date;
	private int capacity;
	// Attendee count
	private int attCount = 0;
	
	public Event(long user_id, String creatorName, String idStr, String title, String description, String imageUrl, String venue, String date, int capacity) {
		super(user_id, creatorName, idStr, title, description, imageUrl);
		this.venue    = venue;
		this.date     = date;
		this.capacity = capacity;
	}

	// read from database constructor
	public Event(long id, long user_id, boolean isDeleted, String[] args, int capacity, int attCount) {
		super(id, user_id, isDeleted, args);
		this.venue    = args[6];
		this.date     = args[7];
		this.capacity = capacity;
		this.attCount = attCount;
	}

	public static String generateId() throws SQLException {
		DB db = new DB();
		int eventSize   = db.count("posts", Type.Event.toString());
		int eventAmount = eventSize + 1;
		String eAmount  = "";
		if (eventAmount < 10) {
			eAmount = "00" + eventAmount;
		}else if (eventAmount < 100) {
			eAmount = "0" + eventAmount;
		}else {
			eAmount = Integer.toString(eventAmount);
		}
		String eveId = "EVE"+ eAmount;
		return eveId;
	}

	static public ArrayList<Event> getEvents(){
		ArrayList<Event> events = new ArrayList<>();
		DB db = new DB();
		String sql = "SELECT * FROM posts Where type = '"+ Type.Event.toString() +"' AND isDeleted = "+ 0;
		try{
			ResultSet results = db.search(sql);
			while (results.next()){
				events.add(constructEvent(results));
			}
		}catch (Exception e){
		}
		return events;
	}
	
	public Event createEvent(User currentUser) throws ExistException, SQLException {
		DB db = new DB();
		String sql = "INSERT INTO posts(idStr, title, description, imageUrl, status, creatorName, creatorId, venue, date, capacity, attCount, isDeleted, type) " +
				"VALUES('"+ this.getPostName() +"', '"+ this.getTitle() +"', '"+ this.getDescription() +"', '"+ this.getImage() +"', '"+ this.getStatus() +"', '"+ this.getCreatorName() +"', " +
				"'"+ this.getCreatorID() +"','"+ this.getVenue() +"', '"+ this.getDate() +"', '"+ this.getCapacity() +"', '"+ this.getAttCount() +"'," +
				classToDBTransDelValue(this.isDeleted()) +", '"+ Type.Event.toString() +"')";
		try {
			ResultSet results = db.insert(sql);
			while(results.next()){
				this.setPostID(results.getLong(1));
				return this;
			}
		} catch (ExistException ee){
			throw new ExistException("event already exist!");
		}
		return null;
	}

	@Override
	public boolean handleReply(double offer) throws SQLException, ExistException, DBSaveException {
		// TODO Auto-generated method stub
		MainViewController mC = new MainViewController();
		Reply reply = new Reply(this.getPostID(), mC.currentUser.getId(), mC.currentUser.getUserName(), offer);
		DB db = new DB();
		if (reply.saveReply() != null){
			attCount++;
			String sql = "UPDATE posts SET attCount = " + attCount + " WHERE id = " + this.getPostID();
			if (db.update(sql)) {
				return true;
			} else {
				reply.deleteReply();
				throw new DBSaveException("Create Reply Failed");
			}
		} else {
			throw new DBSaveException("Create Reply Failed");
		}
	}

	@Override
	public ArrayList<String> getReplies() throws SQLException {
		ArrayList<String> replies = new ArrayList<>();
		DB db = new DB();
		String sql = "SELECT * FROM replies WHERE postId = " + this.getPostID();
		ResultSet rs = db.search(sql);
		while(rs.next()) {
			String reply = "Attender: User name: " + rs.getString("creatorName");
			replies.add(reply);
		}
		return replies;
	}

	public String getVenue() { return this.venue; }

	public String getDate() { return this.date; }

	public int getCapacity() { return this.capacity; }

	public int getAttCount() { return this.attCount; }

	static public Event constructEvent(ResultSet results) throws SQLException {
		String[] params = {
				results.getString("creatorName"),
				results.getString("idStr"),
				results.getString("title"),
				results.getString("description"),
				results.getString("imageUrl"),
				results.getString("status"),
				results.getString("venue"),
				results.getString("date")
		};
		Event event = new Event(results.getLong("id"), results.getLong("creatorId"), dbToClassTransDelValue(results.getInt("isDeleted")),
				params, results.getInt("capacity"), results.getInt("attCount"));
		return event;
	}
}
