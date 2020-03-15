package net.rebworks.avenyn.lunch.domain;

import java.util.function.Function;

@FunctionalInterface
public interface MessageFormatterFactory {
    Function<String, String> create();
}
