package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.commands.AutoGearCommand;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@Module.Declaration(
   name = "SortInventory",
   category = Category.Misc
)
public class SortInventory extends Module {
   IntegerSetting tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
   BooleanSetting confirmSort = this.registerBoolean("Confirm Sort", true);
   BooleanSetting instaSort = this.registerBoolean("Insta Sort", false);
   BooleanSetting closeAfter = this.registerBoolean("Close After", false);
   BooleanSetting infoMsgs = this.registerBoolean("Info Msgs", true);
   BooleanSetting debugMode = this.registerBoolean("Debug Mode", false);
   private HashMap<Integer, String> planInventory = new HashMap();
   private HashMap<String, Integer> nItems = new HashMap();
   private ArrayList<Integer> sortItems = new ArrayList();
   private int delayTimeTicks;
   private int stepNow;
   private boolean openedBefore;
   private boolean finishSort;
   private boolean doneBefore;

   public void onEnable() {
      String curConfigName = AutoGearCommand.getCurrentSet();
      if (curConfigName.equals("")) {
         this.disable();
      } else {
         if ((Boolean)this.infoMsgs.getValue()) {
            MessageBus.printDebug("Config " + curConfigName + " actived", false);
         }

         String inventoryConfig = AutoGearCommand.getInventoryKit(curConfigName);
         if (inventoryConfig.equals("")) {
            this.disable();
         } else {
            String[] inventoryDivided = inventoryConfig.split(" ");
            this.planInventory = new HashMap();
            this.nItems = new HashMap();

            for(int i = 0; i < inventoryDivided.length; ++i) {
               if (!inventoryDivided[i].contains("air")) {
                  this.planInventory.put(i, inventoryDivided[i]);
                  if (this.nItems.containsKey(inventoryDivided[i])) {
                     this.nItems.put(inventoryDivided[i], (Integer)this.nItems.get(inventoryDivided[i]) + 1);
                  } else {
                     this.nItems.put(inventoryDivided[i], 1);
                  }
               }
            }

            this.delayTimeTicks = 0;
            this.openedBefore = this.doneBefore = false;
            if ((Boolean)this.instaSort.getValue()) {
               mc.func_147108_a(new GuiInventory(mc.field_71439_g));
            }

         }
      }
   }

   public void onDisable() {
      if ((Boolean)this.infoMsgs.getValue() && this.planInventory.size() > 0) {
         MessageBus.printDebug("AutoSort Turned Off!", true);
      }

   }

   public void onUpdate() {
      if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
         ++this.delayTimeTicks;
      } else {
         this.delayTimeTicks = 0;
         if (this.planInventory.size() == 0) {
            this.disable();
         }

         if (mc.field_71462_r instanceof GuiInventory) {
            this.sortInventoryAlgo();
         } else {
            this.openedBefore = false;
         }

      }
   }

   private void sortInventoryAlgo() {
      if (!this.openedBefore) {
         if ((Boolean)this.infoMsgs.getValue() && !this.doneBefore) {
            MessageBus.printDebug("Start sorting inventory...", false);
         }

         this.sortItems = this.getInventorySort();
         if (this.sortItems.size() == 0 && !this.doneBefore) {
            this.finishSort = false;
            if ((Boolean)this.infoMsgs.getValue()) {
               MessageBus.printDebug("Inventory arleady sorted...", true);
            }

            if ((Boolean)this.instaSort.getValue() || (Boolean)this.closeAfter.getValue()) {
               mc.field_71439_g.func_71053_j();
               if ((Boolean)this.instaSort.getValue()) {
                  this.disable();
               }
            }
         } else {
            this.finishSort = true;
            this.stepNow = 0;
         }

         this.openedBefore = true;
      } else if (this.finishSort) {
         if (this.sortItems.size() != 0) {
            int slotChange = (Integer)this.sortItems.get(this.stepNow++);
            mc.field_71442_b.func_187098_a(0, slotChange < 9 ? slotChange + 36 : slotChange, 0, ClickType.PICKUP, mc.field_71439_g);
         }

         if (this.stepNow == this.sortItems.size()) {
            if ((Boolean)this.confirmSort.getValue() && !this.doneBefore) {
               this.openedBefore = false;
               this.finishSort = false;
               this.doneBefore = true;
               this.checkLastItem();
               return;
            }

            this.finishSort = false;
            if ((Boolean)this.infoMsgs.getValue()) {
               MessageBus.printDebug("Inventory sorted", false);
            }

            this.checkLastItem();
            this.doneBefore = false;
            if ((Boolean)this.instaSort.getValue() || (Boolean)this.closeAfter.getValue()) {
               mc.field_71439_g.func_71053_j();
               if ((Boolean)this.instaSort.getValue()) {
                  this.disable();
               }
            }
         }
      }

   }

   private void checkLastItem() {
      if (this.sortItems.size() != 0) {
         int slotChange = (Integer)this.sortItems.get(this.sortItems.size() - 1);
         if (mc.field_71439_g.field_71071_by.func_70301_a(slotChange).func_190926_b()) {
            mc.field_71442_b.func_187098_a(0, slotChange < 9 ? slotChange + 36 : slotChange, 0, ClickType.PICKUP, mc.field_71439_g);
         }
      }

   }

   private ArrayList<Integer> getInventorySort() {
      ArrayList<Integer> planMove = new ArrayList();
      ArrayList<String> copyInventory = this.getInventoryCopy();
      HashMap<Integer, String> planInventoryCopy = (HashMap)this.planInventory.clone();
      HashMap<String, Integer> nItemsCopy = (HashMap)this.nItems.clone();
      ArrayList<Integer> ignoreValues = new ArrayList();

      for(int i = 0; i < this.planInventory.size(); ++i) {
         int value = (Integer)this.planInventory.keySet().toArray()[i];
         if (((String)copyInventory.get(value)).equals(planInventoryCopy.get(value))) {
            ignoreValues.add(value);
            nItemsCopy.put(planInventoryCopy.get(value), (Integer)nItemsCopy.get(planInventoryCopy.get(value)) - 1);
            if ((Integer)nItemsCopy.get(planInventoryCopy.get(value)) == 0) {
               nItemsCopy.remove(planInventoryCopy.get(value));
            }

            planInventoryCopy.remove(value);
         }
      }

      String pickedItem = null;

      for(int i = 0; i < copyInventory.size(); ++i) {
         if (!ignoreValues.contains(i)) {
            String itemCheck = (String)copyInventory.get(i);
            Optional<Entry<Integer, String>> momentAim = planInventoryCopy.entrySet().stream().filter((x) -> {
               return ((String)x.getValue()).equals(itemCheck);
            }).findFirst();
            if (momentAim.isPresent()) {
               if (pickedItem == null) {
                  planMove.add(i);
               }

               int aimKey = (Integer)((Entry)momentAim.get()).getKey();
               planMove.add(aimKey);
               if (pickedItem == null || !pickedItem.equals(itemCheck)) {
                  ignoreValues.add(aimKey);
               }

               nItemsCopy.put(itemCheck, (Integer)nItemsCopy.get(itemCheck) - 1);
               if ((Integer)nItemsCopy.get(itemCheck) == 0) {
                  nItemsCopy.remove(itemCheck);
               }

               copyInventory.set(i, copyInventory.get(aimKey));
               copyInventory.set(aimKey, itemCheck);
               if (!((String)copyInventory.get(aimKey)).equals("minecraft:air0")) {
                  if (i >= copyInventory.size()) {
                     continue;
                  }

                  pickedItem = (String)copyInventory.get(i);
                  --i;
               } else {
                  pickedItem = null;
               }

               planInventoryCopy.remove(aimKey);
            } else if (pickedItem != null) {
               planMove.add(i);
               copyInventory.set(i, pickedItem);
               pickedItem = null;
            }
         }
      }

      if (planMove.size() != 0 && ((Integer)planMove.get(planMove.size() - 1)).equals(planMove.get(planMove.size() - 2))) {
         planMove.remove(planMove.size() - 1);
      }

      if ((Boolean)this.debugMode.getValue()) {
         Iterator var13 = planMove.iterator();

         while(var13.hasNext()) {
            int valuePath = (Integer)var13.next();
            MessageBus.printDebug(Integer.toString(valuePath), false);
         }
      }

      return planMove;
   }

   private ArrayList<String> getInventoryCopy() {
      ArrayList<String> output = new ArrayList();
      Iterator var2 = mc.field_71439_g.field_71071_by.field_70462_a.iterator();

      while(var2.hasNext()) {
         ItemStack i = (ItemStack)var2.next();
         output.add(((ResourceLocation)Objects.requireNonNull(i.func_77973_b().getRegistryName())).toString() + i.func_77960_j());
      }

      return output;
   }
}
