package net.rebworks.avenyn.lunch.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.rebworks.avenyn.lunch.domain.Menu;
import net.rebworks.avenyn.lunch.domain.Restaurant;
import net.rebworks.avenyn.lunch.service.MenuFetcher;
import net.rebworks.avenyn.lunch.service.RestaurantRegistry;
import net.rebworks.avenyn.lunch.service.slack.MenuFormatter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/preview")
@Produces(MediaType.APPLICATION_JSON + "; charset=utf8")
public class MessagePreviewEndpoint {

    private final ObjectMapper objectMapper;
    private final RestaurantRegistry restaurantRegistry;
    private final MenuFetcher fetcher;
    private final MenuFormatter formatter;

    public MessagePreviewEndpoint(final ObjectMapper objectMapper, final RestaurantRegistry restaurantRegistry, final MenuFetcher fetcher, final MenuFormatter formatter) {
        this.objectMapper = objectMapper;
        this.restaurantRegistry = restaurantRegistry;
        this.fetcher = fetcher;
        this.formatter = formatter;
    }

    @GET
    public JsonNode preview(@QueryParam("q")List<String> keywords) throws IOException {
        List<Restaurant> restaurants = keywords.stream().map(restaurantRegistry::find).collect(toList());
        final List<Menu> menus = fetcher.fetch(restaurants);
        final byte[] content = formatter.format(menus);
        return objectMapper.readTree(content);
    }

}
