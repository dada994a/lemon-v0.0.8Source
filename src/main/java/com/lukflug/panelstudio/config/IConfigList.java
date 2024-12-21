package com.lukflug.panelstudio.config;

public interface IConfigList {
   void begin(boolean var1);

   void end(boolean var1);

   IPanelConfig addPanel(String var1);

   IPanelConfig getPanel(String var1);
}
