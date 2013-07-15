package org.springframework.social.foursquare.api;

import java.util.List;

public class FriendInfo {
	
	private int total;
	private List<FoursquareUserGroup> groups;

    public FriendInfo() {
    }

    public FriendInfo(int total, List<FoursquareUserGroup> groups) {
		this.total = total;
		this.groups = groups;
	}

	public int getTotal() {
		return total;
	}

	public List<FoursquareUserGroup> getGroups() {
		return groups;
	}
}
