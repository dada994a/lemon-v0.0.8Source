package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import org.lwjgl.input.Mouse;

@Module.Declaration(
   name = "MCF",
   category = Category.Misc
)
public class MCF extends Module {
   @EventHandler
   private final Listener<MouseInputEvent> listener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L && mc.field_71476_x != null) {
         if (mc.field_71476_x.field_72313_a.equals(Type.ENTITY) && mc.field_71476_x.field_72308_g instanceof EntityPlayer && Mouse.isButtonDown(2)) {
            if (SocialManager.isFriend(mc.field_71476_x.field_72308_g.func_70005_c_())) {
               SocialManager.delFriend(mc.field_71476_x.field_72308_g.func_70005_c_());
               MessageBus.sendClientPrefixMessage(((ColorMain)ModuleManager.getModule(ColorMain.class)).getDisabledColor() + "Removed " + mc.field_71476_x.field_72308_g.func_70005_c_() + " from friends list", Notification.Type.SUCCESS);
            } else {
               SocialManager.addFriend(mc.field_71476_x.field_72308_g.func_70005_c_());
               MessageBus.sendClientPrefixMessage(((ColorMain)ModuleManager.getModule(ColorMain.class)).getEnabledColor() + "Added " + mc.field_71476_x.field_72308_g.func_70005_c_() + " to friends list", Notification.Type.SUCCESS);
            }
         }

      }
   }, new Predicate[0]);
}
