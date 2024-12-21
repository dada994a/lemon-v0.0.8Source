package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.InvStack;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.Locks;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;

@Module.Declaration(
   name = "AutoArmor",
   category = Category.Combat
)
public class AutoArmor extends Module {
   IntegerSetting delay = this.registerInteger("Delay", 1, 1, 10);
   BooleanSetting noDesync = this.registerBoolean("No Desync", true);
   BooleanSetting illegalSync = this.registerBoolean("Illegal Sync", true);
   IntegerSetting checkDelay = this.registerInteger("Check Delay", 1, 0, 20, () -> {
      return (Boolean)this.noDesync.getValue();
   });
   BooleanSetting strict = this.registerBoolean("Strict", false);
   BooleanSetting stackArmor = this.registerBoolean("Stack Armor", false);
   IntegerSetting slot = this.registerInteger("Swap Slot", 1, 1, 9, () -> {
      return (Boolean)this.stackArmor.getValue();
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return (Boolean)this.stackArmor.getValue();
   });
   BooleanSetting check = this.registerBoolean("Switch Check", true, () -> {
      return (Boolean)this.stackArmor.getValue();
   });
   BooleanSetting armorSaver = this.registerBoolean("Armor Saver", false);
   BooleanSetting pauseWhenSafe = this.registerBoolean("Pause When Safe", false);
   IntegerSetting depletion = this.registerInteger("Depletion", 20, 0, 99, () -> {
      return (Boolean)this.armorSaver.getValue();
   });
   BooleanSetting allowMend = this.registerBoolean("Allow Mend", false);
   IntegerSetting repair = this.registerInteger("Repair", 80, 0, 100);
   Timing rightClickTimer = new Timing();
   Timing timer = new Timing();
   private boolean sleep;
   @EventHandler
   private final Listener<RightClickItem> listener = new Listener((event) -> {
      if (event.getEntityPlayer() == mc.field_71439_g) {
         if (event.getItemStack().func_77973_b() == Items.field_151062_by) {
            this.rightClickTimer.reset();
         }
      }
   }, new Predicate[0]);

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (mc.field_71439_g.field_70173_aa % (Integer)this.delay.getValue() == 0 && !this.checkDesync()) {
            if (!(Boolean)this.strict.getValue() || mc.field_71439_g.field_70159_w == 0.0D && mc.field_71439_g.field_70179_y == 0.0D) {
               if ((Boolean)this.pauseWhenSafe.getValue()) {
                  List<Entity> proximity = (List)mc.field_71441_e.field_72996_f.stream().filter((e) -> {
                     return e instanceof EntityPlayer && !e.equals(mc.field_71439_g) && mc.field_71439_g.func_70032_d(e) <= 6.0F || e instanceof EntityEnderCrystal && mc.field_71439_g.func_70032_d(e) <= 12.0F;
                  }).collect(Collectors.toList());
                  if (proximity.isEmpty()) {
                     return;
                  }
               }

               boolean isMending = ModuleManager.isModuleEnabled(AutoMend.class);
               if ((Boolean)this.allowMend.getValue() && !this.rightClickTimer.passedMs(500L)) {
                  for(int i = 0; i < mc.field_71439_g.field_71071_by.field_70460_b.size(); ++i) {
                     ItemStack armorPiece = (ItemStack)mc.field_71439_g.field_71071_by.field_70460_b.get(i);
                     if (armorPiece.field_190928_g) {
                        return;
                     }

                     boolean mending = false;
                     Iterator var25 = EnchantmentHelper.func_82781_a(armorPiece).entrySet().iterator();

                     while(var25.hasNext()) {
                        Entry<Enchantment, Integer> entry = (Entry)var25.next();
                        if (((Enchantment)entry.getKey()).func_77320_a().contains("mending")) {
                           mending = true;
                           break;
                        }
                     }

                     if (mending && !armorPiece.func_190926_b()) {
                        long freeSlots = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((is) -> {
                           return is.func_190926_b() || is.func_77973_b() == Items.field_190931_a;
                        }).map((is) -> {
                           return mc.field_71439_g.field_71071_by.func_184429_b(is);
                        }).count();
                        if (freeSlots <= 0L) {
                           return;
                        }

                        if (armorPiece.func_77952_i() != 0) {
                           this.shiftClickSpot(8 - i);
                           return;
                        }
                     }
                  }

               } else if (!(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory) {
                  AtomicBoolean hasSwapped = new AtomicBoolean(false);
                  if (this.sleep) {
                     this.sleep = false;
                  } else {
                     Set<InvStack> replacements = new HashSet();

                     for(int slot = 0; slot < 45; ++slot) {
                        if (slot <= 4 || slot >= 9) {
                           InvStack invStack = new InvStack(slot, mc.field_71439_g.field_71069_bz.func_75139_a(slot).func_75211_c());
                           if (invStack.stack.func_77973_b() instanceof ItemArmor || invStack.stack.func_77973_b() instanceof ItemElytra) {
                              replacements.add(invStack);
                           }
                        }
                     }

                     List<InvStack> armors = (List)replacements.stream().filter((invStackx) -> {
                        return invStackx.stack.func_77973_b() instanceof ItemArmor;
                     }).filter((invStackx) -> {
                        return !(Boolean)this.armorSaver.getValue() || invStackx.stack.func_77973_b().getDurabilityForDisplay(invStackx.stack) < (double)(Integer)this.depletion.getValue();
                     }).sorted(Comparator.comparingInt((invStackx) -> {
                        return invStackx.slot;
                     })).sorted(Comparator.comparingInt((invStackx) -> {
                        return ((ItemArmor)invStackx.stack.func_77973_b()).field_77879_b;
                     })).collect(Collectors.toList());
                     boolean wasEmpty = armors.isEmpty();
                     if (wasEmpty) {
                        armors = (List)replacements.stream().filter((invStackx) -> {
                           return invStackx.stack.func_77973_b() instanceof ItemArmor;
                        }).sorted(Comparator.comparingInt((invStackx) -> {
                           return invStackx.slot;
                        })).sorted(Comparator.comparingInt((invStackx) -> {
                           return ((ItemArmor)invStackx.stack.func_77973_b()).field_77879_b;
                        })).collect(Collectors.toList());
                     }

                     ItemStack currentHeadItem = mc.field_71439_g.field_71071_by.func_70301_a(39);
                     ItemStack currentChestItem = mc.field_71439_g.field_71071_by.func_70301_a(38);
                     ItemStack currentLegsItem = mc.field_71439_g.field_71071_by.func_70301_a(37);
                     ItemStack currentFeetItem = mc.field_71439_g.field_71071_by.func_70301_a(36);
                     boolean saveHead = !wasEmpty && currentHeadItem.func_190916_E() == 1 && (Boolean)this.armorSaver.getValue() && this.getItemDamage(5) <= (Integer)this.depletion.getValue();
                     boolean saveChest = !wasEmpty && currentChestItem.func_190916_E() == 1 && (Boolean)this.armorSaver.getValue() && this.getItemDamage(6) <= (Integer)this.depletion.getValue();
                     boolean saveLegs = !wasEmpty && currentLegsItem.func_190916_E() == 1 && (Boolean)this.armorSaver.getValue() && this.getItemDamage(7) <= (Integer)this.depletion.getValue();
                     boolean saveFeet = !wasEmpty && currentFeetItem.func_190916_E() == 1 && (Boolean)this.armorSaver.getValue() && this.getItemDamage(8) <= (Integer)this.depletion.getValue();
                     boolean replaceHead = currentHeadItem.field_190928_g || saveHead || isMending && this.getItemDamage(5) >= (Integer)this.repair.getValue();
                     boolean replaceChest = currentChestItem.field_190928_g || saveChest || isMending && this.getItemDamage(6) >= (Integer)this.repair.getValue();
                     boolean replaceLegs = currentLegsItem.field_190928_g || saveLegs || isMending && this.getItemDamage(7) >= (Integer)this.repair.getValue();
                     boolean replaceFeet = currentFeetItem.field_190928_g || saveFeet || isMending && this.getItemDamage(8) >= (Integer)this.repair.getValue();
                     if (replaceHead && !hasSwapped.get()) {
                        armors.stream().filter((invStackx) -> {
                           return invStackx.stack.func_77973_b() instanceof ItemArmor;
                        }).filter((invStackx) -> {
                           return ((ItemArmor)invStackx.stack.func_77973_b()).field_77881_a.equals(EntityEquipmentSlot.HEAD);
                        }).filter((invStackx) -> {
                           return !saveHead || this.getItemDamage(invStackx.slot) > (Integer)this.depletion.getValue();
                        }).filter((invStackx) -> {
                           return !isMending || this.getItemDamage(invStackx.slot) <= (Integer)this.repair.getValue();
                        }).findFirst().ifPresent((invStackx) -> {
                           this.swapSlot(invStackx.slot, 5);
                           hasSwapped.set(true);
                        });
                     }

                     if (replaceChest || currentChestItem.func_77973_b() instanceof ItemElytra && !hasSwapped.get()) {
                        armors.stream().filter((invStackx) -> {
                           return invStackx.stack.func_77973_b() instanceof ItemArmor;
                        }).filter((invStackx) -> {
                           return ((ItemArmor)invStackx.stack.func_77973_b()).field_77881_a.equals(EntityEquipmentSlot.CHEST);
                        }).filter((invStackx) -> {
                           return !saveChest || this.getItemDamage(invStackx.slot) > (Integer)this.depletion.getValue();
                        }).filter((invStackx) -> {
                           return !isMending || this.getItemDamage(invStackx.slot) <= (Integer)this.repair.getValue();
                        }).findFirst().ifPresent((invStackx) -> {
                           this.swapSlot(invStackx.slot, 6);
                           hasSwapped.set(true);
                        });
                     }

                     if (replaceLegs && !hasSwapped.get()) {
                        armors.stream().filter((invStackx) -> {
                           return invStackx.stack.func_77973_b() instanceof ItemArmor;
                        }).filter((invStackx) -> {
                           return ((ItemArmor)invStackx.stack.func_77973_b()).field_77881_a.equals(EntityEquipmentSlot.LEGS);
                        }).filter((invStackx) -> {
                           return !saveLegs || this.getItemDamage(invStackx.slot) > (Integer)this.depletion.getValue();
                        }).filter((invStackx) -> {
                           return !isMending || this.getItemDamage(invStackx.slot) <= (Integer)this.repair.getValue();
                        }).findFirst().ifPresent((invStackx) -> {
                           this.swapSlot(invStackx.slot, 7);
                           hasSwapped.set(true);
                        });
                     }

                     if (replaceFeet && !hasSwapped.get()) {
                        armors.stream().filter((invStackx) -> {
                           return invStackx.stack.func_77973_b() instanceof ItemArmor;
                        }).filter((invStackx) -> {
                           return ((ItemArmor)invStackx.stack.func_77973_b()).field_77881_a.equals(EntityEquipmentSlot.FEET);
                        }).filter((invStackx) -> {
                           return !saveFeet || this.getItemDamage(invStackx.slot) > (Integer)this.depletion.getValue();
                        }).filter((invStackx) -> {
                           return !isMending || this.getItemDamage(invStackx.slot) <= (Integer)this.repair.getValue();
                        }).findFirst().ifPresent((invStackx) -> {
                           this.swapSlot(invStackx.slot, 8);
                           hasSwapped.set(true);
                        });
                     }

                  }
               }
            }
         }
      }
   }

   private int getItemDamage(int slot) {
      ItemStack itemStack = mc.field_71439_g.field_71069_bz.func_75139_a(slot).func_75211_c();
      float green = ((float)itemStack.func_77958_k() - (float)itemStack.func_77952_i()) / (float)itemStack.func_77958_k();
      float red = 1.0F - green;
      return 100 - (int)(red * 100.0F);
   }

   private void swapSlot(int source, int target) {
      ItemStack sourceStack = mc.field_71439_g.field_71069_bz.func_75139_a(source).func_75211_c();
      boolean stacked = sourceStack.func_190916_E() > 1;
      if (stacked) {
         this.swapStack(source, target);
      } else {
         this.swap(source, target);
      }

      this.sleep = true;
   }

   private void swapStack(int slotFrom, int slotTo) {
      if ((Boolean)this.stackArmor.getValue()) {
         if (mc.field_71439_g.field_71069_bz.func_75139_a(slotTo).func_75211_c() != ItemStack.field_190927_a) {
            mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slotTo, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
         }

         int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
         if (slotFrom < 36) {
            this.swapToHotbar(slotFrom);
            this.switchTo((Integer)this.slot.getValue() - 1);
         } else {
            this.switchTo(slotFrom - 36);
         }

         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
         this.switchTo(oldslot);
         mc.field_71442_b.func_78765_e();
         if (slotFrom < 36) {
            this.swapToHotbar(slotFrom);
         }

      }
   }

   private boolean checkDesync() {
      if ((Boolean)this.noDesync.getValue() && !(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory && this.timer.passedMs((long)((Integer)this.checkDelay.getValue() * 50))) {
         int bestSlot = -1;
         int clientValue = 0;
         boolean foundType = false;
         int armorValue = mc.field_71439_g.func_70658_aO();

         for(int i = 5; i < 9; ++i) {
            ItemStack stack = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
            if (stack.func_190926_b() && !foundType) {
               bestSlot = i;
               foundType = true;
            } else if (stack.func_77973_b() instanceof ItemArmor) {
               ItemArmor itemArmor = (ItemArmor)stack.func_77973_b();
               clientValue += itemArmor.field_77879_b;
            }
         }

         if (clientValue != armorValue && this.timer.passedMs((long)((Integer)this.delay.getValue() * 50))) {
            if ((Boolean)this.illegalSync.getValue()) {
               InventoryUtil.illegalSync();
            } else {
               Item i;
               if (bestSlot != -1 && getSlot(mc.field_71439_g.field_71071_by.func_70445_o()) == fromSlot(bestSlot)) {
                  i = get(bestSlot).func_77973_b();
                  clickLocked(bestSlot, bestSlot, i, i);
               } else {
                  i = get(20).func_77973_b();
                  clickLocked(20, 20, i, i);
               }
            }

            this.timer.reset();
            return true;
         }
      }

      return false;
   }

   public static void clickLocked(int slot, int to, Item inSlot, Item inTo) {
      Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
         if ((slot == -1 || get(slot).func_77973_b() == inSlot) && get(to).func_77973_b() == inTo) {
            boolean multi = slot >= 0;
            if (multi) {
               click(slot);
            }

            click(to);
         }

      });
   }

   public static void click(int slot) {
      mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, mc.field_71439_g);
   }

   public static ItemStack get(int slot) {
      return slot == -2 ? mc.field_71439_g.field_71071_by.func_70445_o() : (ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(slot);
   }

   public static EntityEquipmentSlot fromSlot(int slot) {
      switch(slot) {
      case 5:
         return EntityEquipmentSlot.HEAD;
      case 6:
         return EntityEquipmentSlot.CHEST;
      case 7:
         return EntityEquipmentSlot.LEGS;
      case 8:
         return EntityEquipmentSlot.FEET;
      default:
         ItemStack stack = get(slot);
         return getSlot(stack);
      }
   }

   public static EntityEquipmentSlot getSlot(ItemStack stack) {
      if (!stack.func_190926_b()) {
         if (stack.func_77973_b() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)stack.func_77973_b();
            return armor.func_185083_B_();
         }

         if (stack.func_77973_b() instanceof ItemElytra) {
            return EntityEquipmentSlot.CHEST;
         }
      }

      return null;
   }

   private void swapToHotbar(int InvSlot) {
      if ((Integer)this.slot.getValue() == 1) {
         mc.field_71442_b.func_187098_a(0, InvSlot, 0, ClickType.SWAP, mc.field_71439_g);
      } else {
         mc.field_71442_b.func_187098_a(0, InvSlot, 0, ClickType.SWAP, mc.field_71439_g);
         mc.field_71442_b.func_187098_a(0, (Integer)this.slot.getValue() + 35, 0, ClickType.SWAP, mc.field_71439_g);
         mc.field_71442_b.func_187098_a(0, InvSlot, 0, ClickType.SWAP, mc.field_71439_g);
      }

      mc.field_71442_b.func_78765_e();
   }

   private void swap(int slotFrom, int slotTo) {
      if (mc.field_71439_g.field_71069_bz.func_75139_a(slotTo).func_75211_c().field_190928_g) {
         mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slotFrom, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
      } else {
         boolean hasEmpty = false;

         for(int l_I = 0; l_I < 36; ++l_I) {
            ItemStack l_Stack = mc.field_71439_g.field_71071_by.func_70301_a(l_I);
            if (l_Stack.field_190928_g) {
               hasEmpty = true;
               break;
            }
         }

         if (hasEmpty) {
            mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slotTo, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
            mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slotFrom, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
         } else {
            mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slotFrom, 0, ClickType.PICKUP, mc.field_71439_g);
            mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slotTo, 0, ClickType.PICKUP, mc.field_71439_g);
            mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slotFrom, 0, ClickType.PICKUP, mc.field_71439_g);
         }
      }

      mc.field_71442_b.func_78765_e();
   }

   private void shiftClickSpot(int source) {
      mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, source, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }

         mc.field_71442_b.func_78765_e();
      }

   }
}
