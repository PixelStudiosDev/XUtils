package dev.pixelstudios.xutils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class ReflectionUtil {

    public static final int VERSION;
    public static final int PATCH_VERSION;

    static {
        String[] parts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");

        VERSION = Integer.parseInt(parts[1]);
        PATCH_VERSION = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;
    }

    public static boolean supports(int version) {
        return VERSION >= version;
    }

    public static boolean supports(int version, int patch) {
        return VERSION > version || (VERSION == version && PATCH_VERSION >= patch);
    }

    public static Object getFieldValue(Class<?> clazz, String fieldName, Object instance) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invokeMethod(Method method, Object instance, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {}
        return null;
    }

    public static boolean isMethodPresent(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            clazz.getDeclaredMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

}
