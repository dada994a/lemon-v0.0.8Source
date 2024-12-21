package com.lemonclient.client.clickgui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.lukflug.panelstudio.config.IConfigList;
import com.lukflug.panelstudio.config.IPanelConfig;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GuiConfig implements IConfigList {
   private final String fileLocation;
   private JsonObject panelObject = null;

   public GuiConfig(String fileLocation) {
      this.fileLocation = fileLocation;
   }

   public void begin(boolean loading) {
      if (loading) {
         Path path = Paths.get(this.fileLocation + "ClickGUI.json");
         if (!Files.exists(path, new LinkOption[0])) {
            return;
         }

         try {
            InputStream inputStream = Files.newInputStream(path);
            JsonObject mainObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
            if (mainObject.get("Panels") == null) {
               return;
            }

            this.panelObject = mainObject.get("Panels").getAsJsonObject();
            inputStream.close();
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      } else {
         this.panelObject = new JsonObject();
      }

   }

   public void end(boolean loading) {
      if (this.panelObject != null) {
         if (!loading) {
            try {
               Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
               OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get(this.fileLocation + "ClickGUI.json")), StandardCharsets.UTF_8);
               JsonObject mainObject = new JsonObject();
               mainObject.add("Panels", this.panelObject);
               String jsonString = gson.toJson((new JsonParser()).parse(mainObject.toString()));
               fileOutputStreamWriter.write(jsonString);
               fileOutputStreamWriter.close();
            } catch (IOException var6) {
               var6.printStackTrace();
            }
         }

         this.panelObject = null;
      }
   }

   public IPanelConfig addPanel(String title) {
      if (this.panelObject == null) {
         return null;
      } else {
         JsonObject valueObject = new JsonObject();
         this.panelObject.add(title, valueObject);
         return new GuiConfig.GSPanelConfig(valueObject);
      }
   }

   public IPanelConfig getPanel(String title) {
      if (this.panelObject == null) {
         return null;
      } else {
         JsonElement configObject = this.panelObject.get(title);
         return configObject != null && configObject.isJsonObject() ? new GuiConfig.GSPanelConfig(configObject.getAsJsonObject()) : null;
      }
   }

   private static class GSPanelConfig implements IPanelConfig {
      private final JsonObject configObject;

      public GSPanelConfig(JsonObject configObject) {
         this.configObject = configObject;
      }

      public void savePositon(Point position) {
         this.configObject.add("PosX", new JsonPrimitive(position.x));
         this.configObject.add("PosY", new JsonPrimitive(position.y));
      }

      public void saveSize(Dimension size) {
      }

      public Point loadPosition() {
         Point point = new Point();
         JsonElement panelPosXObject = this.configObject.get("PosX");
         if (panelPosXObject != null && panelPosXObject.isJsonPrimitive()) {
            point.x = panelPosXObject.getAsInt();
            JsonElement panelPosYObject = this.configObject.get("PosY");
            if (panelPosYObject != null && panelPosYObject.isJsonPrimitive()) {
               point.y = panelPosYObject.getAsInt();
               return point;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      public Dimension loadSize() {
         return null;
      }

      public void saveState(boolean state) {
         this.configObject.add("State", new JsonPrimitive(state));
      }

      public boolean loadState() {
         JsonElement panelOpenObject = this.configObject.get("State");
         return panelOpenObject != null && panelOpenObject.isJsonPrimitive() ? panelOpenObject.getAsBoolean() : false;
      }
   }
}
