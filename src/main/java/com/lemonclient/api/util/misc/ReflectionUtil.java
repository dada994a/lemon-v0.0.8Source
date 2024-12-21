package com.lemonclient.api.util.misc;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.function.Consumer;

public class ReflectionUtil {
   public static void addToClassPath(URLClassLoader classLoader, File file) throws Exception {
      URL url = file.toURI().toURL();
      addToClassPath(classLoader, url);
   }

   public static void addToClassPath(URLClassLoader classLoader, URL url) throws Exception {
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      method.setAccessible(true);
      method.invoke(classLoader, url);
   }

   public static void iterateSuperClasses(Class<?> clazz, Consumer<Class<?>> consumer) {
      while(clazz != Object.class) {
         consumer.accept(clazz);
         clazz = clazz.getSuperclass();
      }

   }

   public static <T> T getField(Class<?> clazz, Object instance, int index) {
      try {
         Field field = clazz.getDeclaredFields()[index];
         field.setAccessible(true);
         return field.get(instance);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   public static void setField(Class<?> clazz, Object instance, int index, Object value) {
      try {
         Field field = clazz.getDeclaredFields()[index];
         field.setAccessible(true);
         field.set(instance, value);
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   public static Field getField(Class<?> clazz, String... mappings) throws NoSuchFieldException {
      String[] var2 = mappings;
      int var3 = mappings.length;
      int var4 = 0;

      while(var4 < var3) {
         String s = var2[var4];

         try {
            return clazz.getDeclaredField(s);
         } catch (NoSuchFieldException var7) {
            ++var4;
         }
      }

      throw new NoSuchFieldException("No Such field: " + clazz.getName() + "-> " + Arrays.toString(mappings));
   }

   public static Method getMethodNoParameters(Class<?> clazz, String... mappings) {
      String[] var2 = mappings;
      int var3 = mappings.length;
      int var4 = 0;

      while(var4 < var3) {
         String s = var2[var4];

         try {
            return clazz.getDeclaredMethod(s);
         } catch (NoSuchMethodException var7) {
            ++var4;
         }
      }

      throw new RuntimeException("Couldn't find: " + Arrays.toString(mappings));
   }

   public static Method getMethod(Class<?> clazz, String notch, String searge, String mcp, Class<?>... parameterTypes) {
      try {
         return clazz.getMethod(searge, parameterTypes);
      } catch (NoSuchMethodException var10) {
         try {
            return clazz.getMethod(notch, parameterTypes);
         } catch (NoSuchMethodException var9) {
            try {
               return clazz.getMethod(mcp, parameterTypes);
            } catch (NoSuchMethodException var8) {
               throw new RuntimeException(var8);
            }
         }
      }
   }

   public static String getSimpleName(String name) {
      return name.substring(name.lastIndexOf(".") + 1);
   }
}
