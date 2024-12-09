package com.beaconstrategists.taccaseapiservice.config.api;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;

import java.lang.reflect.Type;

public class SimpleTypeResolutionContext implements TypeResolutionContext {
    private final TypeFactory typeFactory;

    public SimpleTypeResolutionContext(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    @Override
    public JavaType resolveType(Type type) {
        return typeFactory.constructType(type);
    }
}
