package net.rebworks.avenyn.lunch.domain;

public class Menu {
    private final Restaurant restaurant;
    private final String logoUrl;
    private final String[] items;

    public Menu(final Restaurant restaurant, final String logoUrl, final String[] items) {
        this.restaurant = restaurant;
        this.logoUrl = logoUrl;
        this.items = items;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String[] getItems() {
        return items;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
