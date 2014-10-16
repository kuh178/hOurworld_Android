package edu.psu.ist.mtb_hourworld.items;


public class MTBTaskItems implements Cloneable{

	private String mAccessToken;
	private int mEID;
	private int mMemID;
	private String mDescription;
	private String mSvcCat;
	private String mFName;
	private String mLName;
	private int mSvcCatID;
	private int mSvcID;
	private String mService;
	private String mTimeStamp;
	private String mProfileImage;
	private String mEmailAddress;
	private String mPhoneNumber;
	
	private String mRequestType;
	private String mRequestMode;
	private String mIcon;
	private int mMembersCount;
	private int mCategoryCount;
	private String mLinkText;
	private String mSearchChars;
	
	private int mListMemID;
	private String mListMemName;
	private String mLastLoginTrans;
	private String mMilesAway;
	private String mCity;
	private String mBio;
	
	private String mQuestion;
	private String mAnswer;
	
	private int mPostNum;
	private String mExp;
	private String mEblast;
	private String mOwner;
	//private String mProfile;
	private String mUserName;
	
	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	
	private double mOLat = 0.0;
	private double mOLon = 0.0;
	private double mDLat = 0.0;
	private double mDLon = 0.0;
	
	private int mGroupID;
	private String mGroupName;
	
	private int mXDays = 14;
	
	public MTBTaskItems cloen() throws CloneNotSupportedException {
		
		MTBTaskItems sItem = (MTBTaskItems) super.clone();
		return sItem;
	}
	
	/*
	 * Set methods
	 */
	public void setAccessToken(String accessToken) {
		mAccessToken = accessToken;
	}
	
	public void setEID(int eID) {
		mEID = eID;
	}
	
	public void setMemID(int memID) {
		mMemID = memID;
	}
	
	public void setDescription(String description) {
		mDescription = description;
	}
	
	public void setSvcCat(String svcCat) {
		mSvcCat = svcCat;
	}
	
	public void setFName(String fName) {
		mFName = fName;
	}
	
	public void setLName(String lName) {
		mLName = lName;
	}
	
	public void setSvcCatID(int svcCatID) {
		mSvcCatID = svcCatID;
	}
	
	public void setSvcID(int svcID) {
		mSvcID = svcID;
	}
	
	public void setService(String service) {
		mService = service;
	}
	
	public void setTimeStamp(String timeStamp) {
		mTimeStamp = timeStamp;
	}
	
	public void setProfileImage(String profileImage) {
		mProfileImage = profileImage;
	}
	
	public void setEmailAddress(String emailAddress) {
		mEmailAddress = emailAddress;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}
	
	public void setRequestType(String requestType) {
		mRequestType = requestType;
	}
	
	public void setRequestMode(String requestMode) {
		mRequestMode = requestMode;
	}
	
	public void setIcon(String icon) {
		mIcon = icon;
	}
	
	public void setMembersCount(int cnt) {
		mMembersCount = cnt;
	}
	
	public void setCategoryCount(int count) {
		mCategoryCount = count;	
	}
	
	public void setLinkText(String linkText) {
		mLinkText = linkText;
	}
	
	public void setSearchChar(String chars) {
		mSearchChars = chars;
	}
	
	public void setListMemID(int memID) {
		mListMemID = memID;
	}
	
	public void setListMemName(String memName) {
		mListMemName = memName;
	}
	
	public void setLastLoginTrans(String lastLoginTrans) {
		mLastLoginTrans = lastLoginTrans;
	}
	
	public void setMilesAway(String milesAway) {
		mMilesAway = milesAway;
	}
	
	public void setCity(String city) {
		mCity = city;
	}
	
	public void setBio(String bio) {
		mBio = bio;
	}
	
	public void setQuestion(String question) {
		mQuestion = question;
	}
	
	public void setAnswer(String answer) {
		mAnswer = answer;
	}
	
	public void setPostNum(int postNum) {
		mPostNum = postNum;
	}
	
	public void setExp(String exp) {
		mExp = exp;
	}
	
	public void setEblast(String eBlast) {
		mEblast = eBlast;
	}
	
	public void setOwner(String owner) {
		mOwner = owner;
	}

	public void setUserName(String userName) {
		mUserName = userName;
	}
	
	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}
	
	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}
	
	public void setGroupName(String groupName) {
		mGroupName = groupName;
	}
	
	public void setGroupID(int groupID) {
		mGroupID = groupID;
	}
	
	public void setOLat(double lat) {
		mOLat = lat;
	}
	
	public void setOLon(double lon) {
		mOLon = lon;
	}
	
	public void setDLat(double lat) {
		mDLat = lat;
	}
	
	public void setDLon(double lon) {
		mDLon = lon;
	}
	
	public void setXDays(int days) {
		mXDays = days;
	}
	
	/*
	 * Get methods
	 */
	public String getAccessToken() {
		return mAccessToken;
	}
	
	public int getEID() {
		return mEID;
	}
	
	public int getMemID() {
		return mMemID;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public String getSvcCat() {
		return mSvcCat; 
	}
	
	public String getFName() {
		return mFName;
	}
	
	public String getLName() {
		return mLName;
	}

	public int getSvcCatID() {
		return mSvcCatID;
	}
	
	public int getSvcID() {
		return mSvcID;
	}
	
	public String getService() {
		return mService;
	}
	
	public String getTimeStamp() {
		return mTimeStamp;
	}
	
	public String getProfileImage() {
		return mProfileImage;
	}
	
	public String getEmailAddress() {
		return mEmailAddress;
	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	public String getRequestType() {
		return mRequestType;
	}
	
	public String getRequestMode() {
		return mRequestMode;
	}
	
	public String getIcon() {
		return mIcon;
	}
	
	public int getMembersCount() {
		return mMembersCount;
	}
	
	public int getCategoryCounts() {
		return mCategoryCount;
	}
	
	public String getLinkText() {
		return mLinkText;
	}
	
	public String getSearchChars() {
		return mSearchChars;
	}
	
	public int getListMemID() {
		return mListMemID;
	}
	
	public String getListMemName() {
		return mListMemName;
	}
	
	public String getLastLoginTrans() {
		return mLastLoginTrans;
	}
	
	public String getMilesAway() {
		return mMilesAway;
	}
	
	public String getCity() {
		return mCity;
	}
	
	public String getBio() {
		return mBio;
	}
	
	public String getQuestion() {
		return mQuestion;
	}
	
	public String getAnswer() {
		return mAnswer;
	}

	public int getPostNum() {
		return mPostNum;
	}
	
	public String getExp() {
		return mExp;
	}
	
	public String getEblast() {
		return mEblast; 
	}
	
	public String getOwner() {
		return mOwner;
	}

	public String getUserName() {
		return mUserName;
	}

	public double getLatitude() {
		return mLatitude;
	}
	
	public double getLongitude() {
		return mLongitude;
	}
	
	public String getGroupName() {
		return mGroupName;
	}
	
	public int getGroupID() {
		return mGroupID;
	}
	
	public double getOLat() {
		return mOLat;
	}
	
	public double getOLon() {
		return mOLon;
	}
	
	public double getDLat() {
		return mDLat;
	}
	
	public double getDLon() {
		return mDLon;
	}
	
	public int getXDays() {
		return mXDays;
	}

}

