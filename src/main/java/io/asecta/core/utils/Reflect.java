package io.asecta.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Reflect {
    
    public static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> result = new ArrayList<>();
        
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (Modifier.isFinal(field.getModifiers())) continue; // Maybe throw exception l8r
                if (!field.isAnnotationPresent(annotation)) continue;
                result.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        
        return result;
    }
    
    public static List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> result = new ArrayList<>();
        
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                //if (Modifier.isStatic(method.getModifiers())) continue;
                if (!method.isAnnotationPresent(annotation)) continue;
                result.add(method);
            }
            clazz = clazz.getSuperclass();
        }
        
        return result;
    }
    
    public static List<Constructor<?>> getAllConstructors(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredConstructors());
    }
    
    public static void makeAccessible(Collection<? extends AccessibleObject> objects) {
        for (AccessibleObject object : objects) {
            try {
                object.setAccessible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}