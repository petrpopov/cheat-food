package org.springframework.social.foursquare.api;

import java.util.List;

public class Category {
	
	private String id;
    private String name;
	private String shortName;
	private String pluralName;
	private String iconUrl;
	private List<String> parents;
	private List<Category> children;
	private boolean primary;
    private Icon icon;

    public Category() {
    }

    public Category(String id, String shortName, String name, String pluralName, String iconUrl, boolean primary) {
		this.id = id;
		this.shortName = shortName;
		this.pluralName = pluralName;
		this.iconUrl = iconUrl;
		this.primary = primary;
        this.name = name;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
		return id;
	}

	public String getShortName() {
		return shortName;
	}

	public String getPluralName() {
		return pluralName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public List<String> getParents() {
		return parents;
	}

	public List<Category> getChildren() {
		return children;
	}
	
	public boolean isPrimary() {
		return primary;
	}

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
