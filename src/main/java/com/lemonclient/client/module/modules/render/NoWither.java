package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.RenderEntityEvent;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.projectile.EntityWitherSkull;

@Module.Declaration(
   name = "NoWither",
   category = Category.Render
)
public class NoWither extends Module {
   @EventHandler
   private final Listener<RenderEntityEvent.Head> render = new Listener((event) -> {
      if (event.getEntity() instanceof EntityWither || event.getEntity() instanceof EntityWitherSkull) {
         event.cancel();
      }

   }, new Predicate[0]);
}
