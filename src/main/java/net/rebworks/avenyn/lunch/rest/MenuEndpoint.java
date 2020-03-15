package net.rebworks.avenyn.lunch.rest;

import net.rebworks.avenyn.lunch.domain.Menu;
import net.rebworks.avenyn.lunch.domain.Restaurant;
import net.rebworks.avenyn.lunch.dto.MenuDto;
import net.rebworks.avenyn.lunch.service.MenuFetcher;
import net.rebworks.avenyn.lunch.service.RestaurantRegistry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/menus")
@Produces(MediaType.APPLICATION_JSON + "; charset=utf8")
public class MenuEndpoint {

    private final RestaurantRegistry restaurantRegistry;
    private final MenuFetcher fetcher;

    public MenuEndpoint(final RestaurantRegistry restaurantRegistry, final MenuFetcher fetcher) {
        this.restaurantRegistry = restaurantRegistry;
        this.fetcher = fetcher;
    }

    @GET
    public List<MenuDto> getMenus(@QueryParam("q") List<String> restaurants) throws IOException {
        final List<Restaurant> restaurantList = restaurants.stream().map(restaurantRegistry::find).collect(toList());
        final List<Menu> menus = fetcher.fetch(restaurantList);
        return menus.stream().map(m -> new MenuDto(m.getRestaurant().getName(), m.getItems())).collect(toList());
    }

}
