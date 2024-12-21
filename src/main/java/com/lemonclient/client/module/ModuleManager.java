package com.lemonclient.client.module;

import com.lemonclient.api.util.misc.ClassUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

public class ModuleManager {
   private static final String modulePath = "com.lemonclient.client.module.modules";
   private static final LinkedHashMap<Class<? extends Module>, Module> modulesClassMap = new LinkedHashMap();
   private static final LinkedHashMap<String, Module> modulesNameMap = new LinkedHashMap();

   public static void init() {
      Category[] var0 = Category.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Category category = var0[var2];
         Iterator var4 = ClassUtil.findClassesInPath("com.lemonclient.client.module.modules." + category.toString().toLowerCase()).iterator();

         while(var4.hasNext()) {
            Class<?> clazz = (Class)var4.next();
            if (clazz != null && Module.class.isAssignableFrom(clazz)) {
               try {
                  Module module = (Module)clazz.newInstance();
                  addMod(module);
               } catch (IllegalAccessException | InstantiationException var7) {
                  var7.printStackTrace();
               }
            }
         }
      }

   }

   private static void addMod(Module module) {
      modulesClassMap.put(module.getClass(), module);
      modulesNameMap.put(module.getName().toLowerCase(Locale.ROOT), module);
   }

   public static Collection<Module> getModules() {
      return modulesClassMap.values();
   }

   public static ArrayList<Module> getModulesInCategory(Category category) {
      ArrayList<Module> list = new ArrayList();
      Iterator var2 = modulesClassMap.values().iterator();

      while(var2.hasNext()) {
         Module module = (Module)var2.next();
         if (module.getCategory().equals(category)) {
            list.add(module);
         }
      }

      return list;
   }

   public static <T extends Module> T getModule(Class<T> clazz) {
      return (Module)modulesClassMap.get(clazz);
   }

   public static Module getModule(String name) {
      return name == null ? null : (Module)modulesNameMap.get(name.toLowerCase(Locale.ROOT));
   }

   public static boolean isModuleEnabled(Class<? extends Module> clazz) {
      Module module = getModule(clazz);
      return module != null && module.isEnabled();
   }

   public static boolean isModuleEnabled(String name) {
      Module module = getModule(name);
      return module != null && module.isEnabled();
   }
}
