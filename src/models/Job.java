package models;

import Exceptions.DBSaveException;
import Exceptions.ExistException;
import Exceptions.ValidationException;
import controller.MainViewController;
import database.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Job extends Post {
	private double proposedPrice;
	private double lowestOffer;

	public Job(long user_id, String creatorName, String idStr, String title, String description, String imageUrl, double proposedPrice) {
		super(user_id, creatorName, idStr, title, description, imageUrl);
		this.proposedPrice = proposedPrice;
		//Initial lowest offer as same as proposed price
		this.lowestOffer   = proposedPrice;
	}

	// read from database constructor
	public Job(long id, long user_id, boolean isDeleted, String[] args, double proposedPrice, double lowestOffer) {
		super(id, user_id, isDeleted, args);
		this.proposedPrice = proposedPrice;
		this.lowestOffer   = lowestOffer;
	}

	public static String generateId() throws SQLException {
		DB db = new DB();
		int jobSize   = db.count("posts", Type.Job.toString());
		int jobAmount = jobSize + 1;
		String jAmount  = "";
		if (jobAmount < 10) {
			jAmount = "00" + jobAmount;
		}else if (jobAmount < 100) {
			jAmount = "0" + jobAmount;
		}else {
			jAmount = Integer.toString(jobAmount);
		}
		String jobId = "JOB"+ jAmount;
		return jobId;
	}

	public Job createJob(User currentUser) throws ExistException, SQLException {
		DB db = new DB();
		String sql = "INSERT INTO posts(idStr, title, description, imageUrl, status, creatorName, creatorId, proposedPrice, lowestOffer, isDeleted, type) " +
				"VALUES('"+ this.getPostName() +"', '"+ this.getTitle() +"', '"+ this.getDescription() +"', '"+ this.getImage() +"', '"+ this.getStatus() +"', '"+ this.getCreatorName() +"', " +
				"'"+ this.getCreatorID() +"', "+ this.getProposedPrice() +", "+ this.getLowestOffer() +", " +
				classToDBTransDelValue(this.isDeleted()) +", '"+ Type.Job.toString() +"')";
		try {
			ResultSet results = db.insert(sql);
			while(results.next()){
				this.setPostID(results.getLong(1));
				return this;
			}
		} catch (ExistException ee){
			throw new ExistException("sale already exist!");
		}
		return null;
	}

	@Override
	public boolean handleReply(double offer) throws ValidationException, ExistException, SQLException, DBSaveException {
		// TODO Auto-generated method stub
		if (offer >= this.lowestOffer) {
			throw new ValidationException("There is a lowest offer!");
		}
		setLowestOffer(offer);
		MainViewController mC = new MainViewController();
		Reply reply = new Reply(this.getPostID(), mC.currentUser.getId(), mC.currentUser.getUserName(), offer);
		DB db = new DB();
		if (reply.saveReply() != null){
			String sql = "UPDATE posts SET lowestOffer = " + this.getLowestOffer() + " WHERE id = " + this.getPostID();
			if (db.update(sql)) {
				return true;
			} else {
				throw new DBSaveException("Create Reply Failed");
			}
		} else {
			throw new DBSaveException("Create Reply Failed");
		}
	}

	static public Job constructJob(ResultSet results) throws SQLException {
		String[] params = {
				results.getString("creatorName"),
				results.getString("idStr"),
				results.getString("title"),
				results.getString("description"),
				results.getString("imageUrl"),
				results.getString("status"),
		};
		Job job = new Job(results.getLong("id"), results.getLong("creatorId"), dbToClassTransDelValue(results.getInt("isDeleted")),
				params, results.getDouble("proposedPrice"), results.getDouble("lowestOffer"));
		return job;
	}

	public double getProposedPrice() { return this.proposedPrice; }

	public double getLowestOffer() { return this.lowestOffer; }

	private void setLowestOffer(double offer) {
		this.lowestOffer = offer;
	}
}
