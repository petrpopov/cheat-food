package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.social.foursquare.api.Category;

import java.util.List;

@JsonDeserialize(using=CategoriesContainerDeserializer.class)
public class CategoriesContainer {
	
	private List<Category> categories;
	
	public CategoriesContainer(List<Category> categories) {
		this.categories = categories;
	}

	public List<Category> getCategories() {
		return categories;
	}
}
