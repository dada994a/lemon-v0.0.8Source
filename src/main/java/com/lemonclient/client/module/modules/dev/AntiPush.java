package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.MoverType;
import net.minecraft.util.MovementInput;

@Module.Declaration(
   name = "AntiPush",
   category = Category.Dev,
   priority = 1000
)
public class AntiPush extends Module {
   BooleanSetting move = this.registerBoolean("Move", false);
   @EventHandler
   public final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener((event) -> {
      MoverType moverType = event.getType();
      if (moverType != MoverType.SELF && moverType != MoverType.PLAYER) {
         event.cancel();
      }

   }, new Predicate[0]);

   public void fast() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L && (Boolean)this.move.getValue()) {
         MovementInput input = mc.field_71439_g.field_71158_b;
         if ((double)input.field_192832_b == 0.0D && (double)input.field_78902_a == 0.0D) {
            mc.field_71439_g.field_70159_w = 0.0D;
            mc.field_71439_g.field_70179_y = 0.0D;
         }

      }
   }
}
