package net.rebworks.avenyn.lunch.domain.modifiers;

import net.rebworks.avenyn.lunch.domain.MenuModifier;

import java.util.stream.Stream;

public class BarriqueModifier implements MenuModifier {

    private static final String[] NO_MONDAY_MENU = new String[]{"Barrique only serves lunch Tue-Fri"};

    @Override
    public String[] apply(final String[] menu) {
        if (isMonday(menu)) {
            return NO_MONDAY_MENU;
        }
        return menu;
    }

    private boolean isMonday(final String[] menu) {
        return Stream.of(menu).anyMatch(s -> s.contains("tis-ons") || s.contains("tor-fre"));
    }
}
