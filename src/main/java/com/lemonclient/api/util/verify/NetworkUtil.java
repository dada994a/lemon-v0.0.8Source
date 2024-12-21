package com.lemonclient.api.util.verify;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.common.FMLLog;

public class NetworkUtil {
   public static List<String> getHWIDList() {
      ArrayList HWIDList = new ArrayList();

      try {
         URL url = new URL("https://pastebin.com/raw/YZ5r5k06");
         BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

         String inputLine;
         while((inputLine = in.readLine()) != null) {
            HWIDList.add(inputLine);
         }
      } catch (Exception var6) {
         try {
            URL url = new URL("https://raw.githubusercontent.com/OaDwH/CapeUUID/main/hwid");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            while((inputLine = in.readLine()) != null) {
               HWIDList.add(inputLine);
            }
         } catch (Exception var5) {
            FMLLog.log.info("Load HWID Failed!");
         }
      }

      return HWIDList;
   }
}
