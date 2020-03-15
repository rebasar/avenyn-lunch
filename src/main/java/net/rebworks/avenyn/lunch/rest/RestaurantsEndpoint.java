package net.rebworks.avenyn.lunch.rest;

import net.rebworks.avenyn.lunch.domain.Restaurant;
import net.rebworks.avenyn.lunch.dto.RestaurantDto;
import net.rebworks.avenyn.lunch.service.RestaurantRegistry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/restaurants")
@Produces(MediaType.APPLICATION_JSON + "; charset=utf8")
public class RestaurantsEndpoint {

    private final RestaurantRegistry restaurantRegistry;

    public RestaurantsEndpoint(final RestaurantRegistry restaurantRegistry) {
        this.restaurantRegistry = restaurantRegistry;
    }

    @GET
    @Path("/{keyword}")
    public RestaurantDto getRestaurant(@PathParam("keyword") String keyword) {
        Restaurant restaurant = restaurantRegistry.find(keyword);
        return new RestaurantDto(restaurant.getName(), restaurant.getIdentifier());
    }

}
