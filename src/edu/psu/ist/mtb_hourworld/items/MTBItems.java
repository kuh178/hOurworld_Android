package edu.psu.ist.mtb_hourworld.items;


public class MTBItems implements Cloneable{

	private int tbID;
	private int tbUserID;
	private int tbAcceptUserID;
	private int tbState;
	private int tbUserImageID;
	private int tbGroupID;
	private int tbOtherUserImageID;
	
	private String tbUsername;
	private String tbTakenUsername;
	private String tbPass;
	private String tbType;
	private String tbTitle;
	private String tbCategory;
	private String tbDescription;
	
	private String tbFrom;
	private String tbTo;
	private String tbUDate;
	private String tbTakenDate;
	private String tbProcessDate;
	private String tbUserEmail;
	private String tbTakenUserEmail;
	private String tbTakenUserFBAuth;
	
	private long tbAmount;
	
	private double tbLatitude;
	private double tbLongitude;
	
	private boolean tbFBuser;
	

	public MTBItems cloen() throws CloneNotSupportedException {
		
		MTBItems sItem = (MTBItems) super.clone();
		return sItem;
	}
	// set functions
	public void setID(int id) {
		tbID = id;
	}
	
	public void setUserID(int num) {
		tbUserID = num;
	}
	
	public void setAcceptUserID(int aUser) {
		tbAcceptUserID = aUser;
	}
	
	public void setState(int state) {
		tbState = state;
	}
	
	public void setUserImageID(int imageID) {
		tbUserImageID = imageID;
	}
	
	public void setGroupID(int groupID) {
		tbGroupID = groupID;
	}
	
	public void setOtherUserImageID(int userImageID) {
		tbOtherUserImageID = userImageID;
	}
	
	public void setUsername(String username) {
		tbUsername = username;
	}
	
	public void setTakenUsername(String takenUName) {
		tbTakenUsername = takenUName;
	}
	
	public void setPass(String pass) {
		tbPass = pass;
	}
	
	public void setType(String type) {
		tbType = type;
	}
	
	public void setTitle(String title) {
		tbTitle = title;
	}
	
	public void setCategory(String category) {
		tbCategory = category;
	}
	
	public void setDescription(String description) {
		tbDescription = description;
	}
	
	public void setFrom(String from) {
		tbFrom = from;
	}
	
	public void setTo(String to) {
		tbTo = to;
	}
	
	public void setUDate(String uDate) {
		tbUDate = uDate;
	}
	
	public void setTBTakenDate(String date) {
		tbTakenDate = date;
	}
	
	public void setProcessDate(String date) {
		tbProcessDate = date;
	}
	
	public void setUserEmail(String email) {
		tbUserEmail = email;
	}
	
	public void setTakenUserEmail(String email) {
		tbTakenUserEmail = email;
	}
	
	public void setLatitude(double lat) {
		tbLatitude = lat;
	}
	
	public void setLongitude(double lng) {
		tbLongitude = lng;
	}
	
	public void setFBuser(boolean user) {
		tbFBuser = user;
	}
	
	public void setAmount(long amount) {
		tbAmount = amount;
	}
	
	public void setTakenUserFBAuth(String fb_auth) {
		tbTakenUserFBAuth = fb_auth;
	}
	
	// get functions
	public int getID() {
		return tbID;
	}
	
	public int getAcceptUserID() {
		return tbAcceptUserID;
	}
	
	public int getState() {
		return tbState;
	}
	
	public int getUserImageID() {
		return tbUserImageID;
	}
	
	public int getGroupID() {
		return tbGroupID;
	}
	
	public int getOtherUserImageID() {
		return tbOtherUserImageID;
	}
	
	public String getUserName() {
		return tbUsername;
	}
	
	public String getTakenUsername() {
		return tbTakenUsername;
	}
	
	public String getPass() {
		return tbPass;
	}
	
	public int getUserID() {
		return tbUserID;
	}
	
	public String getTitle() {
		return tbTitle;
	}
	
	public String getDescription() {
		return tbDescription;
	}
	
	public String getCategory() {
		return tbCategory;
	}
	
	public String getType() {
		return tbType;
	}
	
	public String getFrom() {
		return tbFrom;
	}
	
	public String getTo() {
		return tbTo;
	}
	
	public String getUDate() {
		return tbUDate;
	}
	
	public String getTBTakenDate() {
		return tbTakenDate;
	}
	
	public String getProcessDate() {
		return tbProcessDate;
	}
	
	public String getUserEmail() {
		return tbUserEmail;
	}
	
	public String getTakenUserEmail() {
		return tbTakenUserEmail;
	}

	public double getLatitude() {
		return tbLatitude;
	}
	
	public double getLongitude() {
		return tbLongitude;
	}
	
	public boolean getFBuser() {
		return tbFBuser;
	}
	
	public long getAmount() {
		return tbAmount;
	}
	
	public String getTakenUserTBAuth() {
		return tbTakenUserFBAuth;
	}
}
