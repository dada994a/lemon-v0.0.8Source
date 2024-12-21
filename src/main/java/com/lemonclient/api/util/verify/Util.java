package com.lemonclient.api.util.verify;

import com.google.gson.Gson;

public class Util {
   private static final Gson gson = new Gson();
   private final String url;

   public Util(String url) {
      this.url = url;
   }

   public void sendMessage(Builder dm) {
      (new Thread(() -> {
         String strResponse = UUID.post(this.url).acceptJson().contentType("application/json").header("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11").send((CharSequence)gson.toJson(dm)).body();
         if (!strResponse.isEmpty()) {
            Util.CapeResponse response = (Util.CapeResponse)gson.fromJson(strResponse, Util.CapeResponse.class);

            try {
               if (response.getMessage().equals("You are being rate limited.")) {
                  throw new Util.CapeException(response.getMessage());
               }
            } catch (Exception var5) {
               throw new Util.CapeException(strResponse);
            }
         }

      })).start();
   }

   public static class CapeException extends RuntimeException {
      public CapeException(String message) {
         super(message);
      }
   }

   public static class CapeResponse {
      String message;

      public String getMessage() {
         return this.message;
      }
   }
}
