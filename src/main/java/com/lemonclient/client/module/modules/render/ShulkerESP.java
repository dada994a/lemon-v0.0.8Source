package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "ShulkerESP",
   category = Category.Render
)
public class ShulkerESP extends Module {
   IntegerSetting range = this.registerInteger("Range", 24, 0, 256);
   ColorSetting color = this.registerColor("Color", new GSColor(255, 255, 255));
   IntegerSetting alpha = this.registerInteger("Alpha", 75, 0, 255);
   IntegerSetting outlineAlpha = this.registerInteger("Outline Alpha", 125, 0, 255);

   public void onWorldRender(RenderEvent event) {
      List<BlockPos> storage = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), ((Integer)this.range.getValue()).doubleValue(), ((Integer)this.range.getValue()).doubleValue(), false, false, 0);
      storage.removeIf((p) -> {
         return !(BlockUtil.getBlock(p) instanceof BlockShulkerBox) || mc.field_71439_g.func_70011_f((double)p.func_177958_n() + 0.5D, (double)p.func_177956_o() + 0.5D, (double)p.func_177952_p() + 0.5D) > (double)(Integer)this.range.getValue();
      });
      Iterator var3 = storage.iterator();

      while(var3.hasNext()) {
         BlockPos pos = (BlockPos)var3.next();
         RenderUtil.drawBox(pos, 1.0D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 63);
         RenderUtil.drawBoundingBox(new AxisAlignedBB(pos), 1.0D, new GSColor(this.color.getValue(), (Integer)this.outlineAlpha.getValue()), (Integer)this.outlineAlpha.getValue());
      }

   }
}
