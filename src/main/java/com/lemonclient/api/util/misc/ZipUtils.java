package com.lemonclient.api.util.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {
   public static void zip(File source, File dest) {
      List<String> list = new ArrayList();
      createFileList(source, source, list);

      try {
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest));
         Iterator var4 = list.iterator();

         while(var4.hasNext()) {
            String file = (String)var4.next();
            ZipEntry ze = new ZipEntry(file);
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            zos.putNextEntry(ze);

            while(true) {
               int len = in.read(buffer);
               if (len <= 0) {
                  in.close();
                  zos.closeEntry();
                  break;
               }

               zos.write(buffer, 0, len);
            }
         }

         zos.close();
      } catch (FileNotFoundException var10) {
         var10.printStackTrace();
      } catch (IOException var11) {
         var11.printStackTrace();
      }

   }

   private static void createFileList(File file, File source, List<String> list) {
      if (file.isFile()) {
         list.add(file.getPath());
      } else if (file.isDirectory()) {
         String[] var3 = file.list();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String subfile = var3[var5];
            createFileList(new File(file, subfile), source, list);
         }
      }

   }
}
