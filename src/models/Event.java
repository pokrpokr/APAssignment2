package models;

import Exceptions.AlreadyDeleteException;
import Exceptions.ExistException;
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
				events.add((Event) constructEvent(results));
			}
		}catch (Exception e){
		}
		return events;
	}

	static public Event findEvent(String idStr) throws AlreadyDeleteException {
		DB db = new DB();
		String sql = "SELECT * FROM posts WHERE idStr ='"+ idStr +"'";
		try{
			ResultSet results = db.search(sql);
			while (results.next()){
				if (dbToClassTransDelValue(results.getInt("isDeleted"))){
				} else {
					return (Event) constructEvent(results);
				}
			}
		} catch (SQLException se) {
			return null;
		}
		throw new AlreadyDeleteException("Event is already deleted");
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
	public boolean handleReply(Reply reply) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getVenue() { return this.venue; }

	public String getDate() { return this.date; }

	public int getCapacity() { return this.capacity; }

	public int getAttCount() { return this.attCount; }

	static public Post constructEvent(ResultSet results) throws SQLException {
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
		Post event = new Event(results.getLong("id"), results.getLong("creatorId"), dbToClassTransDelValue(results.getInt("isDeleted")),
				params, results.getInt("capacity"), results.getInt("attCount"));
		return event;
	}
}
