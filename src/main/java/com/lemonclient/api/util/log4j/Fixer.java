package com.lemonclient.api.util.log4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import javax.naming.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.net.JndiManager;

public class Fixer {
   public static void doRuntimeTest(Logger logger) {
      logger.info("Fix4Log4J loaded.");
      logger.info("If you see stacktrace below, CLOSE EVERYTHING IMMEDIATELY!");
      String someRandomUri = randomUri();
      logger.info("Exploit Test: ${jndi:ldap://" + someRandomUri + "}");
   }

   private static String randomUri() {
      char[] buf = new char[81];
      Random rng = new SecureRandom();

      for(int i = 0; i < buf.length; ++i) {
         buf[i] = (char)(97 + rng.nextInt(26));
      }

      buf[40] = ':';
      return new String(buf);
   }

   public static void disableJndiManager() {
      try {
         disableJndiManager0();
      } catch (Exception var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }

   private static void disableJndiManager0() {
      JndiManager.getDefaultManager();
      Class<AbstractManager> mapHolder = AbstractManager.class;
      Arrays.stream(mapHolder.getDeclaredFields()).filter((f) -> {
         return Modifier.isStatic(f.getModifiers());
      }).filter((f) -> {
         return Map.class.isAssignableFrom(f.getType());
      }).map((f) -> {
         try {
            f.setAccessible(true);
            return (Map)((Map)f.get((Object)null));
         } catch (IllegalAccessException var2) {
            throw new ExceptionInInitializerError(var2);
         }
      }).forEach((map) -> {
         if (map != null) {
            map.forEach((k, v) -> {
               if (v instanceof JndiManager) {
                  try {
                     fixJndiManager((JndiManager)v);
                  } catch (ReflectiveOperationException var3) {
                     throw new ExceptionInInitializerError(var3);
                  }
               }

            });
         }
      });
   }

   private static void fixJndiManager(JndiManager jndiManager) throws ReflectiveOperationException {
      Arrays.stream(jndiManager.getClass().getDeclaredFields()).filter((f) -> {
         return Context.class.isAssignableFrom(f.getType());
      }).forEach((f) -> {
         try {
            f.setAccessible(true);
            removeFinalModifier(f);
            f.set(jndiManager, EmptyJndiContext.INSTANCE);
         } catch (IllegalAccessException var3) {
            throw new ExceptionInInitializerError(var3);
         }
      });
   }

   public static void removeFinalModifier(Field field) throws IllegalAccessException {
      try {
         if (Modifier.isFinal(field.getModifiers())) {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            boolean doForceAccess = !modifiersField.isAccessible();
            if (doForceAccess) {
               modifiersField.setAccessible(true);
            }

            try {
               modifiersField.setInt(field, field.getModifiers() & -17);
            } finally {
               if (doForceAccess) {
                  modifiersField.setAccessible(false);
               }

            }
         }
      } catch (NoSuchFieldException var7) {
      }

   }

   private Fixer() {
   }
}
