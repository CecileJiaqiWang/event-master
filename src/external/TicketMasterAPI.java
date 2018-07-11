package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TicketMasterAPI {
	// Common part in the full URL.
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	// Default keyword is an empty string.
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "nDAkjiDHQYtoNS1ytB07n22ZuaAvQQwt";
	
	/**
	 * 
	 * @param lat The latitude of the location.
	 * @param lon longitude The longitude of the location.
	 * @param keyword Optional search keyword.
	 * @return JSONArray format events.
	 */
	public JSONArray search(double lat, double lon, String keyword) {
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
					return new JSONArray();
				} else {
					return responseObj.getJSONObject("_embedded").getJSONArray("events");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	private void printSearchResults(double lat, double lon) {
		JSONArray events = search(lat, lon, null);
		try {
			for (int i = 0; i < events.length(); i++) {
				JSONObject event = events.getJSONObject(i);
				System.out.println(event);
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
