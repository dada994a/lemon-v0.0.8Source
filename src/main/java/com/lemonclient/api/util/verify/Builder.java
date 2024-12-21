package com.lemonclient.api.util.verify;

import com.google.gson.annotations.SerializedName;

public class Builder {
   String username;
   String content;
   @SerializedName("avatar_url")
   String avatarUrl;
   @SerializedName("tts")
   boolean textToSpeech;

   public Builder() {
      this((String)null, "", (String)null, false);
   }

   public Builder(String username, String content, String avatar_url, boolean tts) {
      this.capeUsername(username);
      this.setCape(content);
      this.checkCapeUrl(avatar_url);
      this.isDev(tts);
   }

   public void capeUsername(String username) {
      if (username != null) {
         this.username = username.substring(0, Math.min(31, username.length()));
      } else {
         this.username = null;
      }

   }

   public void setCape(String content) {
      this.content = content;
   }

   public void checkCapeUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
   }

   public void isDev(boolean textToSpeech) {
      this.textToSpeech = textToSpeech;
   }

   public static class build {
      private final Builder message = new Builder();

      public Builder.build withUsername(String username) {
         this.message.capeUsername(username);
         return this;
      }

      public Builder.build withContent(String content) {
         this.message.setCape(content);
         return this;
      }

      public Builder.build withAvatarURL(String avatarURL) {
         this.message.checkCapeUrl(avatarURL);
         return this;
      }

      public Builder.build withDev(boolean tts) {
         this.message.isDev(tts);
         return this;
      }

      public Builder build() {
         return this.message;
      }
   }
}
