package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@Module.Declaration(
   name = "CatOffhand",
   category = Category.Dev
)
public class OffHandCat extends Module {
   public static OffHandCat INSTANCE;
   public boolean autoCrystal;
   ModeSetting defaultItem = this.registerMode("Default", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "EChest", "Pot", "Exp", "Bed"), "Totem");
   ModeSetting nonDefaultItem = this.registerMode("Non Default", Arrays.asList("Totem", "Crystal", "Gapple", "Obby", "EChest", "Pot", "Exp", "Plates", "String", "Skull", "Bed"), "Crystal");
   ModeSetting noPlayerItem = this.registerMode("No Player", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "EChest", "Pot", "Exp", "Bed"), "Gapple");
   ModeSetting swordMode = this.registerMode("Sword Switch", Arrays.asList("Gapple", "Crystal", "Pot", "None"), "Gapple");
   ModeSetting gappleMode = this.registerMode("Gap Switch", Arrays.asList("Totem", "Gapple", "Crystal", "None"), "Crystal");
   ModeSetting pickaxeMode = this.registerMode("Pick Switch", Arrays.asList("Obsidian", "EChest", "Gapple", "Crystal", "None"), "Gapple");
   ModeSetting shiftPickaxeMode = this.registerMode("Shift Pick", Arrays.asList("Obsidian", "EChest", "Gapple", "Crystal", "None"), "Gapple");
   ModeSetting potionChoose = this.registerMode("Potion", Arrays.asList("first", "strength", "swiftness"), "first");
   IntegerSetting healthSwitch = this.registerInteger("Health Switch", 14, 0, 36);
   IntegerSetting swordHealth = this.registerInteger("Sword Health", 14, 0, 36);
   IntegerSetting tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
   IntegerSetting fallDistance = this.registerInteger("Fall Distance", 12, 0, 30);
   IntegerSetting maxSwitchPerSecond = this.registerInteger("Max Switch", 6, 2, 10);
   DoubleSetting biasDamage = this.registerDouble("Bias Damage", 1.0D, 0.0D, 3.0D);
   DoubleSetting playerDistance = this.registerDouble("Player Distance", 0.0D, 0.0D, 30.0D);
   BooleanSetting rightGap = this.registerBoolean("Right Click Gap", false);
   BooleanSetting shiftPot = this.registerBoolean("Shift Pot", false);
   BooleanSetting swordCheck = this.registerBoolean("Only Sword", true);
   BooleanSetting crystalGap = this.registerBoolean("Crystal Gap", false);
   BooleanSetting fallDistanceBol = this.registerBoolean("Fall Distance", true);
   BooleanSetting crystalCheck = this.registerBoolean("Crystal Check", false);
   IntegerSetting predict = this.registerInteger("Predict Tick", 1, 0, 20);
   BooleanSetting noHotBar = this.registerBoolean("No HotBar", false);
   BooleanSetting onlyHotBar = this.registerBoolean("Only HotBar", false);
   BooleanSetting antiWeakness = this.registerBoolean("AntiWeakness", false);
   BooleanSetting hotBarTotem = this.registerBoolean("Switch HotBar Totem", false);
   BooleanSetting refill = this.registerBoolean("ReFill", true, () -> {
      return (Boolean)this.hotBarTotem.getValue();
   });
   BooleanSetting check = this.registerBoolean("Check", true, () -> {
      return (Boolean)this.hotBarTotem.getValue() && (Boolean)this.refill.getValue();
   });
   IntegerSetting totemSlot = this.registerInteger("Totem Slot", 1, 1, 9, () -> {
      return (Boolean)this.hotBarTotem.getValue() && (Boolean)this.refill.getValue();
   });
   ModeSetting HudMode = this.registerMode("Hud Mode", Arrays.asList("Totem", "Offhand"), "Offhand");
   BooleanSetting debug = this.registerBoolean("Debug Msg", false);
   String ItemName;
   String itemCheck = "";
   int prevSlot;
   int tickWaited;
   int counts;
   int totems;
   boolean returnBack;
   boolean stepChanging;
   boolean firstChange;
   Item item;
   private final ArrayList<Long> switchDone = new ArrayList();
   Map<String, Item> allowedItemsItem = new HashMap<String, Item>() {
      {
         this.put("Totem", Items.field_190929_cY);
         this.put("Crystal", Items.field_185158_cP);
         this.put("Gapple", Items.field_151153_ao);
         this.put("Pot", Items.field_151068_bn);
         this.put("Exp", Items.field_151062_by);
         this.put("Bed", Items.field_151104_aV);
         this.put("String", Items.field_151007_F);
      }
   };
   Map<String, Block> allowedItemsBlock = new HashMap<String, Block>() {
      {
         this.put("Plates", Blocks.field_150452_aw);
         this.put("EChest", Blocks.field_150477_bB);
         this.put("Skull", Blocks.field_150465_bP);
         this.put("Obby", Blocks.field_150343_Z);
      }
   };
   int nowSlot;
   @EventHandler
   private final Listener<PacketEvent.Send> postSendListener = new Listener((event) -> {
      if (event.getPacket() instanceof CPacketHeldItemChange) {
         this.nowSlot = ((CPacketHeldItemChange)event.getPacket()).func_149614_c();
      }

   }, new Predicate[0]);

   public OffHandCat() {
      INSTANCE = this;
   }

   public void onEnable() {
      this.autoCrystal = false;
      this.firstChange = true;
      this.returnBack = false;
   }

   public void onDisable() {
   }

   public void onTick() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L && (!(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory)) {
         if ((Boolean)this.hotBarTotem.getValue() && (Boolean)this.refill.getValue()) {
            boolean hasTotem = false;

            int i;
            for(i = 0; i < 9; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_190929_cY) {
                  hasTotem = true;
               }
            }

            if (!hasTotem || !(Boolean)this.check.getValue()) {
               for(i = 9; i < 36; ++i) {
                  if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_190929_cY) {
                     mc.field_71442_b.func_187098_a(0, i, (Integer)this.totemSlot.getValue() - 1, ClickType.SWAP, mc.field_71439_g);
                     break;
                  }
               }
            }
         }

         if (this.stepChanging) {
            if (this.tickWaited++ < (Integer)this.tickDelay.getValue()) {
               return;
            }

            this.tickWaited = 0;
            this.stepChanging = false;
            mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, mc.field_71439_g);
            this.switchDone.add(System.currentTimeMillis());
         }

         this.totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_190929_cY;
         }).mapToInt(ItemStack::func_190916_E).sum();
         if (this.returnBack) {
            if (this.tickWaited++ < (Integer)this.tickDelay.getValue()) {
               return;
            }

            this.changeBack();
         }

         this.itemCheck = this.getItem(false);
         if (this.offHandSame(this.itemCheck)) {
            if ((Boolean)this.hotBarTotem.getValue() && this.itemCheck.equals("Totem")) {
               this.itemCheck = this.getItem(this.switchItemTotemHot());
            }

            if (this.offHandSame(this.itemCheck)) {
               this.switchItemNormal(this.itemCheck);
            }
         }

         this.GetOffhand();
      }
   }

   private void GetOffhand() {
      if (((String)this.HudMode.getValue()).equals("Offhand")) {
         this.item = mc.field_71439_g.func_184592_cb().func_77973_b();
         int items = mc.field_71439_g.func_184592_cb().func_190916_E();
         this.ItemName = mc.field_71439_g.func_184592_cb().func_82833_r();
         this.counts = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == this.item;
         }).mapToInt(ItemStack::func_190916_E).sum() + items;
      }

   }

   private void changeBack() {
      if (this.prevSlot == -1 || !mc.field_71439_g.field_71071_by.func_70301_a(this.prevSlot).func_190926_b()) {
         this.prevSlot = this.findEmptySlot();
      }

      if (this.prevSlot != -1) {
         mc.field_71442_b.func_187098_a(0, this.prevSlot < 9 ? this.prevSlot + 36 : this.prevSlot, 0, ClickType.PICKUP, mc.field_71439_g);
      } else if ((Boolean)this.debug.getValue()) {
         MessageBus.printDebug("Your inventory is full.", true);
      }

      this.returnBack = false;
      this.tickWaited = 0;
   }

   private boolean switchItemTotemHot() {
      int slot = InventoryUtil.findTotemSlot(0, 8);
      if (slot != -1) {
         if (this.nowSlot != slot) {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         }

         return true;
      } else {
         return false;
      }
   }

   private void switchItemNormal(String itemCheck) {
      int t = this.getInventorySlot(itemCheck);
      if (t != -1) {
         if (itemCheck.equals("Totem") || !this.canSwitch()) {
            this.toOffHand(t);
         }
      }
   }

   private String getItem(boolean mainTotem) {
      String itemCheck = "";
      boolean normalOffHand = true;
      if (!mainTotem && ((Boolean)this.fallDistanceBol.getValue() && mc.field_71439_g.field_70143_R >= (float)(Integer)this.fallDistance.getValue() && mc.field_71439_g.field_70167_r != mc.field_71439_g.field_70163_u && !mc.field_71439_g.func_184613_cA() || (Boolean)this.crystalCheck.getValue() && this.crystalDamage())) {
         normalOffHand = false;
         itemCheck = "Totem";
      }

      Item mainHandItem = mc.field_71439_g.func_184614_ca().func_77973_b();
      if (mainHandItem instanceof ItemSword) {
         boolean can = true;
         if (mc.field_71474_y.field_74313_G.func_151470_d() && (Boolean)this.swordCheck.getValue()) {
            if ((Boolean)this.shiftPot.getValue() && mc.field_71474_y.field_74311_E.func_151470_d()) {
               can = false;
               itemCheck = "Pot";
               normalOffHand = false;
            } else if ((Boolean)this.rightGap.getValue() && !((String)this.swordMode.getValue()).equals("Gapple")) {
               can = false;
               itemCheck = "Gapple";
               normalOffHand = false;
            }
         }

         if (can) {
            String var6 = (String)this.swordMode.getValue();
            byte var7 = -1;
            switch(var6.hashCode()) {
            case -1582753002:
               if (var6.equals("Crystal")) {
                  var7 = 1;
               }
               break;
            case 80437:
               if (var6.equals("Pot")) {
                  var7 = 2;
               }
               break;
            case 2125698931:
               if (var6.equals("Gapple")) {
                  var7 = 0;
               }
            }

            switch(var7) {
            case 0:
               itemCheck = "Gapple";
               normalOffHand = false;
               break;
            case 1:
               itemCheck = "Crystal";
               normalOffHand = false;
               break;
            case 2:
               itemCheck = "Pot";
               normalOffHand = false;
            }
         }
      } else if (!(Boolean)this.swordCheck.getValue()) {
         if ((Boolean)this.shiftPot.getValue() && mc.field_71474_y.field_74311_E.func_151470_d()) {
            itemCheck = "Pot";
            normalOffHand = false;
         } else if ((Boolean)this.rightGap.getValue() && !((String)this.swordMode.getValue()).equals("Gapple")) {
            itemCheck = "Gapple";
            normalOffHand = false;
         }
      }

      String var8;
      byte var9;
      if (mainHandItem == Items.field_151046_w) {
         if (!mc.field_71474_y.field_74311_E.func_151470_d() || mc.field_71474_y.field_74311_E.func_151470_d()) {
            var8 = (String)this.pickaxeMode.getValue();
            var9 = -1;
            switch(var8.hashCode()) {
            case -1582753002:
               if (var8.equals("Crystal")) {
                  var9 = 3;
               }
               break;
            case 416515707:
               if (var8.equals("Obsidian")) {
                  var9 = 0;
               }
               break;
            case 2040486332:
               if (var8.equals("EChest")) {
                  var9 = 1;
               }
               break;
            case 2125698931:
               if (var8.equals("Gapple")) {
                  var9 = 2;
               }
            }

            switch(var9) {
            case 0:
               itemCheck = "Obby";
               normalOffHand = false;
               break;
            case 1:
               itemCheck = "EChest";
               normalOffHand = false;
               break;
            case 2:
               itemCheck = "Gapple";
               normalOffHand = false;
               break;
            case 3:
               itemCheck = "Crystal";
               normalOffHand = false;
            }
         }

         if (mc.field_71474_y.field_74311_E.func_151470_d()) {
            var8 = (String)this.shiftPickaxeMode.getValue();
            var9 = -1;
            switch(var8.hashCode()) {
            case -1582753002:
               if (var8.equals("Crystal")) {
                  var9 = 3;
               }
               break;
            case 416515707:
               if (var8.equals("Obsidian")) {
                  var9 = 0;
               }
               break;
            case 2040486332:
               if (var8.equals("EChest")) {
                  var9 = 1;
               }
               break;
            case 2125698931:
               if (var8.equals("Gapple")) {
                  var9 = 2;
               }
            }

            switch(var9) {
            case 0:
               itemCheck = "Obby";
               normalOffHand = false;
               break;
            case 1:
               itemCheck = "EChest";
               normalOffHand = false;
               break;
            case 2:
               itemCheck = "Gapple";
               normalOffHand = false;
               break;
            case 3:
               itemCheck = "Crystal";
               normalOffHand = false;
            }
         }
      }

      if (mainHandItem == Items.field_151153_ao) {
         var8 = (String)this.gappleMode.getValue();
         var9 = -1;
         switch(var8.hashCode()) {
         case -1582753002:
            if (var8.equals("Crystal")) {
               var9 = 2;
            }
            break;
         case 80997281:
            if (var8.equals("Totem")) {
               var9 = 0;
            }
            break;
         case 2125698931:
            if (var8.equals("Gapple")) {
               var9 = 1;
            }
         }

         switch(var9) {
         case 0:
            itemCheck = "Totem";
            normalOffHand = false;
            break;
         case 1:
            itemCheck = "Gapple";
            normalOffHand = false;
            break;
         case 2:
            itemCheck = "Crystal";
            normalOffHand = false;
         }
      }

      if ((Boolean)this.crystalGap.getValue() && mainHandItem == Items.field_185158_cP) {
         itemCheck = "Gapple";
         normalOffHand = false;
      }

      if (normalOffHand && (Boolean)this.antiWeakness.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
         normalOffHand = false;
         itemCheck = "Crystal";
      }

      if (this.autoCrystal) {
         itemCheck = "Crystal";
         normalOffHand = false;
      }

      if (normalOffHand && !this.nearPlayer()) {
         itemCheck = (String)this.noPlayerItem.getValue();
      }

      itemCheck = this.getItemToCheck(itemCheck, mainTotem);
      return itemCheck;
   }

   private boolean canSwitch() {
      long now = System.currentTimeMillis();

      for(int i = 0; i < this.switchDone.size() && now - (Long)this.switchDone.get(i) > 1000L; ++i) {
         this.switchDone.remove(i);
      }

      if (this.switchDone.size() / 2 >= (Integer)this.maxSwitchPerSecond.getValue()) {
         return true;
      } else {
         this.switchDone.add(now);
         return false;
      }
   }

   private boolean nearPlayer() {
      if (((Double)this.playerDistance.getValue()).intValue() == 0) {
         return true;
      } else {
         Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

         EntityPlayer pl;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            pl = (EntityPlayer)var1.next();
         } while(pl == mc.field_71439_g || !((double)mc.field_71439_g.func_70032_d(pl) < (Double)this.playerDistance.getValue()));

         return true;
      }
   }

   private boolean crystalDamage() {
      PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings((Integer)this.predict.getValue(), true, 39, 2, 2, 1, true, true, true, true, 2, 0.15D);
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity t;
      do {
         do {
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               t = (Entity)var2.next();
            } while(!(t instanceof EntityEnderCrystal));
         } while(!(mc.field_71439_g.func_70032_d(t) <= 12.0F));
      } while(!((double)DamageUtil.calculateCrystalDamage(PredictUtil.predictPlayer(mc.field_71439_g, settings), t.field_70165_t, t.field_70163_u, t.field_70161_v) * (Double)this.biasDamage.getValue() >= (double)EntityUtil.getHealth(mc.field_71439_g)) && (!((double)DamageUtil.calculateCrystalDamage(mc.field_71439_g, t.field_70165_t, t.field_70163_u, t.field_70161_v) * (Double)this.biasDamage.getValue() >= (double)EntityUtil.getHealth(mc.field_71439_g)) || this.totems <= 0));

      return true;
   }

   private int findEmptySlot() {
      for(int i = 35; i > -1; --i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_190926_b()) {
            return i;
         }
      }

      return -1;
   }

   private boolean offHandSame(String itemCheck) {
      Item offHandItem = mc.field_71439_g.func_184592_cb().func_77973_b();
      if (this.allowedItemsBlock.containsKey(itemCheck)) {
         Block item = (Block)this.allowedItemsBlock.get(itemCheck);
         if (offHandItem instanceof ItemBlock) {
            return ((ItemBlock)offHandItem).func_179223_d() != item;
         } else {
            return offHandItem instanceof ItemSkull && item == Blocks.field_150465_bP ? true : true;
         }
      } else {
         Item item = (Item)this.allowedItemsItem.get(itemCheck);
         return item != offHandItem;
      }
   }

   private String getItemToCheck(String str, boolean mainTotem) {
      if (mainTotem) {
         return str.isEmpty() ? (String)this.nonDefaultItem.getValue() : str;
      } else if (mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword) {
         return PlayerUtil.getHealth() > (float)(Integer)this.swordHealth.getValue() ? (str.isEmpty() ? (String)this.nonDefaultItem.getValue() : str) : (String)this.defaultItem.getValue();
      } else {
         return PlayerUtil.getHealth() > (float)(Integer)this.healthSwitch.getValue() ? (str.isEmpty() ? (String)this.nonDefaultItem.getValue() : str) : (String)this.defaultItem.getValue();
      }
   }

   private int getInventorySlot(String itemName) {
      boolean blockBool = false;
      Object item;
      if (this.allowedItemsItem.containsKey(itemName)) {
         item = this.allowedItemsItem.get(itemName);
      } else {
         item = this.allowedItemsBlock.get(itemName);
         blockBool = true;
      }

      int res;
      if (!this.firstChange && this.prevSlot != -1) {
         res = this.isCorrect(this.prevSlot, blockBool, item, itemName);
         if (res != -1) {
            return res;
         }
      }

      for(int i = (Boolean)this.onlyHotBar.getValue() ? 8 : 35; i > ((Boolean)this.noHotBar.getValue() ? 9 : -1); --i) {
         res = this.isCorrect(i, blockBool, item, itemName);
         if (res != -1) {
            return res;
         }
      }

      return -1;
   }

   private int isCorrect(int i, boolean blockBool, Object item, String itemName) {
      Item temp = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
      if (blockBool) {
         if (temp instanceof ItemBlock) {
            if (((ItemBlock)temp).func_179223_d() == item) {
               return i;
            }
         } else if (temp instanceof ItemSkull && item == Blocks.field_150465_bP) {
            return i;
         }
      } else if (item == temp) {
         if (itemName.equals("Pot") && !((String)this.potionChoose.getValue()).equalsIgnoreCase("first") && !mc.field_71439_g.field_71071_by.func_70301_a(i).field_77990_d.toString().split(":")[2].contains((CharSequence)this.potionChoose.getValue())) {
            return -1;
         }

         return i;
      }

      return -1;
   }

   private void toOffHand(int t) {
      if (!mc.field_71439_g.func_184592_cb().func_190926_b()) {
         if (this.firstChange) {
            this.prevSlot = t;
         }

         this.returnBack = true;
         this.firstChange = !this.firstChange;
      } else {
         this.prevSlot = -1;
      }

      mc.field_71442_b.func_187098_a(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.field_71439_g);
      if ((Integer)this.tickDelay.getValue() == 0) {
         mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, mc.field_71439_g);
         this.switchDone.add(System.currentTimeMillis());
      } else {
         this.stepChanging = true;
      }

      this.tickWaited = 0;
   }

   public String getHudInfo() {
      if (((String)this.HudMode.getValue()).equals("Totem")) {
         this.counts = this.totems;
         if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
            ++this.counts;
         }

         return "[" + ChatFormatting.WHITE + "Totem " + this.counts + ChatFormatting.GRAY + "]";
      } else {
         return this.itemCheck.isEmpty() ? "[" + ChatFormatting.WHITE + "None" + ChatFormatting.GRAY + "]" : "[" + ChatFormatting.WHITE + this.itemCheck + " " + this.counts + ChatFormatting.GRAY + "]";
      }
   }
}
