package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.api.util.misc.ColorUtil;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.manager.managers.TotemPopManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.Iterator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

@Module.Declaration(
   name = "Nametag",
   category = Category.Render
)
public class Nametag extends Module {
   IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
   BooleanSetting scale = this.registerBoolean("Scale", true);
   BooleanSetting smartScale = this.registerBoolean("Smart Scale", true);
   DoubleSetting size = this.registerDouble("Scale", 0.5D, 0.01D, 15.0D);
   DoubleSetting factor = this.registerDouble("Factor", 0.5D, 0.01D, 1.0D);
   BooleanSetting renderSelf = this.registerBoolean("Render Self", false);
   BooleanSetting reverse = this.registerBoolean("Reverse", true);
   BooleanSetting showDurability = this.registerBoolean("Durability", true);
   BooleanSetting showItems = this.registerBoolean("Items", true);
   BooleanSetting showEnchantName = this.registerBoolean("Enchants", true);
   ModeSetting levelColor;
   BooleanSetting showItemName;
   BooleanSetting showGameMode;
   BooleanSetting showHealth;
   BooleanSetting showPing;
   BooleanSetting showTotem;
   BooleanSetting showEntityID;
   BooleanSetting border;
   ColorSetting borderColor;
   BooleanSetting outline;
   ColorSetting outlineColor;

   public Nametag() {
      this.levelColor = this.registerMode("Level Color", ColorUtil.colors, "Green", () -> {
         return (Boolean)this.showEnchantName.getValue();
      });
      this.showItemName = this.registerBoolean("Item Name", false);
      this.showGameMode = this.registerBoolean("Gamemode", false);
      this.showHealth = this.registerBoolean("Health", true);
      this.showPing = this.registerBoolean("Ping", false);
      this.showTotem = this.registerBoolean("Totem Pops", true);
      this.showEntityID = this.registerBoolean("Entity Id", false);
      this.border = this.registerBoolean("Border", false);
      this.borderColor = this.registerColor("Border Color", new GSColor(255, 0, 0, 255), () -> {
         return (Boolean)this.border.getValue();
      });
      this.outline = this.registerBoolean("Outline", false);
      this.outlineColor = this.registerColor("Border Color", new GSColor(255, 0, 0, 255), () -> {
         return (Boolean)this.outline.getValue();
      });
   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         mc.field_71441_e.field_73010_i.stream().filter(this::shouldRender).forEach((entityPlayer) -> {
            Vec3d vec3d = this.findEntityVec3d(entityPlayer);
            this.renderNameTag(entityPlayer, vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c);
         });
      }
   }

   private boolean shouldRender(EntityPlayer entityPlayer) {
      if (entityPlayer == mc.field_71439_g && !(Boolean)this.renderSelf.getValue()) {
         Entity player = mc.func_175606_aa();
         if (player == null) {
            player = mc.field_71439_g;
         }

         if (player == mc.field_71439_g) {
            return false;
         }
      }

      if (entityPlayer.func_70005_c_().length() == 0) {
         return false;
      } else if (!entityPlayer.field_70128_L && !(entityPlayer.func_110143_aJ() <= 0.0F)) {
         return !(entityPlayer.func_70032_d(mc.field_71439_g) > (float)(Integer)this.range.getValue());
      } else {
         return false;
      }
   }

   public static double getDistance(double x, double y, double z) {
      Entity viewEntity = mc.func_175606_aa();
      if (viewEntity == null) {
         viewEntity = mc.field_71439_g;
      }

      double d0 = ((Entity)viewEntity).field_70165_t - x;
      double d1 = ((Entity)viewEntity).field_70163_u - y;
      double d2 = ((Entity)viewEntity).field_70161_v - z;
      return (double)MathHelper.func_76133_a(d0 * d0 + d1 * d1 + d2 * d2);
   }

   private void renderNameTag(EntityPlayer player, double x, double y, double z) {
      double tempY = y + (player.func_70093_af() ? 0.5D : 0.7D);
      String displayTag = this.buildEntityNameString(player);
      double distance = getDistance(x, y, z);
      int width = FontUtil.getStringWidth((Boolean)ColorMain.INSTANCE.customFont.getValue(), displayTag) / 2;
      double scale = (0.0018D + (Double)this.size.getValue() * distance * (Double)this.factor.getValue()) / 1000.0D;
      if (distance <= 6.0D && (Boolean)this.smartScale.getValue()) {
         scale = (0.0018D + ((Double)this.size.getValue() + 2.0D) * distance * (Double)this.factor.getValue()) / 1000.0D;
      }

      if (distance <= 4.0D && (Boolean)this.smartScale.getValue()) {
         scale = (0.0018D + ((Double)this.size.getValue() + 4.0D) * distance * (Double)this.factor.getValue()) / 1000.0D;
      }

      if (!(Boolean)this.scale.getValue()) {
         scale = (Double)this.size.getValue() / 100.0D;
      }

      GlStateManager.func_179094_E();
      RenderHelper.func_74519_b();
      GlStateManager.func_179088_q();
      GlStateManager.func_179136_a(1.0F, -1500000.0F);
      GlStateManager.func_179140_f();
      GlStateManager.func_179109_b((float)x, (float)tempY + 1.4F, (float)z);
      GlStateManager.func_179114_b(-mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
      float var10001 = mc.field_71474_y.field_74320_O == 2 ? -1.0F : 1.0F;
      GlStateManager.func_179114_b(mc.func_175598_ae().field_78732_j, var10001, 0.0F, 0.0F);
      GlStateManager.func_179139_a(-scale, -scale, scale);
      GlStateManager.func_179097_i();
      GlStateManager.func_179147_l();
      GlStateManager.func_179147_l();
      if ((Boolean)this.border.getValue()) {
         this.drawRect((float)(-width - 2), (float)(-(FontUtil.getFontHeight((Boolean)ColorMain.INSTANCE.customFont.getValue()) + 1)), (float)width + 2.0F, 1.5F, this.borderColor.getColor().getRGB());
      } else if (!(Boolean)this.outline.getValue()) {
         this.drawRect(0.0F, 0.0F, 0.0F, 0.0F, this.borderColor.getColor().getRGB());
      }

      if ((Boolean)this.outline.getValue()) {
         this.drawOutlineRect((float)(-width - 2), (float)(-(FontUtil.getFontHeight((Boolean)ColorMain.INSTANCE.customFont.getValue()) + 1)), (float)width + 2.0F, 1.5F, this.outlineColor.getColor().getRGB());
      }

      GlStateManager.func_179084_k();
      ItemStack renderMainHand = player.func_184614_ca().func_77946_l();
      if (renderMainHand.func_77962_s() && (renderMainHand.func_77973_b() instanceof ItemTool || renderMainHand.func_77973_b() instanceof ItemArmor)) {
         renderMainHand.field_77994_a = 1;
      }

      if ((Boolean)this.showItemName.getValue() && !renderMainHand.field_190928_g && renderMainHand.func_77973_b() != Items.field_190931_a) {
         String stackName = renderMainHand.func_82833_r();
         int stackNameWidth = FontUtil.getStringWidth((Boolean)ColorMain.INSTANCE.customFont.getValue(), stackName) / 2;
         GL11.glPushMatrix();
         GL11.glScalef(0.75F, 0.75F, 0.0F);
         FontUtil.drawStringWithShadow((Boolean)ColorMain.INSTANCE.customFont.getValue(), stackName, (float)(-stackNameWidth), (float)((int)(-(this.getBiggestArmorTag(player) + 20.0F))), new GSColor(255, 255, 255));
         GL11.glScalef(1.5F, 1.5F, 1.0F);
         GL11.glPopMatrix();
      }

      if ((Boolean)this.showItems.getValue()) {
         GlStateManager.func_179094_E();
         int xOffset = -6;
         Iterator var22 = player.field_71071_by.field_70460_b.iterator();

         while(var22.hasNext()) {
            ItemStack armourStack = (ItemStack)var22.next();
            if (armourStack != null) {
               xOffset -= 8;
            }
         }

         xOffset -= 8;
         ItemStack renderOffhand = player.func_184592_cb().func_77946_l();
         if (renderOffhand.func_77962_s() && (renderOffhand.func_77973_b() instanceof ItemTool || renderOffhand.func_77973_b() instanceof ItemArmor)) {
            renderOffhand.field_77994_a = 1;
         }

         this.renderItemStack(renderOffhand, xOffset);
         xOffset += 16;
         ItemStack armourStack2;
         int index;
         if ((Boolean)this.reverse.getValue()) {
            for(index = 0; index <= 3; ++index) {
               armourStack2 = (ItemStack)player.field_71071_by.field_70460_b.get(index);
               if (armourStack2.func_77973_b() != Items.field_190931_a) {
                  armourStack2.func_77946_l();
                  this.renderItemStack(armourStack2, xOffset);
                  xOffset += 16;
               }
            }
         } else {
            for(index = 3; index >= 0; --index) {
               armourStack2 = (ItemStack)player.field_71071_by.field_70460_b.get(index);
               if (armourStack2.func_77973_b() != Items.field_190931_a) {
                  armourStack2.func_77946_l();
                  this.renderItemStack(armourStack2, xOffset);
                  xOffset += 16;
               }
            }
         }

         this.renderItemStack(renderMainHand, xOffset);
         GlStateManager.func_179121_F();
      }

      FontUtil.drawStringWithShadow((Boolean)ColorMain.INSTANCE.customFont.getValue(), displayTag, (float)(-width), (float)(-(FontUtil.getFontHeight((Boolean)ColorMain.INSTANCE.customFont.getValue()) - 1)), this.findTextColor(player));
      GlStateManager.func_179126_j();
      GlStateManager.func_179084_k();
      GlStateManager.func_179113_r();
      GlStateManager.func_179136_a(1.0F, 1500000.0F);
      GlStateManager.func_179121_F();
   }

   private void renderItemStack(ItemStack stack, int x) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179086_m(256);
      RenderHelper.func_74519_b();
      mc.func_175599_af().field_77023_b = -150.0F;
      GlStateManager.func_179118_c();
      GlStateManager.func_179126_j();
      GlStateManager.func_179129_p();
      mc.func_175599_af().func_180450_b(stack, x, -26);
      mc.func_175599_af().func_175030_a(mc.field_71466_p, stack, x, -26);
      mc.func_175599_af().field_77023_b = 0.0F;
      RenderHelper.func_74518_a();
      GlStateManager.func_179089_o();
      GlStateManager.func_179141_d();
      GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
      GlStateManager.func_179097_i();
      this.renderEnchantmentText(stack, x);
      GlStateManager.func_179126_j();
      GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
      GlStateManager.func_179121_F();
   }

   private void renderEnchantmentText(ItemStack stack, int x) {
      int enchantmentY = -34;
      if (stack.func_77973_b() == Items.field_151153_ao && stack.func_77962_s()) {
         FontUtil.drawStringWithShadow((Boolean)ColorMain.INSTANCE.customFont.getValue(), "god", (float)(x * 2), (float)enchantmentY, new GSColor(195, 77, 65));
         enchantmentY -= 8;
      }

      NBTTagList enchants = stack.func_77986_q();

      for(int index = 0; index < enchants.func_74745_c(); ++index) {
         short id = enchants.func_150305_b(index).func_74765_d("id");
         short level = enchants.func_150305_b(index).func_74765_d("lvl");
         Enchantment enc = Enchantment.func_185262_c(id);
         if (enc != null) {
            String encName = enc.func_190936_d() ? TextFormatting.RED + enc.func_77316_c(level).substring(0, 4).toLowerCase() : ColorUtil.settingToTextFormatting(this.levelColor) + enc.func_77316_c(level).substring(0, 2).toLowerCase();
            encName = encName + level;
            FontUtil.drawStringWithShadow((Boolean)ColorMain.INSTANCE.customFont.getValue(), encName, (float)(x * 2), (float)enchantmentY, new GSColor(255, 255, 255));
            enchantmentY -= 8;
         }
      }

      if ((Boolean)this.showDurability.getValue() && stack.func_77984_f()) {
         float damagePercent = (float)(stack.func_77958_k() - stack.func_77952_i()) / (float)stack.func_77958_k();
         float green = damagePercent;
         if (damagePercent > 1.0F) {
            green = 1.0F;
         } else if (damagePercent < 0.0F) {
            green = 0.0F;
         }

         float red = 1.0F - green;
         FontUtil.drawStringWithShadow((Boolean)ColorMain.INSTANCE.customFont.getValue(), (int)(damagePercent * 100.0F) + "%", (float)(x * 2), (float)enchantmentY, new GSColor((int)(red * 255.0F), (int)(green * 255.0F), 0));
      }

   }

   private float getBiggestArmorTag(EntityPlayer player) {
      float enchantmentY = 0.0F;
      boolean arm = false;
      Iterator var4 = player.field_71071_by.field_70460_b.iterator();

      ItemStack renderOffHand;
      float encY2;
      NBTTagList enchants2;
      int index;
      short id2;
      Enchantment enc;
      while(var4.hasNext()) {
         renderOffHand = (ItemStack)var4.next();
         encY2 = 0.0F;
         if (renderOffHand != null) {
            enchants2 = renderOffHand.func_77986_q();

            for(index = 0; index < enchants2.func_74745_c(); ++index) {
               id2 = enchants2.func_150305_b(index).func_74765_d("id");
               enc = Enchantment.func_185262_c(id2);
               if (enc != null) {
                  encY2 += 8.0F;
                  arm = true;
               }
            }
         }

         if (!(encY2 <= enchantmentY)) {
            enchantmentY = encY2;
         }
      }

      ItemStack renderMainHand = player.func_184614_ca().func_77946_l();
      if (renderMainHand.func_77962_s()) {
         float encY2 = 0.0F;
         NBTTagList enchants2 = renderMainHand.func_77986_q();

         for(int index2 = 0; index2 < enchants2.func_74745_c(); ++index2) {
            short id = enchants2.func_150305_b(index2).func_74765_d("id");
            Enchantment enc2 = Enchantment.func_185262_c(id);
            if (enc2 != null) {
               encY2 += 8.0F;
               arm = true;
            }
         }

         if (encY2 > enchantmentY) {
            enchantmentY = encY2;
         }
      }

      if ((renderOffHand = player.func_184592_cb().func_77946_l()).func_77962_s()) {
         encY2 = 0.0F;
         enchants2 = renderOffHand.func_77986_q();

         for(index = 0; index < enchants2.func_74745_c(); ++index) {
            id2 = enchants2.func_150305_b(index).func_74765_d("id");
            enc = Enchantment.func_185262_c(id2);
            if (enc != null) {
               encY2 += 8.0F;
               arm = true;
            }
         }

         if (encY2 > enchantmentY) {
            enchantmentY = encY2;
         }
      }

      return (float)(arm ? 0 : 20) + enchantmentY;
   }

   private Vec3d findEntityVec3d(EntityPlayer entityPlayer) {
      double posX = this.balancePosition(entityPlayer.field_70165_t, entityPlayer.field_70142_S);
      double posY = this.balancePosition(entityPlayer.field_70163_u, entityPlayer.field_70137_T);
      double posZ = this.balancePosition(entityPlayer.field_70161_v, entityPlayer.field_70136_U);
      return new Vec3d(posX, posY, posZ);
   }

   private double balancePosition(double newPosition, double oldPosition) {
      return oldPosition + (newPosition - oldPosition) * (double)mc.field_71428_T.field_194147_b;
   }

   private String buildEntityNameString(EntityPlayer entityPlayer) {
      String name = entityPlayer.func_70005_c_();
      if ((Boolean)this.showEntityID.getValue()) {
         name = name + " ID: " + entityPlayer.func_145782_y();
      }

      if ((Boolean)this.showGameMode.getValue()) {
         if (entityPlayer.func_184812_l_()) {
            name = name + " [C]";
         } else if (entityPlayer.func_175149_v()) {
            name = name + " [I]";
         } else {
            name = name + " [S]";
         }
      }

      if ((Boolean)this.showTotem.getValue()) {
         name = name + " [" + TotemPopManager.INSTANCE.getPlayerPopCount(entityPlayer.func_70005_c_()) + "]";
      }

      int health;
      if ((Boolean)this.showPing.getValue()) {
         health = 0;
         if (mc.func_147114_u() != null && mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()) != null) {
            health = mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()).func_178853_c();
         }

         name = name + " " + health + "ms";
      }

      if ((Boolean)this.showHealth.getValue()) {
         health = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
         TextFormatting textFormatting = this.findHealthColor(health);
         name = name + " " + textFormatting + health;
      }

      return name;
   }

   private GSColor findTextColor(EntityPlayer entityPlayer) {
      ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
      if (SocialManager.isFriend(entityPlayer.func_70005_c_())) {
         return colorMain.getFriendGSColor();
      } else if (SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
         return colorMain.getEnemyGSColor();
      } else if (entityPlayer.func_82150_aj()) {
         return new GSColor(128, 128, 128);
      } else if (mc.func_147114_u() != null && mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()) == null) {
         return new GSColor(239, 1, 71);
      } else {
         return entityPlayer.func_70093_af() ? new GSColor(255, 153, 0) : new GSColor(255, 255, 255);
      }
   }

   private TextFormatting findHealthColor(int health) {
      if (health <= 0) {
         return TextFormatting.DARK_RED;
      } else if (health <= 5) {
         return TextFormatting.RED;
      } else if (health <= 10) {
         return TextFormatting.GOLD;
      } else if (health <= 15) {
         return TextFormatting.YELLOW;
      } else {
         return health <= 20 ? TextFormatting.DARK_GREEN : TextFormatting.GREEN;
      }
   }

   public void drawOutlineRect(float x, float y, float w, float h, int color) {
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_187441_d(1.0F);
      GlStateManager.func_179120_a(770, 771, 1, 0);
      bufferbuilder.func_181668_a(2, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)x, (double)h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)w, (double)h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)w, (double)y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)x, (double)y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public void drawRect(float x, float y, float w, float h, int color) {
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_187441_d(1.0F);
      GlStateManager.func_179120_a(770, 771, 1, 0);
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)x, (double)h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)w, (double)h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)w, (double)y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)x, (double)y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }
}
