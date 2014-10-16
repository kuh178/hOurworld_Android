package edu.psu.ist.mtb_hourworld.items;


public class MTBGroupItems implements Cloneable{

	private int mGroupID = 0;
	private String mGroupName = "No group name yet";
	private String mGroupProfile = "No group description yet";
	private String mGroupOwnerName = "";
	private int mGroupOwnerID = 0;
	
	public MTBGroupItems cloen() throws CloneNotSupportedException {
		
		MTBGroupItems sItem = (MTBGroupItems) super.clone();
		return sItem;
	}
	
	/*
	 * set methods 
	 */
	
	public void setGroupID(int groupID) {
		mGroupID = groupID;
	}
	
	public void setGroupName(String groupName) {
		mGroupName = groupName;
	}
	
	public void setGroupProfile(String groupProfile) {
		mGroupProfile = groupProfile;
	}
	
	public void setGroupOwnerID(int groupOwnerID) {
		mGroupOwnerID = groupOwnerID;
	}
	
	public void setGroupOwnerName(String name) {
		mGroupOwnerName = name;
	}
	
	/*
	 * get methods
	 */
	
	public int getGroupID() {
		return mGroupID;
	}
	
	public String getGroupName() {
		return mGroupName;
	}
	
	public String getGroupProfile() {
		return mGroupProfile;
	}
	
	public int getGroupOwnerID() {
		return mGroupOwnerID;
	}
	
	public String getGroupOwnerName() {
		return mGroupOwnerName;
	}
}

