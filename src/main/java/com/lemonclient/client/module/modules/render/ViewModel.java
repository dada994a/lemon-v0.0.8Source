package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.TransformSideFirstPersonEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;

@Module.Declaration(
   name = "ViewModel",
   category = Category.Render
)
public class ViewModel extends Module {
   ModeSetting type = this.registerMode("Type", Arrays.asList("Value", "FOV", "Both"), "Value");
   public BooleanSetting cancelEating = this.registerBoolean("No Eat", false);
   DoubleSetting xLeft = this.registerDouble("Left X", 0.0D, -2.0D, 2.0D);
   DoubleSetting yLeft = this.registerDouble("Left Y", 0.2D, -2.0D, 2.0D);
   DoubleSetting zLeft = this.registerDouble("Left Z", -1.2D, -2.0D, 2.0D);
   DoubleSetting xRight = this.registerDouble("Right X", 0.0D, -2.0D, 2.0D);
   DoubleSetting yRight = this.registerDouble("Right Y", 0.2D, -2.0D, 2.0D);
   DoubleSetting zRight = this.registerDouble("Right Z", -1.2D, -2.0D, 2.0D);
   DoubleSetting fov = this.registerDouble("Item FOV", 130.0D, 70.0D, 200.0D);
   @EventHandler
   private final Listener<TransformSideFirstPersonEvent> eventListener = new Listener((event) -> {
      if (((String)this.type.getValue()).equalsIgnoreCase("Value") || ((String)this.type.getValue()).equalsIgnoreCase("Both")) {
         if (event.getEnumHandSide() == EnumHandSide.RIGHT) {
            GlStateManager.func_179137_b((Double)this.xRight.getValue(), (Double)this.yRight.getValue(), (Double)this.zRight.getValue());
         } else if (event.getEnumHandSide() == EnumHandSide.LEFT) {
            GlStateManager.func_179137_b((Double)this.xLeft.getValue(), (Double)this.yLeft.getValue(), (Double)this.zLeft.getValue());
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<FOVModifier> fovModifierListener = new Listener((event) -> {
      if (((String)this.type.getValue()).equalsIgnoreCase("FOV") || ((String)this.type.getValue()).equalsIgnoreCase("Both")) {
         event.setFOV(((Double)this.fov.getValue()).floatValue());
      }

   }, new Predicate[0]);
}
