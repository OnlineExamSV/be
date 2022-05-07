package com.toeic.online.commons;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

public class ReflectorUtil {

    public static Field[] getAllFields(Class<?> clazz) {
        List<Class<?>> classes = getAllSuperclasses(clazz);
        classes.add(clazz);
        return getAllFields(classes);
    }

    /**
     * As {@link #getAllFields(Class)} but acts on a list of {@link Class}s and
     * uses only {@link Class#getDeclaredFields()}.
     *
     * @param classes The list of classes to reflect on
     * @return The complete list of fields
     */
    private static Field[] getAllFields(List<Class<?>> classes) {
        Set<Field> fields = new HashSet<>();
        for (Class<?> clazz : classes) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Return a List of super-classes for the given class.
     *
     * @param clazz the class to look up
     * @return the List of super-classes in order going up from this one
     */
    public static List<Class<?>> getAllSuperclasses(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>();

        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }

        return classes;
    }

    public static <T> T mapToDTO(Map<String, Object> map, Class<T> dtoClass) {

        try {
            Constructor<T> constructor = dtoClass.getConstructor();
            T t = constructor.newInstance();

            map.forEach((key,value) -> {
                try {
                    if (value != null) {
                        String fieldName = resolveFieldName(key);
                        String methodName = resolveMethodName(key);
                        Field field = dtoClass.getDeclaredField(fieldName);
                        Class<?> type = field.getType();
                        Method method = dtoClass.getMethod(methodName, type);

                        String simpleName = type.getSimpleName();

                        if ("Integer".equals(simpleName)) {
                            method.invoke(t, (Integer) value);
                            return;
                        }

                        if ("Long".equals(simpleName)) {
                            method.invoke(t, Long.valueOf(value.toString()));
                            return;
                        }

                        if ("Instant".equals(simpleName)) {
                            Timestamp timestamp = Timestamp.valueOf(value.toString());
                            method.invoke(t, timestamp.toInstant());
                            return;
                        }

                        method.invoke(t, value);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    private static String resolveFieldName(String name) {
        String[] split = name.split("");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String ch = split[i];
            if ( ch.equals("_") ) {
                i++;
                ch = split[i].toUpperCase();
            }
            stringBuilder.append(ch);
        }
        return stringBuilder.toString();
    }

    private static String resolveMethodName(String name) {
        String s = resolveFieldName(name);
        return String.format("%s%s%s", "set", s.substring(0,1).toUpperCase(), s.substring(1));
    }
}
