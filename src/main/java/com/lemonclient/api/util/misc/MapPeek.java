package com.lemonclient.api.util.misc;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.misc.ShulkerBypass;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class MapPeek {
   private final Minecraft mc = Minecraft.func_71410_x();
   private List<List<String>> pages = new ArrayList();

   public static List<List<String>> getTextInBook(ItemStack item) {
      List<String> pages = new ArrayList();
      NBTTagCompound nbt = item.func_77978_p();
      if (nbt != null && nbt.func_74764_b("pages")) {
         NBTTagList nbt2 = nbt.func_150295_c("pages", 8);
         nbt2.forEach((b) -> {
            pages.add(((NBTTagString)b).func_150285_a_());
         });
      }

      List<List<String>> finalPages = new ArrayList();
      Iterator var4 = pages.iterator();

      while(var4.hasNext()) {
         String s = (String)var4.next();
         String buffer = "";
         List<String> pageBuffer = new ArrayList();
         char[] chars = s.toCharArray();
         char[] var9 = chars;
         int var10 = chars.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            char c = var9[var11];
            if (Minecraft.func_71410_x().field_71466_p.func_78256_a(buffer) > 114 || buffer.endsWith("\n")) {
               pageBuffer.add(buffer.replace("\n", ""));
               buffer = "";
            }

            buffer = buffer + c;
         }

         pageBuffer.add(buffer);
         finalPages.add(pageBuffer);
      }

      return finalPages;
   }

   public void draw(int mouseX, int mouseY, GuiContainer screen) {
      try {
         this.pages = null;
         Slot slot = screen.getSlotUnderMouse();
         if (slot == null) {
            return;
         }

         if (ModuleManager.isModuleEnabled("Peek") && ShulkerBypass.books) {
            this.drawBookToolTip(slot, mouseX, mouseY);
         }

         if (ModuleManager.isModuleEnabled("Peek") && ShulkerBypass.maps) {
            if (slot.func_75211_c().func_77973_b() != Items.field_151098_aY) {
               return;
            }

            MapData data = Items.field_151098_aY.func_77873_a(slot.func_75211_c(), this.mc.field_71441_e);
            byte[] colors = ((MapData)Objects.requireNonNull(data)).field_76198_e;
            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 0.5D);
            GL11.glTranslated(0.0D, 0.0D, 300.0D);
            int x = mouseX * 2 + 30;
            int y = mouseY * 2 - 164;
            this.renderTooltipBox(x - 12, y + 12, 128, 128);
            byte[] var9 = colors;
            int var10 = colors.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               int b = var9[var11];
               if (b / 4 != 0) {
                  GuiScreen.func_73734_a(x, y, x + 1, y + 1, MapColor.field_76281_a[(b & 255) / 4].func_151643_b(b & 255 & 3));
               }

               if (x - (mouseX * 2 + 30) == 127) {
                  x = mouseX * 2 + 30;
                  ++y;
               } else {
                  ++x;
               }
            }

            GL11.glScaled(2.0D, 2.0D, 2.0D);
            GL11.glPopMatrix();
         }
      } catch (Exception var13) {
         System.out.println("oopsie poopsie");
         var13.printStackTrace();
      }

   }

   public void drawBookToolTip(Slot slot, int mX, int mY) {
      if (slot.func_75211_c().func_77973_b() == Items.field_151099_bA || slot.func_75211_c().func_77973_b() == Items.field_151164_bB) {
         if (this.pages == null) {
            this.pages = getTextInBook(slot.func_75211_c());
         }

         if (!this.pages.isEmpty()) {
            int lenght = this.mc.field_71466_p.func_78256_a("Page: 1/" + this.pages.size());
            this.renderTooltipBox(mX + 56 - lenght / 2, mY - ((List)this.pages.get(0)).size() * 10 - 19, 5, lenght);
            this.renderTooltipBox(mX, mY - ((List)this.pages.get(0)).size() * 10 - 6, ((List)this.pages.get(0)).size() * 10 - 2, 120);
            this.mc.field_71466_p.func_175063_a("Page: 1/" + this.pages.size(), (float)(mX + 68 - lenght / 2), (float)(mY - ((List)this.pages.get(0)).size() * 10 - 32), -1);
            int count = 0;

            for(Iterator pagesIter = ((List)this.pages.get(0)).iterator(); pagesIter.hasNext(); ++count) {
               String s = (String)pagesIter.next();
               this.mc.field_71466_p.func_175063_a(s, (float)(mX + 12), (float)(mY - 18 - ((List)this.pages.get(0)).size() * 10 + count * 10), 49344);
            }
         }
      }

   }

   public void renderTooltipBox(int x1, int y1, int x2, int y2) {
      GlStateManager.func_179101_C();
      RenderHelper.func_74518_a();
      GlStateManager.func_179140_f();
      GlStateManager.func_179097_i();
      GlStateManager.func_179109_b(0.0F, 0.0F, 300.0F);
      int int_5 = x1 + 12;
      int int_6 = y1 - 12;
      GuiScreen.func_73734_a(int_5 - 3, int_6 - 4, int_5 + y2 + 3, int_6 - 3, -267386864);
      GuiScreen.func_73734_a(int_5 - 3, int_6 + x2 + 3, int_5 + y2 + 3, int_6 + x2 + 4, -267386864);
      GuiScreen.func_73734_a(int_5 - 3, int_6 - 3, int_5 + y2 + 3, int_6 + x2 + 3, -267386864);
      GuiScreen.func_73734_a(int_5 - 4, int_6 - 3, int_5 - 3, int_6 + x2 + 3, -267386864);
      GuiScreen.func_73734_a(int_5 + y2 + 3, int_6 - 3, int_5 + y2 + 4, int_6 + x2 + 3, -267386864);
      GuiScreen.func_73734_a(int_5 - 3, int_6 - 3 + 1, int_5 - 3 + 1, int_6 + x2 + 3 - 1, 1347420415);
      GuiScreen.func_73734_a(int_5 + y2 + 2, int_6 - 3 + 1, int_5 + y2 + 3, int_6 + x2 + 3 - 1, 1347420415);
      GuiScreen.func_73734_a(int_5 - 3, int_6 - 3, int_5 + y2 + 3, int_6 - 3 + 1, 1347420415);
      GuiScreen.func_73734_a(int_5 - 3, int_6 + x2 + 2, int_5 + y2 + 3, int_6 + x2 + 3, 1344798847);
      GlStateManager.func_179145_e();
      GlStateManager.func_179126_j();
      RenderHelper.func_74519_b();
      GlStateManager.func_179091_B();
   }
}
