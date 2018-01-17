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

import rest.recommend.RecommendItems;

@Stateless
@LocalBean
@Path("/recommend")
public class RecommendationResource {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String recommend(@QueryParam("userId") String userId, @QueryParam("type") String type, @QueryParam("city") String city) throws ApiException, IOException {
		System.out.println("--> RecommendationResource request...");
		System.out.println("--> URI = "+uriInfo);
		System.out.println("--> request = "+request);
		String items = RecommendItems.recommendToUser(userId, type, city);
		return items;
	}

	
}
