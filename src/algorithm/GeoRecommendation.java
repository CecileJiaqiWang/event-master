package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems (String userId, double lat, double lon) {
		List<Item> recommendedItems = new ArrayList<>();
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			Set<String> favoriteItemIds = conn.getFavoriteItemIds(userId);
			
			// Use hashmap to count the frequency of the categories of events a user favored.
			Map<String, Integer> categoriesFreq = new HashMap<>();
			for (String itemId : favoriteItemIds) {
				for (String category : conn.getCategories(itemId)) {
					categoriesFreq.put(category, categoriesFreq.getOrDefault(category, 0) + 1);
				}
			}
			
			// Sort categories based on frequency.
			List<Entry<String, Integer>> sortedCategories = new ArrayList<>(categoriesFreq.entrySet());
			Collections.sort(sortedCategories, new Comparator<Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> e0, Entry<String, Integer> e1) {
					return e0.getValue().compareTo(e1.getValue());
				}
				
			});
			
			Set<Item> visited = new HashSet<>();
			
			for (Entry<String, Integer> category : sortedCategories) {
				List<Item> items = conn.searchItems(lat, lon, category.getKey());
				List<Item> filteredItems = new ArrayList<>();
				
				for (Item item : items) {
					if (!favoriteItemIds.contains(item.getItemId()) && !visited.contains(item)) {
						filteredItems.add(item);
					} 
				}
				
				// Sort events by distance.
				Collections.sort(filteredItems, new Comparator<Item>() {
					@Override
					public int compare(Item item0, Item item1) {
						return Double.compare(item0.getDistance(), item1.getDistance());
					}
				});
				
				visited.addAll(items);
				
				recommendedItems.addAll(filteredItems);
			}
		} finally {
			conn.close();
		}
		return recommendedItems;
	}
}
