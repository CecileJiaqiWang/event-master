package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI {
	// Common part in the full URL.
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	// Default keyword is an empty string.
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "nDAkjiDHQYtoNS1ytB07n22ZuaAvQQwt";
	
	// Constant values.
	
	private static final String ID = "id";
	private static final String RATING = "rating";
	private static final String DISTANCE = "distance";
	private static final String EMBEDDED = "_embedded";
	private static final String EVENTS = "events";
	private static final String VENUES = "venues";
	private static final String ADDRESS = "address";
	private static final String LINE1 = "line1";
	private static final String LINE2 = "line2";
	private static final String LINE3 = "line3";
	private static final String CITY = "city";
	private static final String IMAGES = "images";
	private static final String URL_STR = "url";
	private static final String CLASSIFICATIONS = "classifications";
	private static final String SEGMENT = "segment";
	private static final String NAME = "name";
	
	/**
	 * 
	 * @param event JSONObject
	 * @return String : the first nonempty address of the event.
	 * @throws JSONException
	 */
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull(EMBEDDED)) {
			JSONObject embedded = event.getJSONObject(EMBEDDED);	
			if (!embedded.isNull(VENUES)) {
				JSONArray venues = embedded.getJSONArray(VENUES);	
				for (int i = 0; i < venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder sb = new StringBuilder();
					if (!venue.isNull(ADDRESS)) {
						JSONObject address = venue.getJSONObject(ADDRESS);
						if (!address.isNull(LINE1)) {
							sb.append(address.getString(LINE1));
						}
						if (!address.isNull(LINE2)) {
							sb.append('\n');
							sb.append(address.getString(LINE2));
						}
						if (!address.isNull(LINE3)) {
							sb.append('\n');
							sb.append(address.getString(LINE3));
						}
					}
					
					if (!venue.isNull(CITY)) {
						JSONObject city = venue.getJSONObject(CITY);
						
						if (!city.isNull(NAME)) {
							sb.append('\n');
							sb.append(city.getString(NAME));
						}
					}
					
					String addr = sb.toString();
					if (!addr.equals("")) {
						// Return the first nonempty address;
						return addr;
					}
				}
			}
		}
		return "";

	}
	
	/**
	 * 
	 * @param event JSONObject
	 * @return String : the first nonempty image url.
	 * @throws JSONException
	 */
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull(IMAGES)) {
			JSONArray images = event.getJSONArray(IMAGES);
			for (int i = 0; i < images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				if (!image.isNull(URL_STR)) {
					String res = image.getString(URL_STR);
					if (!res.isEmpty()) {
						return res;
					}
				}
			}
		}
		return "";
	}
	
	/**
	 * 
	 * @param event JSONObject
	 * @return Set<String>: a set of categories matching the event.
	 * @throws JSONException
	 */
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull(CLASSIFICATIONS)) {
			JSONArray classifications = event.getJSONArray(CLASSIFICATIONS);
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull(SEGMENT)) {
					JSONObject segment = classification.getJSONObject(SEGMENT);
					if (!segment.isNull(NAME)) {
						String name = segment.getString(NAME);
						if (!name.isEmpty()) {
							categories.add(name);
						}
					
					}
				}
			}
		}
		return categories;
	}
	
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			String itemId = event.isNull(ID) ? "" : event.getString(ID);
			String name = event.isNull(NAME) ? "" : event.getString(NAME);
			String url = event.isNull(URL_STR) ? "" : event.getString(URL_STR);
			String imgUrl = getImageUrl(event);
			Double rating = event.isNull(RATING) ? 0 : event.getDouble(RATING);
			Double distance = event.isNull(DISTANCE) ? 0 : event.getDouble(DISTANCE);
			String address = getAddress(event);
			Set<String> categories = getCategories(event);

			Item item = builder.categories(categories).distance(distance).imgUrl(imgUrl).itemId(itemId)
			.name(name).rating(rating).url(url).address(address).build();
			itemList.add(item);
		}
		return itemList;
	}

	
	/**
	 * 
	 * @param lat The latitude of the location.
	 * @param lon longitude The longitude of the location.
	 * @param keyword Optional search keyword.
	 * @return JSONArray format events.
	 */
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		} 
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		// Construct URL query.
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s",
				API_KEY, geoHash, keyword, 50);
		try {
			// Open an HTTP connection.
			HttpURLConnection connection = (HttpURLConnection)new URL(URL + "?" + query).openConnection();
			connection.setRequestMethod("GET");
			// Get response status code.
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + URL + "?" + query);
			System.out.println("Response Code : " + responseCode);
			// Read response.
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String input;
			StringBuilder response = new StringBuilder();
			while ((input = in.readLine()) != null) {
				response.append(input);
			}
			in.close();
			// Return the embedded events.
			try {
				JSONObject responseObj = new JSONObject(response.toString());
				if (responseObj.isNull("_embedded")) {
					return new ArrayList<>();
				} else {
					JSONArray events = responseObj.getJSONObject("_embedded").getJSONArray("events");
					return getItemList(events);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	private void printSearchResults(double lat, double lon) {
		List<Item> events = search(lat, lon, null);
		try {
			for (Item item : events) {
				JSONObject obj = item.toJSONObject();
				System.out.println(obj);
			}
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		tmApi.printSearchResults(37.38, -122.08);
		// London, UK
		tmApi.printSearchResults(51.503364, -0.12);
		// Houston, TX
		tmApi.printSearchResults(29.682684, -95.295410);
	}
}
