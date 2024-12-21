package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayer.Position;

@Module.Declaration(
   name = "ClipFlight",
   category = Category.Exploits
)
public class ClipFlight extends Module {
   ModeSetting flight = this.registerMode("Mode", Arrays.asList("Flight", "Clip"), "Clip");
   IntegerSetting packets = this.registerInteger("Packets", 80, 1, 300);
   IntegerSetting speed = this.registerInteger("XZ Speed", 7, -99, 99, () -> {
      return ((String)this.flight.getValue()).equalsIgnoreCase("Flight");
   });
   IntegerSetting speedY = this.registerInteger("Y Speed", 7, -99, 99, () -> {
      return !((String)this.flight.getValue()).equalsIgnoreCase("Relative");
   });
   BooleanSetting bypass = this.registerBoolean("Bypass", false);
   IntegerSetting interval = this.registerInteger("Interval", 25, 1, 100, () -> {
      return ((String)this.flight.getValue()).equalsIgnoreCase("Clip");
   });
   BooleanSetting update = this.registerBoolean("Update Position Client Side", false);
   int num = 0;
   double startFlat = 0.0D;
   @EventHandler
   private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener((event) -> {
      double[] dir = MotionUtil.forward((double)(Integer)this.speed.getValue());
      String var3 = (String)this.flight.getValue();
      byte var4 = -1;
      switch(var3.hashCode()) {
      case 2103152:
         if (var3.equals("Clip")) {
            var4 = 1;
         }
         break;
      case 2107011216:
         if (var3.equals("Flight")) {
            var4 = 0;
         }
      }

      switch(var4) {
      case 0:
         double xPos = mc.field_71439_g.field_70165_t;
         double yPos = mc.field_71439_g.field_70163_u;
         double zPos = mc.field_71439_g.field_70161_v;
         if (mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d()) {
            yPos += (double)(Integer)this.speedY.getValue();
         } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
            yPos -= (double)(Integer)this.speedY.getValue();
         }

         xPos += dir[0];
         zPos += dir[1];
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(xPos, yPos, zPos, false));
         if ((Boolean)this.update.getValue()) {
            mc.field_71439_g.func_70107_b(xPos, yPos, zPos);
         }

         if ((Boolean)this.bypass.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.05D, mc.field_71439_g.field_70161_v, true));
         }
         break;
      case 1:
         if (mc.field_71474_y.field_151444_V.func_151470_d() || mc.field_71439_g.field_70173_aa % (Integer)this.interval.getValue() == 0) {
            for(int i = 0; i < (Integer)this.packets.getValue(); ++i) {
               double yposition = mc.field_71439_g.field_70163_u + (double)(Integer)this.speedY.getValue();
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, yposition, mc.field_71439_g.field_70161_v, false));
               if ((Boolean)this.update.getValue()) {
                  mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, yposition, mc.field_71439_g.field_70161_v);
               }

               if ((Boolean)this.bypass.getValue()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.05D, mc.field_71439_g.field_70161_v, true));
               }
            }
         }
      }

   }, new Predicate[0]);

   public void onEnable() {
      this.startFlat = mc.field_71439_g.field_70163_u;
      this.num = 0;
   }
}
