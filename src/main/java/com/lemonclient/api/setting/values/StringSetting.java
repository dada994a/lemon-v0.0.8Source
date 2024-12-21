package com.lemonclient.api.setting.values;

import com.lemonclient.api.setting.Setting;
import com.lemonclient.client.module.Module;

public class StringSetting extends Setting<String> {
   private String text;

   public StringSetting(String name, Module parent, String text) {
      super(text, name, parent);
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }
}
