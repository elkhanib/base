package com.bosch.inst.esurvey.base.logging;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class HealthMetricsConfiguration {
    private CompositeHealthIndicator healthIndicator;

    public HealthMetricsConfiguration(HealthAggregator healthAggregator,
                                      List<HealthIndicator> healthIndicators,
                                      MeterRegistry registry) {

        healthIndicator = new CompositeHealthIndicator(healthAggregator);

        for (Integer i = 0; i < healthIndicators.size(); i++) {
            healthIndicator.addHealthIndicator(i.toString(), healthIndicators.get(i));
        }

        // presumes there is a common tag applied elsewhere that adds tags for app, etc.
        Gauge.builder("health", healthIndicator, health -> {
            Status status = health.health().getStatus();
            switch (status.getCode()) {
                case "UP":
                    return 3;
                case "OUT_OF_SERVICE":
                    return 2;
                case "DOWN":
                    return 1;
                case "UNKNOWN":
                default:
                    return 0;
            }
        })
                .description("Spring Boot Health endpoint, 3 = UP, 2 = OUT_OF_SERVICE, 1 = DOWN, 0 = UNKNOWN")
                .register(registry);
    }
}
