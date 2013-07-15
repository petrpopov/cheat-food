package org.springframework.social.foursquare.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties({"likes", "listed"})
public class Venue {
	
	private String id;
	private String name;
	private ContactInfo contact;
	private Location location;
	private List<Category> categories;
	private boolean verified;
	private VenueStats stats;
	private String url;
    private String canonicalUrl;
	private HereNow hereNow;
	private VenueTips tips;
	private List<String> tags;
	private List<Special> specials;
	private List<Special> specialsNearby;
	private String shortUrl;
	private String timezone;
	private VenueBeenHere beenHere;
	private PhotoGroups photos;
	private String description;
    private boolean like;
    private boolean closed;

    public Venue() {
    }

    public Venue(String id, String name, ContactInfo contact, Location location, List<Category> categories,
			boolean verified, VenueStats stats) {
		this.id = id;
		this.name = name;
		this.contact = contact;
		this.location = location;
		this.categories = categories;
		this.verified = verified;
		this.stats = stats;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ContactInfo getContact() {
		return contact;
	}

	public void setContact(ContactInfo contact) {
		this.contact = contact;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public VenueStats getStats() {
		return stats;
	}

	public void setStats(VenueStats stats) {
		this.stats = stats;
	}

	public String getUrl() {
		return url;
	}

	public HereNow getHereNow() {
		return hereNow;
	}

	public VenueTips getTips() {
		return tips;
	}

	public List<String> getTags() {
		return tags;
	}

	public List<Special> getSpecials() {
		return specials;
	}

	public List<Special> getSpecialsNearby() {
		return specialsNearby;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public String getTimezone() {
		return timezone;
	}

	public VenueBeenHere getBeenHere() {
		return beenHere;
	}

	public PhotoGroups getPhotos() {
		return photos;
	}

	public String getDescription() {
		return description;
	}

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
        this.url = canonicalUrl;
    }

    public void setUrl(String url) {
        this.url = url;
        this.canonicalUrl = url;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
