package models;

public class Sale extends Post {


	public Sale(long user_id, String creatorName, String idStr, String title, String description, String imageUrl) {
		super(user_id, creatorName, idStr, title, description, imageUrl);
	}

	//	public Sale(int user_id, String[] args) {
//		super(user_id, args);
//		// TODO Auto-generated constructor stub
//	}
//
	@Override
	public boolean handleReply(Reply reply) {
		// TODO Auto-generated method stub
		return false;
	}

}
