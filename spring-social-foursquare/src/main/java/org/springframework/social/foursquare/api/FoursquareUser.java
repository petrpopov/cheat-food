package org.springframework.social.foursquare.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"tips", "lists"})
public class FoursquareUser {
	
	private String id;
	private String firstName;
	private String lastName;
	private String photoUrl;
	private String gender;
	private String homeCity;
	private String relationship;
	private boolean pings;
	private ContactInfo contact;
	private MayorshipInfo mayorshipInfo;
	private CheckinInfo checkinInfo;
	private FriendInfo friendInfo;
	private Scores scores;
    private FoursquareUserPhoto photo;
    private String bio;


    public FoursquareUser() {
    }

    public FoursquareUser(String id, String firstName, String lastName, String photoUrl,
			String gender, String homeCity, String relationship) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.photoUrl = photoUrl;
		this.gender = gender;
		this.homeCity = homeCity;
		this.relationship = relationship;
	}

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public FoursquareUserPhoto getPhoto() {
        return photo;
    }

    public void setPhoto(FoursquareUserPhoto photo) {
        this.photo = photo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setPings(boolean pings) {
        this.pings = pings;
    }

    public void setContact(ContactInfo contactInfo) {
        this.contact = contactInfo;
    }

    public void setMayorshipInfo(MayorshipInfo mayorshipInfo) {
        this.mayorshipInfo = mayorshipInfo;
    }

    public void setCheckinInfo(CheckinInfo checkinInfo) {
        this.checkinInfo = checkinInfo;
    }

    public void setFriendInfo(FriendInfo friendInfo) {
        this.friendInfo = friendInfo;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public String getGender() {
		return gender;
	}

	public String getHomeCity() {
		return homeCity;
	}

	public String getRelationship() {
		return relationship;
	}

	public boolean hasPings() {
		return pings;
	}

	public ContactInfo getContact() {
		return contact;
	}
	
	public MayorshipInfo getMayorshipInfo() {
		return mayorshipInfo;
	}

	public CheckinInfo getCheckinInfo() {
		return checkinInfo;
	}

	public FriendInfo getFriendInfo() {
		return friendInfo;
	}

	public Scores getScores() {
		return scores;
	}

}
