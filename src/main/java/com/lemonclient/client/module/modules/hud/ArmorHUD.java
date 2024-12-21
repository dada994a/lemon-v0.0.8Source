package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.Iterator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

@Module.Declaration(
   name = "ArmorHUD",
   category = Category.HUD,
   drawn = false
)
public class ArmorHUD extends Module {
   public void onRender() {
      GlStateManager.func_179094_E();
      GlStateManager.func_179098_w();
      ScaledResolution resolution = new ScaledResolution(mc);
      int i = resolution.func_78326_a() / 2;
      int iteration = 0;
      int y = resolution.func_78328_b() - 55 - (mc.field_71439_g.func_70090_H() ? 10 : 0);
      Iterator var5 = mc.field_71439_g.field_71071_by.field_70460_b.iterator();

      while(var5.hasNext()) {
         ItemStack is = (ItemStack)var5.next();
         ++iteration;
         if (!is.func_190926_b()) {
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.func_179126_j();
            mc.func_175599_af().field_77023_b = 200.0F;
            mc.func_175599_af().func_180450_b(is, x, y);
            mc.func_175599_af().func_180453_a(mc.field_71466_p, is, x, y, "");
            mc.func_175599_af().field_77023_b = 0.0F;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            String s = is.func_190916_E() > 1 ? is.func_190916_E() + "" : "";
            mc.field_71466_p.func_175063_a(s, (float)(x + 19 - 2 - mc.field_71466_p.func_78256_a(s)), (float)(y + 9), (new GSColor(255, 255, 255)).getRGB());
            float green = ((float)is.func_77958_k() - (float)is.func_77952_i()) / (float)is.func_77958_k();
            float red = 1.0F - green;
            int dmg = 100 - (int)(red * 100.0F);
            if (green > 1.0F) {
               green = 1.0F;
            } else if (green < 0.0F) {
               green = 0.0F;
            }

            if (red > 1.0F) {
               red = 1.0F;
            }

            if (dmg < 0) {
               dmg = 0;
            }

            FontUtil.drawStringWithShadow((Boolean)((ColorMain)ModuleManager.getModule(ColorMain.class)).customFont.getValue(), dmg + "", (float)(x + 8 - mc.field_71466_p.func_78256_a(dmg + "") / 2), (float)(y - 11), new GSColor((int)(red * 255.0F), (int)(green * 255.0F), 0));
         }
      }

      GlStateManager.func_179126_j();
      GlStateManager.func_179121_F();
   }
}
