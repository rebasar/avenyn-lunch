package net.rebworks.avenyn.lunch.domain.modifiers;

import net.rebworks.avenyn.lunch.domain.MenuModifier;

import java.util.stream.Stream;

public class OliviaModifier implements MenuModifier {
    @Override
    public String[] apply(final String[] menu) {
        return Stream.of(menu)
                     .filter(s -> !s.startsWith("Dagens"))
                     .toArray(String[]::new);
    }
}
