package rest.resources;

import java.io.IOException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recombee.api_client.bindings.Item;
import com.recombee.api_client.exceptions.ApiException;

@Stateless
@LocalBean
@Path("/recommend")
public class RecommendationResource {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	/*	
	 * 	always recommend 5 items
	 *  recommend to new users with no ratings and no preference the top rated items -> TODO: implement listActivitiesWithCount and listRestaurantsWithCount
	 *  recommend to new user with no ratings (3 preference at least) -> item based recommendation
	 *  recommend to existing users with rating -> user based recommendation
	 *  recommend activity -> filter="activity" in 'type'
	 *  recommend restaurant -> filter="restaurant" in 'type'
	 *  recommend item based on location
	 *  recommend item based on topic
	 */
	
	/*
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String search(@DefaultValue("")@QueryParam("keyword") String keyword) throws ApiException, IOException {
		System.out.println("--> RecommendationResource request...");
		System.out.println("--> URI = "+uriInfo);
		System.out.println("--> request = "+request);
		String items = SearchItems.search(keyword);
		//Entity entity = Entity.json(items);
		return items;
	}
	*/
	
}
