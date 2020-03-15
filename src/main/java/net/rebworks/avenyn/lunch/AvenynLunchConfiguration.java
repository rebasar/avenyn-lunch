package net.rebworks.avenyn.lunch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import net.rebworks.avenyn.lunch.domain.MessageFormatterFactory;
import net.rebworks.avenyn.lunch.domain.MessageSupplierFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AvenynLunchConfiguration extends Configuration {

    @JsonIgnore
    private final Random random = new Random();
    private String lunchUrl;
    private String slackUrl;
    private Map<String, RestaurantConfiguration> restaurants = new HashMap<>();
    private Messages messages;

    @JsonIgnore
    public MessageFormatterFactory getMessageFormatterFactory() {
        return () -> {
            final LinkedList<String> messages = new LinkedList<>(this.messages.getRestaurant());
            Collections.shuffle(messages, random);
            return (restaurant) -> {
                if (messages.isEmpty()) {
                    return String.format(this.messages.getDefaultMessage(), restaurant);
                }
                return String.format(messages.pop(), restaurant);
            };
        };
    }

    @JsonIgnore
    public MessageSupplierFactory getDailyMessageSupplierFactory() {
        return () -> {
            return () -> messages.getDaily().get(random.nextInt(messages.getDaily().size()));
        };
    }

    public String getSlackUrl() {
        return slackUrl;
    }

    public void setSlackUrl(final String slackUrl) {
        this.slackUrl = slackUrl;
    }

    public String getLunchUrl() {
        return lunchUrl;
    }

    public void setLunchUrl(final String lunchUrl) {
        this.lunchUrl = lunchUrl;
    }

    public Map<String, RestaurantConfiguration> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(final Map<String, RestaurantConfiguration> restaurants) {
        this.restaurants = restaurants;
    }

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(final Messages messages) {
        this.messages = messages;
    }

    static class Messages {

        @JsonProperty("default")
        private String defaultMessage = "I have nothing to say about *%s*";
        private List<String> daily;
        private List<String> restaurant;

        public String getDefaultMessage() {
            return defaultMessage;
        }

        public void setDefaultMessage(final String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public List<String> getDaily() {
            return daily;
        }

        public void setDaily(final List<String> daily) {
            this.daily = daily;
        }

        public List<String> getRestaurant() {
            return restaurant;
        }

        public void setRestaurant(final List<String> restaurant) {
            this.restaurant = restaurant;
        }
    }

    static class RestaurantConfiguration {
        private String name;
        private String url;


        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }
    }
}
