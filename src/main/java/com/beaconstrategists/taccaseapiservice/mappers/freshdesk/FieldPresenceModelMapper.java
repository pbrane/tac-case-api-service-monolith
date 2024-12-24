package com.beaconstrategists.taccaseapiservice.mappers.freshdesk;

import com.beaconstrategists.taccaseapiservice.dtos.AbstractFieldPresenceAwareDto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class FieldPresenceModelMapper {

    public <S extends AbstractFieldPresenceAwareDto, T> void map(S source, T target) {
        try {
            // Access the fieldPresence map from the source object
            Field fieldPresenceField = AbstractFieldPresenceAwareDto.class.getDeclaredField("fieldPresence");
            fieldPresenceField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Boolean> fieldPresence = (Map<String, Boolean>) fieldPresenceField.get(source);

            // Traverse the source class hierarchy
            Class<?> currentSourceClass = source.getClass();
            while (currentSourceClass != null && currentSourceClass != Object.class) {
                for (Field sourceField : currentSourceClass.getDeclaredFields()) {
                    sourceField.setAccessible(true);

                    String fieldName = sourceField.getName();
                    if (fieldPresence.getOrDefault(fieldName, false)) {
                        // Explicitly resolve the field from the TARGET class hierarchy
                        Field targetField = findFieldInTargetHierarchy(target.getClass(), fieldName);
                        if (targetField != null) {
                            targetField.setAccessible(true);
                            targetField.set(target, sourceField.get(source)); // Set the value from the source

                            // Check if the target is (or extends) AbstractFieldPresenceAwareDto
                            if (isTargetFieldPresenceAware(target)) {
                                // Call markFieldPresent(fieldName) on the target
                                invokeMarkFieldPresent(target, fieldName);
                            }
                        }
                    }
                }
                currentSourceClass = currentSourceClass.getSuperclass();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error during field mapping", e);
        }
    }

    private Field findFieldInTargetHierarchy(Class<?> targetClass, String fieldName) {
        Class<?> currentClass = targetClass;
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName); // Explicitly search the target class
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass(); // Move up the hierarchy
            }
        }
        return null;
    }

    private boolean isTargetFieldPresenceAware(Object target) {
        return AbstractFieldPresenceAwareDto.class.isAssignableFrom(target.getClass());
    }

    private void invokeMarkFieldPresent(Object target, String fieldName) {
        try {
            Method markFieldPresentMethod = AbstractFieldPresenceAwareDto.class.getDeclaredMethod("markFieldPresent", String.class);
            markFieldPresentMethod.setAccessible(true); // Allow access to the method
            markFieldPresentMethod.invoke(target, fieldName);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking markFieldPresent on target object", e);
        }
    }
}






