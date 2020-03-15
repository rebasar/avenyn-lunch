package net.rebworks.avenyn.lunch.service.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.rebworks.avenyn.lunch.domain.Menu;
import net.rebworks.avenyn.lunch.domain.MessageFormatterFactory;
import net.rebworks.avenyn.lunch.domain.MessageSupplierFactory;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MenuFormatter {
    private static final int TWO_TO_THE_TWENTYFOUR = 1 << 24;
    private final ObjectMapper objectMapper;
    private final MessageFormatterFactory menuMessageFormatterFactory;
    private final MessageSupplierFactory dailyMessageSupplierFactory;

    public MenuFormatter(final ObjectMapper objectMapper, final MessageFormatterFactory menuMessageFormatterFactory, final MessageSupplierFactory dailyMessageSupplierFactory) {
        this.objectMapper = objectMapper;
        this.menuMessageFormatterFactory = menuMessageFormatterFactory;
        this.dailyMessageSupplierFactory = dailyMessageSupplierFactory;
    }

    public byte[] format(final List<Menu> menus) throws JsonProcessingException {
        ObjectNode message = createMessage(menus);
        return objectMapper.writeValueAsBytes(message);
    }

    private ObjectNode createMessage(final List<Menu> menus) {
        final Supplier<String> dailyMessageSupplier = dailyMessageSupplierFactory.create();
        final Function<String, String> menuMessageFormatter = menuMessageFormatterFactory.create();
        ObjectNode message = objectMapper.createObjectNode();
        ArrayNode attachments = objectMapper.createArrayNode();
        message.set("text", new TextNode(dailyMessageSupplier.get()));
        menus.stream().map(menu -> toAttachment(menu, menuMessageFormatter)).forEach(attachments::add);
        message.set("attachments", attachments);
        return message;
    }

    private ObjectNode toAttachment(Menu menu, final Function<String, String> menuMessageFormatter) {
        ObjectNode attachment = objectMapper.createObjectNode();
        final String restaurant = menu.getRestaurant().getName();
        attachment.set("pretext", new TextNode(menuMessageFormatter.apply(restaurant)));
        attachment.set("author_name", new TextNode(restaurant));
        attachment.set("author_link", new TextNode(menu.getRestaurant().getUrl()));
        attachment.set("fallback", new TextNode(String.format("Menu for %s", restaurant)));
        attachment.set("color", new TextNode(colorize(restaurant)));
        attachment.set("text", new TextNode(Stream.of(menu.getItems()).map(s -> "- " + s).collect(Collectors.joining("\n"))));
        attachment.set("thumb_url", new TextNode(menu.getLogoUrl()));
        return attachment;
    }

    private String colorize(final String name) {
        final int hash = name.hashCode()%TWO_TO_THE_TWENTYFOUR;
        return String.format("#%X", hash).substring(0, 7);
    }
}
