package com.beaconstrategists.taccaseapiservice.config.authsvr;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.Nullable;

public class DevelopmentOrMonolithCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, @Nullable AnnotatedTypeMetadata metadata) {
        String env = context.getEnvironment().getProperty("AUTH_SVR_ENV");
        return env != null && (env.equals("development") || env.equals("monolith"));
    }
}

