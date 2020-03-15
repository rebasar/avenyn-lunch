package net.rebworks.avenyn.lunch.health;

import com.codahale.metrics.health.HealthCheck;

public class ApplicationHealthCheck extends HealthCheck {
    @Override
    protected Result check() {
        return Result.healthy();
    }
}
