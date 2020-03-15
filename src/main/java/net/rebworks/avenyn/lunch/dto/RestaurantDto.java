package net.rebworks.avenyn.lunch.dto;

public class RestaurantDto {

    private final String name;
    private final String identifier;

    public RestaurantDto(final String name, final String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }
}
