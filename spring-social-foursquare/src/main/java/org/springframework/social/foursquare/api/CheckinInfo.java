package org.springframework.social.foursquare.api;

import java.util.ArrayList;
import java.util.List;

public class CheckinInfo {

	private int count;
	private List<Checkin> items;

    public CheckinInfo()
    {
        count = 0;
        items = new ArrayList<Checkin>();
    }
	
	public CheckinInfo(int count, List<Checkin> items) {
		this.count = count;
		this.items = items;
	}

	public int getCount() {
		return count;
	}

	public List<Checkin> getItems() {
		return items;
	}

    public void setCount(int count) {
        this.count = count;
    }

    public void setItems(List<Checkin> items) {
        this.items = items;
    }
}
