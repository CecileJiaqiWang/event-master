package entity;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Item {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}

	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imgUrl;
	private String url;
	private double distance;
	
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imgUrl = builder.imgUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}
	/**
	 * 
	 * Item builder
	 *
	 */
	public static class ItemBuilder {
		// Default values.
		private String itemId = "";
		private String name = "";
		private double rating = 0;
		private String address = "";
		private Set<String> categories = new HashSet<>();
		private String imgUrl = "";
		private String url = "";
		private double distance = 0;
		
		public ItemBuilder itemId(String itemId) {
			this.itemId = itemId;
			return this;
		}
		
		public ItemBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		public ItemBuilder rating(double rating) {
			this.rating = rating;
			return this;
		}
		
		public ItemBuilder address(String address) {
			this.address = address;
			return this;
		}
		
		public ItemBuilder categories(Set<String> categories) {
			this.categories = categories;
			return this;
		}
		
		public ItemBuilder imgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
			return this;
		}
		
		public ItemBuilder url(String url) {
			this.url = url;
			return this;
		}
		
		public ItemBuilder distance(Double distance) {
			this.distance = distance;
			return this;
		}
		
		public Item build() {
			return new Item(this);
		}
	}
	
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImgURL() {
		return imgUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imgUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
