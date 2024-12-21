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
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@Module.Declaration(
   name = "AutoGear",
   category = Category.Misc
)
public class AutoGear extends Module {
   IntegerSetting tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
   IntegerSetting switchForTick = this.registerInteger("Switch Per Tick", 1, 1, 100);
   BooleanSetting enderChest = this.registerBoolean("EnderChest", false);
   BooleanSetting confirmSort = this.registerBoolean("Confirm Sort", true);
   BooleanSetting invasive = this.registerBoolean("Invasive", false);
   BooleanSetting closeAfter = this.registerBoolean("Close After", false);
   BooleanSetting infoMsgs = this.registerBoolean("Info Msgs", true);
   BooleanSetting debugMode = this.registerBoolean("Debug Mode", false);
   private HashMap<Integer, String> planInventory = new HashMap();
   private final HashMap<Integer, String> containerInv = new HashMap();
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
            MessageBus.printDebug("Config " + curConfigName + " activated", false);
         }

         String inventoryConfig = AutoGearCommand.getInventoryKit(curConfigName);
         if (inventoryConfig.equals("")) {
            this.disable();
         } else {
            String[] inventoryDivided = inventoryConfig.split(" ");
            this.planInventory = new HashMap();
            HashMap<String, Integer> nItems = new HashMap();

            for(int i = 0; i < inventoryDivided.length; ++i) {
               if (!inventoryDivided[i].contains("air")) {
                  this.planInventory.put(i, inventoryDivided[i]);
                  if (nItems.containsKey(inventoryDivided[i])) {
                     nItems.put(inventoryDivided[i], (Integer)nItems.get(inventoryDivided[i]) + 1);
                  } else {
                     nItems.put(inventoryDivided[i], 1);
                  }
               }
            }

            this.delayTimeTicks = 0;
            this.openedBefore = this.doneBefore = false;
         }
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

         if ((!(mc.field_71439_g.field_71070_bA instanceof ContainerChest) || !(Boolean)this.enderChest.getValue() && ((ContainerChest)mc.field_71439_g.field_71070_bA).func_85151_d().func_145748_c_().func_150260_c().equals("Ender Chest")) && !(mc.field_71439_g.field_71070_bA instanceof ContainerShulkerBox)) {
            this.openedBefore = false;
         } else {
            this.sortInventoryAlgo();
         }

      }
   }

   private void sortInventoryAlgo() {
      int maxValue;
      int slotChange;
      if (!this.openedBefore) {
         maxValue = mc.field_71439_g.field_71070_bA instanceof ContainerChest ? ((ContainerChest)mc.field_71439_g.field_71070_bA).func_85151_d().func_70302_i_() : 27;

         for(slotChange = 0; slotChange < maxValue; ++slotChange) {
            ItemStack item = (ItemStack)mc.field_71439_g.field_71070_bA.func_75138_a().get(slotChange);
            this.containerInv.put(slotChange, ((ResourceLocation)Objects.requireNonNull(item.func_77973_b().getRegistryName())).toString() + item.func_77960_j());
         }

         this.openedBefore = true;
         HashMap<Integer, String> inventoryCopy = this.getInventoryCopy(maxValue);
         HashMap<Integer, String> aimInventory = this.getInventoryCopy(maxValue, this.planInventory);
         this.sortItems = this.getInventorySort(inventoryCopy, aimInventory, maxValue);
         if (this.sortItems.size() == 0 && !this.doneBefore) {
            this.finishSort = false;
            if ((Boolean)this.closeAfter.getValue()) {
               mc.field_71439_g.func_71053_j();
            }
         } else {
            this.finishSort = true;
            this.stepNow = 0;
         }

         this.openedBefore = true;
      } else if (this.finishSort) {
         for(maxValue = 0; maxValue < (Integer)this.switchForTick.getValue(); ++maxValue) {
            if (this.sortItems.size() != 0) {
               slotChange = (Integer)this.sortItems.get(this.stepNow++);
               mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71070_bA.field_75152_c, slotChange, 0, ClickType.PICKUP, mc.field_71439_g);
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
               if ((Boolean)this.closeAfter.getValue()) {
                  mc.field_71439_g.func_71053_j();
               }

               return;
            }
         }
      }

   }

   private void checkLastItem() {
      if (this.sortItems.size() != 0) {
         int slotChange = (Integer)this.sortItems.get(this.sortItems.size() - 1);
         if (((ItemStack)mc.field_71439_g.field_71070_bA.func_75138_a().get(slotChange)).func_190926_b()) {
            mc.field_71442_b.func_187098_a(0, slotChange, 0, ClickType.PICKUP, mc.field_71439_g);
         }
      }

   }

   private ArrayList<Integer> getInventorySort(HashMap<Integer, String> copyInventory, HashMap<Integer, String> planInventoryCopy, int startValues) {
      ArrayList<Integer> planMove = new ArrayList();
      HashMap<String, Integer> nItemsCopy = new HashMap();
      Iterator var6 = planInventoryCopy.values().iterator();

      while(var6.hasNext()) {
         String value = (String)var6.next();
         if (nItemsCopy.containsKey(value)) {
            nItemsCopy.put(value, (Integer)nItemsCopy.get(value) + 1);
         } else {
            nItemsCopy.put(value, 1);
         }
      }

      ArrayList<Integer> ignoreValues = new ArrayList();
      int[] listValue = new int[planInventoryCopy.size()];
      int id = 0;

      int i;
      for(Iterator var9 = planInventoryCopy.keySet().iterator(); var9.hasNext(); listValue[id++] = i) {
         i = (Integer)var9.next();
      }

      int[] var17 = listValue;
      i = listValue.length;

      int values;
      for(int var11 = 0; var11 < i; ++var11) {
         values = var17[var11];
         if (((String)copyInventory.get(values)).equals(planInventoryCopy.get(values))) {
            ignoreValues.add(values);
            nItemsCopy.put(planInventoryCopy.get(values), (Integer)nItemsCopy.get(planInventoryCopy.get(values)) - 1);
            if ((Integer)nItemsCopy.get(planInventoryCopy.get(values)) == 0) {
               nItemsCopy.remove(planInventoryCopy.get(values));
            }

            planInventoryCopy.remove(values);
         }
      }

      String pickedItem = null;

      int aimKey;
      for(i = startValues; i < startValues + copyInventory.size(); ++i) {
         if (!ignoreValues.contains(i)) {
            String itemCheck = (String)copyInventory.get(i);
            Optional<Entry<Integer, String>> momentAim = planInventoryCopy.entrySet().stream().filter((x) -> {
               return ((String)x.getValue()).equals(itemCheck);
            }).findFirst();
            if (momentAim.isPresent()) {
               if (pickedItem == null) {
                  planMove.add(i);
               }

               aimKey = (Integer)((Entry)momentAim.get()).getKey();
               planMove.add(aimKey);
               if (pickedItem == null || !pickedItem.equals(itemCheck)) {
                  ignoreValues.add(aimKey);
               }

               nItemsCopy.put(itemCheck, (Integer)nItemsCopy.get(itemCheck) - 1);
               if ((Integer)nItemsCopy.get(itemCheck) == 0) {
                  nItemsCopy.remove(itemCheck);
               }

               copyInventory.put(i, copyInventory.get(aimKey));
               copyInventory.put(aimKey, itemCheck);
               if (!((String)copyInventory.get(aimKey)).equals("minecraft:air0")) {
                  if (i >= startValues + copyInventory.size()) {
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
               copyInventory.put(i, pickedItem);
               pickedItem = null;
            }
         }
      }

      if (planMove.size() != 0 && ((Integer)planMove.get(planMove.size() - 1)).equals(planMove.get(planMove.size() - 2))) {
         planMove.remove(planMove.size() - 1);
      }

      Object[] keyList = this.containerInv.keySet().toArray();

      for(values = 0; values < keyList.length; ++values) {
         aimKey = (Integer)keyList[values];
         if (nItemsCopy.containsKey(this.containerInv.get(aimKey))) {
            int start = (Integer)((Entry)planInventoryCopy.entrySet().stream().filter((x) -> {
               return ((String)x.getValue()).equals(this.containerInv.get(aimKey));
            }).findFirst().get()).getKey();
            if ((Boolean)this.invasive.getValue() || ((ItemStack)mc.field_71439_g.field_71070_bA.func_75138_a().get(start)).func_190926_b()) {
               planMove.add(start);
               planMove.add(aimKey);
               planMove.add(start);
               nItemsCopy.put(planInventoryCopy.get(start), (Integer)nItemsCopy.get(planInventoryCopy.get(start)) - 1);
               if ((Integer)nItemsCopy.get(planInventoryCopy.get(start)) == 0) {
                  nItemsCopy.remove(planInventoryCopy.get(start));
               }

               planInventoryCopy.remove(start);
            }
         }
      }

      if ((Boolean)this.debugMode.getValue()) {
         Iterator var22 = planMove.iterator();

         while(var22.hasNext()) {
            aimKey = (Integer)var22.next();
            MessageBus.printDebug(Integer.toString(aimKey), false);
         }
      }

      return planMove;
   }

   private HashMap<Integer, String> getInventoryCopy(int startPoint) {
      HashMap<Integer, String> output = new HashMap();
      int sizeInventory = mc.field_71439_g.field_71071_by.field_70462_a.size();

      for(int i = 0; i < sizeInventory; ++i) {
         int value = i + startPoint + (i < 9 ? sizeInventory - 9 : -9);
         ItemStack item = (ItemStack)mc.field_71439_g.field_71070_bA.func_75138_a().get(value);
         output.put(value, ((ResourceLocation)Objects.requireNonNull(item.func_77973_b().getRegistryName())).toString() + item.func_77960_j());
      }

      return output;
   }

   private HashMap<Integer, String> getInventoryCopy(int startPoint, HashMap<Integer, String> inventory) {
      HashMap<Integer, String> output = new HashMap();
      int sizeInventory = mc.field_71439_g.field_71071_by.field_70462_a.size();
      Iterator var5 = inventory.keySet().iterator();

      while(var5.hasNext()) {
         int val = (Integer)var5.next();
         output.put(val + startPoint + (val < 9 ? sizeInventory - 9 : -9), inventory.get(val));
      }

      return output;
   }
}
