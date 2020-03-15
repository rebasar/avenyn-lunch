package net.rebworks.avenyn.lunch.dto;

public class MenuDto {
    private final String name;
    private final String[] items;

    public MenuDto(final String name, final String[] items) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public String[] getItems() {
        return items;
    }
}
