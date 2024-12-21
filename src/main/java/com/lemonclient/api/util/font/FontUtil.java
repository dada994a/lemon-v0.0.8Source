package com.lemonclient.api.util.font;

import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.LemonClient;
import net.minecraft.client.Minecraft;

public class FontUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static float drawStringWithShadow(boolean customFont, String text, float x, float y, GSColor color) {
      return customFont ? LemonClient.INSTANCE.cFontRenderer.drawStringWithShadow(text, (double)x, (double)y, color) : (float)mc.field_71466_p.func_175063_a(text, x, y, color.getRGB());
   }

   public static float drawStringWithShadow(boolean customFont, String text, String mark, float x, float y, GSColor color) {
      mc.field_71466_p.func_175063_a(mark, x, y, color.getRGB());
      return customFont ? LemonClient.INSTANCE.cFontRenderer.drawStringWithShadow(text, (double)(x + (float)mc.field_71466_p.func_78256_a(mark)), (double)y, color) : (float)mc.field_71466_p.func_175063_a(text, x + (float)mc.field_71466_p.func_78256_a(mark), y, color.getRGB());
   }

   public static int getStringWidth(boolean customFont, String string) {
      return customFont ? LemonClient.INSTANCE.cFontRenderer.getStringWidth(string) : mc.field_71466_p.func_78256_a(string);
   }

   public static int getFontHeight(boolean customFont) {
      return customFont ? LemonClient.INSTANCE.cFontRenderer.getHeight() : mc.field_71466_p.field_78288_b;
   }
}
