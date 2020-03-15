package net.rebworks.avenyn.lunch.service;

import net.rebworks.avenyn.lunch.domain.Restaurant;
import org.apache.commons.text.similarity.FuzzyScore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RestaurantRegistry {
    private final List<Restaurant> restaurants = new ArrayList<>();
    private final FuzzyScore fuzzyScore = new FuzzyScore(Locale.getDefault());
    private final ModifierRegistry modifierRegistry;

    public RestaurantRegistry(final ModifierRegistry modifierRegistry) {
        this.modifierRegistry = modifierRegistry;
    }

    public Restaurant find(String keyword) {
        final IdentifierComparator comparator = new IdentifierComparator(keyword);
        return restaurants.stream().max(comparator).orElseGet(() -> create(keyword, keyword, ""));
    }

    public Restaurant create(String identifier, String name, final String url) {
        final Restaurant restaurant = new Restaurant(identifier, name, url, modifierRegistry.getByIdentifier(identifier));
        register(restaurant);
        return restaurant;
    }

    public void register(Restaurant restaurant) {
        restaurants.removeIf(r -> r.getIdentifier().equals(restaurant.getIdentifier()));
        restaurants.add(restaurant);
    }

    private class IdentifierComparator implements Comparator<Restaurant> {
        private final String keyword;

        public IdentifierComparator(final String keyword) {
            this.keyword = keyword;
        }

        @Override
        public int compare(final Restaurant r1, final Restaurant r2) {
            final Integer r1Score = fuzzyScore.fuzzyScore(r1.getIdentifier(), keyword);
            final Integer r2Score = fuzzyScore.fuzzyScore(r2.getIdentifier(), keyword);
            return r1Score.compareTo(r2Score);
        }
    }
}
