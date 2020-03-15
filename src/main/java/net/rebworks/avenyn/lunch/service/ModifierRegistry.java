package net.rebworks.avenyn.lunch.service;

import net.rebworks.avenyn.lunch.domain.MenuModifier;

import java.util.HashMap;
import java.util.Map;

public class ModifierRegistry {

    private static final MenuModifier DEFAULT_MODIFIER = s -> s;
    private final Map<String, MenuModifier> modifiers = new HashMap<>();

    public MenuModifier getByIdentifier(String identifier) {
        return modifiers.getOrDefault(identifier, DEFAULT_MODIFIER);
    }

    public void register(String identifier, MenuModifier modifier) {
        modifiers.put(identifier, modifier);
    }
}
