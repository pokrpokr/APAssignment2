package models;

import Exceptions.DBSaveException;
import Exceptions.ExistException;
import Exceptions.ValidationException;
import controller.MainViewController;
import database.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Sale extends Post {
	private double askingPrice;
	private double highestOffer = 0.0;
	private double minimumRaise;

	public Sale(long user_id, String creatorName, String idStr, String title, String description, String imageUrl, double askingPrice, double minimumRaise) {
		super(user_id, creatorName, idStr, title, description, imageUrl);
		this.askingPrice = askingPrice;
		this.minimumRaise = minimumRaise;
	}

	public Sale(long id, long user_id, boolean isDeleted, String[] args, double askingPrice, double highestOffer, double minimumRaise) {
		super(id, user_id, isDeleted, args);
		this.askingPrice = askingPrice;
		this.highestOffer = highestOffer;
		this.minimumRaise = minimumRaise;
	}

	public static String generateId() throws SQLException {
		DB db = new DB();
		int saleSize   = db.count("posts", Type.Sale.toString());
		int saleAmount = saleSize + 1;
		String sAmount  = "";
		if (saleAmount < 10) {
			sAmount = "00" + saleAmount;
		}else if (saleAmount < 100) {
			sAmount = "0" + saleAmount;
		}else {
			sAmount = Integer.toString(saleAmount);
		}
		String salId = "SAL"+ sAmount;
		return salId;
	}

	public Sale createSale(User currentUser) throws ExistException, SQLException {
		DB db = new DB();
		String sql = "INSERT INTO posts(idStr, title, description, imageUrl, status, creatorName, creatorId, askingPrice, highestOffer, minimumRaise, isDeleted, type) " +
				"VALUES('"+ this.getPostName() +"', '"+ this.getTitle() +"', '"+ this.getDescription() +"', '"+ this.getImage() +"', '"+ this.getStatus() +"', '"+ this.getCreatorName() +"', " +
				"'"+ this.getCreatorID() +"', "+ this.getAskingPrice() +", "+ this.getHighestOffer() +", "+ this.getMinimumRaise() +", " +
				classToDBTransDelValue(this.isDeleted()) +", '"+ Type.Sale.toString() +"')";
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
	public boolean handleReply(double offer) throws SQLException, ValidationException, ExistException, DBSaveException {
		if (offer < (this.highestOffer + this.minimumRaise)) {
			throw new ValidationException("Minimum raise is $" + this.minimumRaise + "!");
		}
		if (offer >= this.askingPrice) {
			this.closePost();
		}
		this.setHighestOffer(offer);

		MainViewController mC = new MainViewController();
		Reply reply = new Reply(this.getPostID(), mC.currentUser.getId(), mC.currentUser.getUserName(), offer);
		DB db = new DB();
		if (reply.saveReply() != null){
			String sql = "UPDATE posts SET highestOffer = " + this.getHighestOffer() + " WHERE id = " + this.getPostID();
			if (db.update(sql)) {
				return true;
			} else {
				throw new DBSaveException("Create Reply Failed");
			}
		} else {
			throw new DBSaveException("Create Reply Failed");
		}
	}

	static public Sale constructSale(ResultSet results) throws SQLException {
		String[] params = {
				results.getString("creatorName"),
				results.getString("idStr"),
				results.getString("title"),
				results.getString("description"),
				results.getString("imageUrl"),
				results.getString("status"),
		};
		Sale sale = new Sale(results.getLong("id"), results.getLong("creatorId"), dbToClassTransDelValue(results.getInt("isDeleted")),
				params, results.getDouble("askingPrice"), results.getDouble("highestOffer"), results.getDouble("minimumRaise"));
		return sale;
	}

	public void setHighestOffer(double offer){ this.highestOffer = offer; }

	public double getAskingPrice() { return this.askingPrice; }

	public double getHighestOffer() { return this.highestOffer; }

	public  double getMinimumRaise() { return this.minimumRaise; }
}
