package net.rebworks.avenyn.lunch;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.rebworks.avenyn.lunch.domain.modifiers.BarriqueModifier;
import net.rebworks.avenyn.lunch.domain.modifiers.OliviaModifier;
import net.rebworks.avenyn.lunch.domain.modifiers.TullenModifier;
import net.rebworks.avenyn.lunch.health.ApplicationHealthCheck;
import net.rebworks.avenyn.lunch.rest.MenuEndpoint;
import net.rebworks.avenyn.lunch.rest.MessagePreviewEndpoint;
import net.rebworks.avenyn.lunch.rest.PushEndpoint;
import net.rebworks.avenyn.lunch.rest.RestaurantsEndpoint;
import net.rebworks.avenyn.lunch.service.MenuFetcher;
import net.rebworks.avenyn.lunch.service.ModifierRegistry;
import net.rebworks.avenyn.lunch.service.RestaurantRegistry;
import net.rebworks.avenyn.lunch.service.SlackPushService;
import net.rebworks.avenyn.lunch.service.slack.MenuFormatter;

public class AvenynLunchApplication extends Application<AvenynLunchConfiguration> {

    @Override
    public void initialize(final Bootstrap<AvenynLunchConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                                               new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    public static void main(String[] args) throws Exception {
        new AvenynLunchApplication().run(args);
    }

    @Override
    public void run(final AvenynLunchConfiguration avenynLunchConfiguration, final Environment environment) {
        final ModifierRegistry modifierRegistry = new ModifierRegistry();
        initializeModifiers(modifierRegistry);
        final RestaurantRegistry restaurantRegistry = new RestaurantRegistry(modifierRegistry);
        initializeRestaurants(restaurantRegistry, avenynLunchConfiguration);
        final MenuFormatter menuFormatter = new MenuFormatter(environment.getObjectMapper(),
                                                              avenynLunchConfiguration.getMessageFormatterFactory(),
                                                              avenynLunchConfiguration.getDailyMessageSupplierFactory());
        final SlackPushService slackPushService = new SlackPushService(avenynLunchConfiguration.getSlackUrl(),
                                                                       menuFormatter);
        final MenuFetcher fetcher = new MenuFetcher(avenynLunchConfiguration.getLunchUrl());
        final RestaurantsEndpoint restaurantsEndpoint = new RestaurantsEndpoint(restaurantRegistry);
        final MenuEndpoint menuEndpoint = new MenuEndpoint(restaurantRegistry, fetcher);
        final PushEndpoint pushEndpoint = new PushEndpoint(restaurantRegistry, fetcher, slackPushService);
        final MessagePreviewEndpoint previewEndpoint = new MessagePreviewEndpoint(environment.getObjectMapper(),
                                                                                  restaurantRegistry,
                                                                                  fetcher,
                                                                                  menuFormatter);
        environment.jersey().register(restaurantsEndpoint);
        environment.jersey().register(menuEndpoint);
        environment.jersey().register(pushEndpoint);
        environment.jersey().register(previewEndpoint);
        final ApplicationHealthCheck applicationHealthCheck = new ApplicationHealthCheck();
        environment.healthChecks().register("application", applicationHealthCheck);
    }

    private void initializeRestaurants(final RestaurantRegistry restaurantRegistry, final AvenynLunchConfiguration avenynLunchConfiguration) {
        avenynLunchConfiguration.getRestaurants().forEach((k, v) -> restaurantRegistry.create(k, v.getName(), v.getUrl()));
    }

    private void initializeModifiers(final ModifierRegistry modifierRegistry) {
        modifierRegistry.register("barrique", new BarriqueModifier());
        modifierRegistry.register("olivia-avenyn", new OliviaModifier());
        modifierRegistry.register("olstugan-tullen", new TullenModifier());
    }
}
