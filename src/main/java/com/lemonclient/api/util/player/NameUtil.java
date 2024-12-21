package com.lemonclient.api.util.player;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class NameUtil {
   private static final Map<String, String> uuidNameCache = Maps.newConcurrentMap();

   public static String resolveName(String uuid) {
      uuid = uuid.replace("-", "");
      if (uuidNameCache.containsKey(uuid)) {
         return (String)uuidNameCache.get(uuid);
      } else {
         String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";

         try {
            String nameJson = IOUtils.toString(new URL(url));
            if (nameJson != null && nameJson.length() > 0) {
               JSONArray jsonArray = (JSONArray)JSONValue.parseWithException(nameJson);
               if (jsonArray != null) {
                  JSONObject latestName = (JSONObject)jsonArray.get(jsonArray.size() - 1);
                  if (latestName != null) {
                     return latestName.get("name").toString();
                  }
               }
            }
         } catch (ParseException | IOException var5) {
         }

         return null;
      }
   }
}
