package org.springframework.social.foursquare.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@JsonIgnoreProperties({"likes", "posts", "entities"})
public class Checkin {

    @Id
    private String mongoid;

	private String id;
	private FoursquareUser user;
	private Date createdAt;
	private String type;
	private String shout;
	private boolean isMayor;
	private String timeZone;
    private String timeZoneOffset;
	private Venue venue;
	private Photos photos;
	private CheckinSource source;
	private CheckinCommentInfo comments;
    private boolean like;
    private String username;

    public Checkin() {
    }

    public Checkin(String id, FoursquareUser user, Date createdAt, String type, String timeZone,  String timeZoneOffset,
			Venue venue, CheckinSource source, CheckinCommentInfo comments) {
		this.id = id;
		this.user = user;
		this.createdAt = createdAt;
		this.type = type;
		this.timeZone = timeZone;
        this.timeZoneOffset = timeZoneOffset;
		this.venue = venue;
		this.source = source;
		this.comments = comments;
	}

    public String getMongoid() {
        return mongoid;
    }

    public void setMongoid(String mongoid) {
        this.mongoid = mongoid;
    }

    public String getId() {
		return id;
	}
	
	public FoursquareUser getUser() {
		return user;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getType() {
		return type;
	}

    public boolean getisMayor() {
        return isMayor;
    }

    public void setisMayor(boolean mayor) {
        isMayor = mayor;
    }

    public String getTimeZone() {
		return timeZone;
	}

    public String getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(String timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public Venue getVenue() {
		return venue;
	}

	public String getShout() {
		return shout;
	}

	public Photos getPhotos() {
		return photos;
	}
	
	public CheckinSource getSource() {
		return source;
	}
	
	public CheckinCommentInfo getComments() {
		return comments;
	}

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
