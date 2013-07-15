package org.springframework.social.foursquare.api;

public class VenueStats {
	
	private int checkinsCount;
	private int usersCount;
    private int tipCount;

    public VenueStats() {
    }

    public VenueStats(int checkinsCount, int usersCount, int tipCount) {
		this.checkinsCount = checkinsCount;
		this.usersCount = usersCount;
        this.tipCount = tipCount;
	}

	public int getCheckinsCount() {
		return checkinsCount;
	}

	public void setCheckinsCount(int checkinsCount) {
		this.checkinsCount = checkinsCount;
	}

	public int getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

    public int getTipCount() {
        return tipCount;
    }

    public void setTipCount(int tipCount) {
        this.tipCount = tipCount;
    }
}
