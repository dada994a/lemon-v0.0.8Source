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
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.client.manager.managers.TotemPopManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.Iterator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

@Module.Declaration(
   name = "Nametags",
   category = Category.Render
)
public class Nametags extends Module {
   IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
   DoubleSetting scale = this.registerDouble("Scale", 0.5D, 0.01D, 1.0D);
   DoubleSetting maxScale = this.registerDouble("Max Scale", 0.5D, 0.0D, 2.0D);
   BooleanSetting renderSelf = this.registerBoolean("Render Self", false);
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
   public BooleanSetting border;
   public BooleanSetting outline;
   public BooleanSetting customColor;
   public ColorSetting borderColor;

   public Nametags() {
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
      this.outline = this.registerBoolean("Outline", false);
      this.customColor = this.registerBoolean("Custom Color", true, () -> {
         return (Boolean)this.outline.getValue();
      });
      this.borderColor = this.registerColor("Border Color", new GSColor(255, 0, 0, 255), () -> {
         return (Boolean)this.outline.getValue() && (Boolean)this.customColor.getValue();
      });
   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         mc.field_71441_e.field_73010_i.stream().filter(this::shouldRender).forEach((entityPlayer) -> {
            Vec3d vec3d = this.findEntityVec3d(entityPlayer);
            this.renderNameTags(entityPlayer, vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c);
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

   private Vec3d findEntityVec3d(EntityPlayer entityPlayer) {
      double posX = this.balancePosition(entityPlayer.field_70165_t, entityPlayer.field_70142_S);
      double posY = this.balancePosition(entityPlayer.field_70163_u, entityPlayer.field_70137_T);
      double posZ = this.balancePosition(entityPlayer.field_70161_v, entityPlayer.field_70136_U);
      return new Vec3d(posX, posY, posZ);
   }

   private double balancePosition(double newPosition, double oldPosition) {
      return oldPosition + (newPosition - oldPosition) * (double)mc.field_71428_T.field_194147_b;
   }

   private void renderNameTags(EntityPlayer entityPlayer, double posX, double posY, double posZ) {
      double adjustedY = posY + (entityPlayer.func_70093_af() ? 1.9D : 2.1D);
      String[] name = new String[]{this.buildEntityNameString(entityPlayer)};
      RenderUtil.drawNametag(posX, adjustedY, posZ, name, this.findTextColor(entityPlayer), 2, (Double)this.scale.getValue(), (Double)this.maxScale.getValue());
      this.renderItemsAndArmor(entityPlayer, 0, 0);
      GlStateManager.func_179121_F();
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

   private void renderItemsAndArmor(EntityPlayer entityPlayer, int posX, int posY) {
      ItemStack mainHandItem = entityPlayer.func_184614_ca();
      ItemStack offHandItem = entityPlayer.func_184592_cb();
      int armorCount = 3;

      int armorCount2;
      for(armorCount2 = 0; armorCount2 <= 3; ++armorCount2) {
         ItemStack itemStack = (ItemStack)entityPlayer.field_71071_by.field_70460_b.get(armorCount);
         if (!itemStack.func_190926_b()) {
            posX -= 8;
            int size = EnchantmentHelper.func_82781_a(itemStack).size();
            if ((Boolean)this.showItems.getValue() && size > posY) {
               posY = size;
            }
         }

         --armorCount;
      }

      if (!mainHandItem.func_190926_b() && ((Boolean)this.showItems.getValue() || (Boolean)this.showDurability.getValue() && offHandItem.func_77984_f())) {
         posX -= 8;
         armorCount2 = EnchantmentHelper.func_82781_a(offHandItem).size();
         if ((Boolean)this.showItems.getValue() && armorCount2 > posY) {
            posY = armorCount2;
         }
      }

      int armorY;
      if (!mainHandItem.func_190926_b()) {
         armorCount2 = EnchantmentHelper.func_82781_a(mainHandItem).size();
         if ((Boolean)this.showItems.getValue() && armorCount2 > posY) {
            posY = armorCount2;
         }

         armorY = this.findArmorY(posY);
         if ((Boolean)this.showItems.getValue() || (Boolean)this.showDurability.getValue() && mainHandItem.func_77984_f()) {
            posX -= 8;
         }

         if ((Boolean)this.showItems.getValue()) {
            this.renderItem(mainHandItem, posX, armorY, posY);
            armorY -= 32;
         }

         if ((Boolean)this.showDurability.getValue() && mainHandItem.func_77984_f()) {
            this.renderItemDurability(mainHandItem, posX, armorY);
         }

         ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
         armorY -= (Boolean)colorMain.customFont.getValue() ? FontUtil.getFontHeight((Boolean)colorMain.customFont.getValue()) : mc.field_71466_p.field_78288_b;
         if ((Boolean)this.showItemName.getValue()) {
            this.renderItemName(mainHandItem, armorY);
         }

         if ((Boolean)this.showItems.getValue() || (Boolean)this.showDurability.getValue() && mainHandItem.func_77984_f()) {
            posX += 16;
         }
      }

      armorCount2 = 3;

      for(armorY = 0; armorY <= 3; ++armorY) {
         ItemStack itemStack = (ItemStack)entityPlayer.field_71071_by.field_70460_b.get(armorCount2);
         if (!itemStack.func_190926_b()) {
            int armorY = this.findArmorY(posY);
            if ((Boolean)this.showItems.getValue()) {
               this.renderItem(itemStack, posX, armorY, posY);
               armorY -= 32;
            }

            if ((Boolean)this.showDurability.getValue() && itemStack.func_77984_f()) {
               this.renderItemDurability(itemStack, posX, armorY);
            }

            posX += 16;
         }

         --armorCount2;
      }

      if (!offHandItem.func_190926_b()) {
         armorY = this.findArmorY(posY);
         if ((Boolean)this.showItems.getValue()) {
            this.renderItem(offHandItem, posX, armorY, posY);
            armorY -= 32;
         }

         if ((Boolean)this.showDurability.getValue() && offHandItem.func_77984_f()) {
            this.renderItemDurability(offHandItem, posX, armorY);
         }
      }

   }

   private int findArmorY(int posY) {
      int posY2 = (Boolean)this.showItems.getValue() ? -26 : -27;
      if (posY > 4) {
         posY2 -= (posY - 4) * 8;
      }

      return posY2;
   }

   private void renderItemName(ItemStack itemStack, int posY) {
      GlStateManager.func_179098_w();
      GlStateManager.func_179094_E();
      GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
      ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
      FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), itemStack.func_82833_r(), (float)(-FontUtil.getStringWidth((Boolean)colorMain.customFont.getValue(), itemStack.func_82833_r()) / 2), (float)posY, new GSColor(255, 255, 255));
      GlStateManager.func_179121_F();
      GlStateManager.func_179090_x();
   }

   private void renderItemDurability(ItemStack itemStack, int posX, int posY) {
      float damagePercent = (float)(itemStack.func_77958_k() - itemStack.func_77952_i()) / (float)itemStack.func_77958_k();
      float green = damagePercent;
      if (damagePercent > 1.0F) {
         green = 1.0F;
      } else if (damagePercent < 0.0F) {
         green = 0.0F;
      }

      float red = 1.0F - green;
      GlStateManager.func_179098_w();
      GlStateManager.func_179094_E();
      GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
      ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
      FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), (int)(damagePercent * 100.0F) + "%", (float)(posX * 2), (float)posY, new GSColor((int)(red * 255.0F), (int)(green * 255.0F), 0));
      GlStateManager.func_179121_F();
      GlStateManager.func_179090_x();
   }

   private void renderItem(ItemStack itemStack, int posX, int posY, int posY2) {
      GlStateManager.func_179098_w();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179086_m(256);
      GlStateManager.func_179126_j();
      GlStateManager.func_179118_c();
      int posY3 = posY2 > 4 ? (posY2 - 4) * 8 / 2 : 0;
      mc.func_175599_af().field_77023_b = -150.0F;
      RenderHelper.func_74519_b();
      mc.func_175599_af().func_180450_b(itemStack, posX, posY + posY3);
      mc.func_175599_af().func_175030_a(mc.field_71466_p, itemStack, posX, posY + posY3);
      RenderHelper.func_74518_a();
      mc.func_175599_af().field_77023_b = 0.0F;
      RenderUtil.prepare();
      GlStateManager.func_179094_E();
      GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
      this.renderEnchants(itemStack, posX, posY - 24);
      GlStateManager.func_179121_F();
   }

   private void renderEnchants(ItemStack itemStack, int posX, int posY) {
      GlStateManager.func_179098_w();
      Iterator var4 = EnchantmentHelper.func_82781_a(itemStack).keySet().iterator();

      while(var4.hasNext()) {
         Enchantment enchantment = (Enchantment)var4.next();
         if (enchantment != null) {
            if ((Boolean)this.showEnchantName.getValue()) {
               int level = EnchantmentHelper.func_77506_a(enchantment, itemStack);
               ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
               FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), this.findStringForEnchants(enchantment, level), (float)(posX * 2), (float)posY, new GSColor(255, 255, 255));
            }

            posY += 8;
         }
      }

      if (itemStack.func_77973_b().equals(Items.field_151153_ao) && itemStack.func_77962_s()) {
         ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
         FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), "God", (float)(posX * 2), (float)(posY + (!(Boolean)this.showEnchantName.getValue() && (Boolean)this.showItems.getValue() ? 8 : 0)), new GSColor(195, 77, 65));
      }

      GlStateManager.func_179090_x();
   }

   private String findStringForEnchants(Enchantment enchantment, int level) {
      ResourceLocation resourceLocation = (ResourceLocation)Enchantment.field_185264_b.func_177774_c(enchantment);
      String string = resourceLocation == null ? enchantment.func_77320_a() : resourceLocation.toString();
      int charCount = level > 1 ? 12 : 13;
      if (string.length() > charCount) {
         string = string.substring(10, charCount);
      }

      return string.substring(0, 1).toUpperCase() + string.substring(1) + ColorUtil.settingToTextFormatting(this.levelColor) + (level > 1 ? level : "");
   }
}
