package rest.recommend;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.ListItems;
import com.recombee.api_client.bindings.Item;
import com.recombee.api_client.exceptions.ApiException;


public class RecommendItems {
	/*	
	 * 	always recommend 5 items
	 *  (recommend to new users with no ratings and no preference random items)
	 *  recommend to new user with no ratings (3 preference at least) -> item based recommendation
	 *  recommend to existing users with rating -> user based recommendation
	 *  
	 *  recommend activity -> filter="activity" in 'type'
	 *  recommend restaurant -> filter="restaurant" in 'type'
	 *  
	 *  recommend item based on location
	 *  (recommend item based on topic)
	 */
	
	public static WebTarget configBase() {
    	ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURIStorage());
        System.out.println("Calling " + getBaseURIStorage() ); 
        return service;
    }
	public static WebTarget configSearch() {
    	ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURISearch());
        System.out.println("Calling " + getBaseURISearch() ); 
        return service;
    }
	
	public static WebTarget config() {
    	ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURIRec());
        System.out.println("Calling " + getBaseURIRec() ); 
        return service;
    }
	
	 public static String format(String jsonString) throws IOException {
		 ObjectMapper mapper = new ObjectMapper();
		 Object json = mapper.readValue(jsonString, Object.class);
		 String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

	     return prettyJson;
	  }
	 private static URI getBaseURISearch() {
		 return UriBuilder.fromUri(
				 "https://sde-item-search-ws.herokuapp.com/").build();
	 }
	 private static URI getBaseURIStorage() {
		 return UriBuilder.fromUri(
				 "https://sde-storage-ws.herokuapp.com/").build();
	 }
	 private static URI getBaseURIRec() {
		 return UriBuilder.fromUri(
				 "https://sde-recombee-adapter-ws.herokuapp.com/").build();
	 }
	 
	
	public static String prepareItemId(String userId, String type) {
		WebTarget service = configBase();
		//get preferences of user
		Response respPref = service.path("/rdb/preferences/by_user").queryParam("userId", userId).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
		String responsePref = respPref.readEntity(String.class);
		JSONArray aRespPref = new JSONArray(responsePref);
		int numberOfPrefs = aRespPref.length();
		
		//get all itemIds
		Response respItem = service.path("/rdb/items/all").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
		String responseItem = respItem.readEntity(String.class);
		JSONArray aRespItem = new JSONArray(responseItem);
		int numberOfItems = aRespItem.length();
		HashMap<String,String> itemIds = new HashMap<>();
		for (int i=0;i<numberOfItems;i++) {
			itemIds.put(aRespItem.getJSONObject(i).getString("itemId"), (aRespItem.getJSONObject(i).getString("type")));
		}

		//select only preferred items with matching type
		List<String> prefItemIds = new ArrayList<>();
		for (int i=0;i<numberOfPrefs;i++) {
			String tempId = aRespPref.getJSONObject(i).getString("itemId");
			
			if (itemIds.get(tempId).compareTo(type)==0) {
				prefItemIds.add(tempId);
			}
		}
		String itemId;
		if (prefItemIds.size()==0) {
			itemId = "null";
		}else {
			itemId = prefItemIds.get(0);
		}
		return itemId;
		
	}
	
	 /*
	  * Use it for users that doesn't have ratings for given type of item yet. Item-based recommendation
	  */
	public static String recToUserWithNoRatings(String userId, String type, String city) throws IOException {
		WebTarget service = config();
		String itemId = prepareItemId(userId,type);
		//return random items if no preference found
		if (itemId.equals("null")) {
			System.out.println("randomFive");
			return randomFiveItems(type, city);
		}
		System.out.println("itemId for rec: " + itemId);
		Response resp =null;
		if (type.equals("activity")) {
			System.out.println("activity");
			resp = service.path("/recombee/recommendation/item_based")
					.queryParam("itemId", itemId)
					.queryParam("count", "5")
					.queryParam("userId", userId)
					.queryParam("userImpact", "0.4")
					.queryParam("filter", "\""+type+"\""+ " in 'type' and " + "\""+city+"\"" + " in 'city'")
					.queryParam("properties", "name,topic,city,from,to")
					.request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
		}else {
			System.out.println("restaurant");
			resp = service.path("/recombee/recommendation/item_based")
					.queryParam("itemId", itemId)
					.queryParam("count", "5")
					.queryParam("userId", userId)
					.queryParam("userImpact", "0.4")
					.queryParam("filter", "\""+type+"\""+ " in 'type' and " + "\""+city+"\"" + " in 'city'")
					.queryParam("properties", "name,topic,city,address,rating")
					.request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
		}
		//get recommendation

		String response = resp.readEntity(String.class);
		JSONArray newArray = new JSONArray();
		JSONArray array = new JSONArray(response);
		for (int i=0;i<array.length();i++) {
			newArray.put(array.getJSONObject(i).get("values"));
		}
		//String json = format(newA);
		return newArray.toString();			
	
	}

	 private static String randomFiveItems(String type, String city) throws IOException {
		 WebTarget service = configSearch();
		 Response resp = service.path("search")
					.queryParam("keyword", type + " " + city)
					.queryParam("count", "5")
					.request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
			String response = resp.readEntity(String.class);
			String json = format(response);
			return json;
	}

	/*
	 * For users that already have rating. User based recommendation.
	 */
	public static String recToUserWithRatings(String userId, String type, String city) throws IOException {
		WebTarget service = config();
		
		//get recommendation
		Response resp =null;
		if (type.equals("activity")) {
			System.out.println("activity");
		  resp = service.path("/recombee/recommendation/user_based")
				.queryParam("userId", userId)
				.queryParam("count", "5")
				.queryParam("filter", "\""+type+"\""+ " in 'type' and " + "\""+city+"\"" + " in 'city'")
				.queryParam("properties", "name,topic,city,from,to")
				.request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
		} else {
			System.out.println("restaurant");
			resp = service.path("/recombee/recommendation/user_based")
					.queryParam("userId", userId)
					.queryParam("count", "5")
					.queryParam("filter", "\""+type+"\""+ " in 'type' and " + "\""+city+"\"" + " in 'city'")
					.queryParam("properties", "name,topic,city,address,rating")
					.request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
		}
				
		String response = resp.readEntity(String.class);
		JSONArray newArray = new JSONArray();
		JSONArray array = new JSONArray(response);
		for (int i=0;i<array.length();i++) {
			newArray.put(array.getJSONObject(i).get("values"));
		}
		//String json = format(newA);
		return newArray.toString();		
	}
	
	public static boolean checkIfUserHasRatings(String userId) {
		WebTarget service = configBase();
		Response resp = service.path("/rdb/ratings/by_user").queryParam("userId", userId).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
		String response = resp.readEntity(String.class);
		JSONArray aResp = new JSONArray(response);
		int numberOfRatings = aResp.length();
		return numberOfRatings>0;
		
	}
	
	
	public static String recommendToUser(String userId, String type, String city) throws IOException {
		if (checkIfUserHasRatings(userId)) {
			System.out.println("user based");
			return recToUserWithRatings(userId, type, city);
			
		}else {
			System.out.println("item based");
			return recToUserWithNoRatings(userId, type, city);
		}
			
	}
	
}
