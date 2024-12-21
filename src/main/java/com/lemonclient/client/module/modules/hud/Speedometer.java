package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.world.TimerUtils;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Module.Declaration(
   name = "Speedometer",
   category = Category.HUD,
   drawn = false
)
@HUDModule.Declaration(
   posX = 0,
   posZ = 70
)
public class Speedometer extends HUDModule {
   private static final String MPS = "m/s";
   private static final String KMH = "km/h";
   private static final String MPH = "mph";
   ModeSetting speedUnit = this.registerMode("Unit", Arrays.asList("m/s", "km/h", "mph"), "km/h");
   BooleanSetting averageSpeed = this.registerBoolean("Average Speed", true);
   IntegerSetting averageSpeedTicks = this.registerInteger("Average Time", 20, 5, 100);
   private final ArrayDeque<Double> speedDeque = new ArrayDeque();
   private String speedString = "";
   @EventHandler
   private final Listener<ClientTickEvent> listener = new Listener((event) -> {
      if (event.phase == Phase.END) {
         EntityPlayerSP player = mc.field_71439_g;
         if (player != null) {
            String unit = (String)this.speedUnit.getValue();
            double speed = this.calcSpeed(player, unit);
            double displaySpeed = speed;
            if ((Boolean)this.averageSpeed.getValue()) {
               if (!(speed > 0.0D) && player.field_70173_aa % 4 != 0) {
                  this.speedDeque.pollFirst();
               } else {
                  this.speedDeque.add(speed);
               }

               while(!this.speedDeque.isEmpty() && this.speedDeque.size() > (Integer)this.averageSpeedTicks.getValue()) {
                  this.speedDeque.poll();
               }

               displaySpeed = this.average(this.speedDeque);
            }

            this.speedString = String.format("%.2f", displaySpeed) + ' ' + unit;
         }
      }
   }, new Predicate[0]);

   protected void onDisable() {
      this.speedDeque.clear();
      this.speedString = "";
   }

   private double calcSpeed(EntityPlayerSP player, String unit) {
      double tps = 1000.0D / (double)TimerUtils.getTickLength();
      double xDiff = player.field_70165_t - player.field_70169_q;
      double zDiff = player.field_70161_v - player.field_70166_s;
      double speed = Math.hypot(xDiff, zDiff) * tps;
      byte var12 = -1;
      switch(unit.hashCode()) {
      case 108325:
         if (unit.equals("mph")) {
            var12 = 1;
         }
         break;
      case 3293947:
         if (unit.equals("km/h")) {
            var12 = 0;
         }
      }

      switch(var12) {
      case 0:
         speed *= 3.6D;
         break;
      case 1:
         speed *= 2.237D;
      }

      return speed;
   }

   private double average(Collection<Double> collection) {
      if (collection.isEmpty()) {
         return 0.0D;
      } else {
         double sum = 0.0D;
         int size = 0;

         for(Iterator var5 = collection.iterator(); var5.hasNext(); ++size) {
            double element = (Double)var5.next();
            sum += element;
         }

         return sum / (double)size;
      }
   }

   public void populate(ITheme theme) {
      this.component = new ListComponent(new Labeled(this.getName(), (String)null, () -> {
         return true;
      }), this.position, this.getName(), new Speedometer.SpeedLabel(), 9, 1);
   }

   private class SpeedLabel implements HUDList {
      private SpeedLabel() {
      }

      public int getSize() {
         return 1;
      }

      public String getItem(int index) {
         return Speedometer.this.speedString;
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
      SpeedLabel(Object x1) {
         this();
      }
   }
}
