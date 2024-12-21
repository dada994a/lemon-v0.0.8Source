package com.lemonclient.api.util.misc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassUtil {
   public static ArrayList<Class<?>> findClassesInPath(String classPath) {
      ArrayList<Class<?>> foundClasses = new ArrayList();
      String resource = ClassUtil.class.getClassLoader().getResource(classPath.replace(".", "/")).getPath();
      if (resource.contains("!")) {
         try {
            ZipInputStream file = new ZipInputStream((new URL(resource.substring(0, resource.lastIndexOf(33)))).openStream());

            ZipEntry entry;
            while((entry = file.getNextEntry()) != null) {
               String name = entry.getName();
               if (name.startsWith(classPath.replace(".", "/") + "/") && name.endsWith(".class")) {
                  try {
                     Class<?> clazz = Class.forName(name.substring(0, name.length() - 6).replace("/", "."));
                     foundClasses.add(clazz);
                  } catch (ClassNotFoundException var10) {
                     var10.printStackTrace();
                  }
               }
            }
         } catch (IOException var12) {
            var12.printStackTrace();
         }
      } else {
         try {
            URL classPathURL = ClassUtil.class.getClassLoader().getResource(classPath.replace(".", "/"));
            if (classPathURL != null) {
               File file = new File(classPathURL.getFile());
               if (file.exists()) {
                  String[] classNamesFound = file.list();
                  if (classNamesFound != null) {
                     String[] var16 = classNamesFound;
                     int var7 = classNamesFound.length;

                     for(int var8 = 0; var8 < var7; ++var8) {
                        String className = var16[var8];
                        if (className.endsWith(".class")) {
                           foundClasses.add(Class.forName(classPath + "." + className.substring(0, className.length() - 6)));
                        }
                     }
                  }
               }
            }
         } catch (Exception var11) {
            var11.printStackTrace();
         }
      }

      foundClasses.sort(Comparator.comparing(Class::getName));
      return foundClasses;
   }
}
