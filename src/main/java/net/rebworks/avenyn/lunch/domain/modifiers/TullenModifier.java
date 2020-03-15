package net.rebworks.avenyn.lunch.domain.modifiers;

import net.rebworks.avenyn.lunch.domain.MenuModifier;

import java.util.stream.Stream;

public class TullenModifier implements MenuModifier {

    private static final String[] REPLACEMENT_MENU = {"It seems like Tullen did not put their menu on the site",
            "Please help this bot by getting the menu from the link below",
            "And sharing with all of us",
            "https://www.facebook.com/wardshusettullen/"};
    private static final String INDICATOR = "Vänligen klicka på länken nedan för att se aktuell meny";

    @Override
    public String[] apply(final String[] items) {
        if(Stream.of(items).anyMatch(s -> s.contains(INDICATOR))){
            return REPLACEMENT_MENU;
        }
        return items;
    }
}
