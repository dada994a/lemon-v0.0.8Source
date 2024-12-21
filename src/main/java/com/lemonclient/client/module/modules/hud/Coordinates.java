package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Module.Declaration(
   name = "Coordinates",
   category = Category.HUD,
   drawn = false
)
@HUDModule.Declaration(
   posX = 0,
   posZ = 0
)
public class Coordinates extends HUDModule {
   BooleanSetting showNetherOverworld = this.registerBoolean("Show Nether", true);
   BooleanSetting thousandsSeparator = this.registerBoolean("Thousands Separator", true);
   IntegerSetting decimalPlaces = this.registerInteger("Decimal Places", 1, 0, 5);
   private final String[] coordinateString = new String[]{"", ""};
   @EventHandler
   private final Listener<ClientTickEvent> listener = new Listener((event) -> {
      if (event.phase == Phase.END) {
         Entity viewEntity = mc.func_175606_aa();
         EntityPlayerSP player = mc.field_71439_g;
         if (viewEntity == null) {
            if (player == null) {
               return;
            }

            viewEntity = player;
         }

         int dimension = ((Entity)viewEntity).field_71093_bK;
         this.coordinateString[0] = "XYZ " + this.getFormattedCoords(((Entity)viewEntity).field_70165_t, ((Entity)viewEntity).field_70163_u, ((Entity)viewEntity).field_70161_v);
         switch(dimension) {
         case -1:
            this.coordinateString[1] = "Overworld " + this.getFormattedCoords(((Entity)viewEntity).field_70165_t * 8.0D, ((Entity)viewEntity).field_70163_u, ((Entity)viewEntity).field_70161_v * 8.0D);
            break;
         case 0:
            this.coordinateString[1] = "Nether " + this.getFormattedCoords(((Entity)viewEntity).field_70165_t / 8.0D, ((Entity)viewEntity).field_70163_u, ((Entity)viewEntity).field_70161_v / 8.0D);
         }

      }
   }, new Predicate[0]);

   private String getFormattedCoords(double x, double y, double z) {
      return this.roundOrInt(x) + ", " + this.roundOrInt(y) + ", " + this.roundOrInt(z);
   }

   private String roundOrInt(double input) {
      String separatorFormat;
      if ((Boolean)this.thousandsSeparator.getValue()) {
         separatorFormat = ",";
      } else {
         separatorFormat = "";
      }

      return String.format('%' + separatorFormat + '.' + this.decimalPlaces.getValue() + 'f', input);
   }

   public void populate(ITheme theme) {
      this.component = new ListComponent(new Labeled(this.getName(), (String)null, () -> {
         return true;
      }), this.position, this.getName(), new Coordinates.CoordinateLabel(), 9, 1);
   }

   private class CoordinateLabel implements HUDList {
      private CoordinateLabel() {
      }

      public int getSize() {
         EntityPlayerSP player = Coordinates.mc.field_71439_g;
         int dimension = player != null ? player.field_71093_bK : 1;
         return !(Boolean)Coordinates.this.showNetherOverworld.getValue() || dimension != -1 && dimension != 0 ? 1 : 2;
      }

      public String getItem(int index) {
         return Coordinates.this.coordinateString[index];
      }

      public Color getItemColor(int index) {
         return new Color(255, 255, 255);
      }

      public boolean sortUp() {
         return false;
      }

      public boolean sortRight() {
         return false;
      }

      // $FF: synthetic method
      CoordinateLabel(Object x1) {
         this();
      }
   }
}
