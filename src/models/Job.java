package models;

import database.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Job extends Post {
	private double proposedPrice;
	private double lowestOffer;

	public Job(long user_id, String creatorName, String idStr, String title, String description, String imageUrl, double proposedPrice) {
		super(user_id, creatorName, idStr, title, description, imageUrl);
		this.proposedPrice = proposedPrice;
		//Initial lowest offer as same as proposed price
		this.lowestOffer   = proposedPrice;
	}

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

	@Override
	public boolean handleReply(Reply reply) {
		// TODO Auto-generated method stub
		return false;
	}

	static public Job constructJob(ResultSet results) throws SQLException {
		String[] params = {
				results.getString("creatorName"),
				results.getString("idStr"),
				results.getString("title"),
				results.getString("description"),
				results.getString("imageUrl"),
		};
		Job job = new Job(results.getLong("id"), results.getLong("creatorId"), dbToClassTransDelValue(results.getInt("isDeleted")),
				params, results.getDouble("proposedPrice"), results.getDouble("lowestOffer"));
		return job;
	}

	public double getProposedPrice() { return this.proposedPrice; }

	public double getLowestOffer() { return this.lowestOffer; }
}
