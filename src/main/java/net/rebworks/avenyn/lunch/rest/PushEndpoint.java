package net.rebworks.avenyn.lunch.rest;

import net.rebworks.avenyn.lunch.domain.Menu;
import net.rebworks.avenyn.lunch.domain.Restaurant;
import net.rebworks.avenyn.lunch.service.MenuFetcher;
import net.rebworks.avenyn.lunch.service.RestaurantRegistry;
import net.rebworks.avenyn.lunch.service.SlackPushService;
import net.rebworks.avenyn.lunch.service.SlackPushService.PushResult;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/push")
@Produces(MediaType.APPLICATION_JSON + "; charset=utf8")
public class PushEndpoint {

    private final RestaurantRegistry restaurantRegistry;
    private final MenuFetcher fetcher;
    private final SlackPushService slackPushService;

    public PushEndpoint(final RestaurantRegistry restaurantRegistry, final MenuFetcher fetcher, final SlackPushService slackPushService) {
        this.restaurantRegistry = restaurantRegistry;
        this.fetcher = fetcher;
        this.slackPushService = slackPushService;
    }

    @POST
    public PushResult push(List<String> keywords) throws IOException {
        List<Restaurant> restaurants = keywords.stream().map(restaurantRegistry::find).collect(toList());
        final List<Menu> menus = fetcher.fetch(restaurants);
        return slackPushService.push(menus);
    }
}
