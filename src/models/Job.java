package models;

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

//	public Job(int user_id, String[] args) {
//		super(user_id, args);
//		// TODO Auto-generated constructor stub
//	}

	@Override
	public boolean handleReply(Reply reply) {
		// TODO Auto-generated method stub
		return false;
	}

}
