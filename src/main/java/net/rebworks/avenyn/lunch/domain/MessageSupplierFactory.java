package net.rebworks.avenyn.lunch.domain;

import java.util.function.Supplier;

@FunctionalInterface
public interface MessageSupplierFactory {
    Supplier<String> create();
}
