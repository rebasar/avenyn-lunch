package net.rebworks.avenyn.lunch.domain;

public class Restaurant {

    private final String identifier;
    private final String name;
    private final String url;
    private final MenuModifier modifier;

    public Restaurant(final String identifier, final String name, final String url, final MenuModifier modifier) {
        this.identifier = identifier;
        this.name = name;
        this.url = url;
        this.modifier = modifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public MenuModifier getModifier() {
        return modifier;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
