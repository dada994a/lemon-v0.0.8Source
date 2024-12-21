package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

@Module.Declaration(
   name = "ElytraSwap",
   category = Category.Movement
)
public class ElytraSwap extends Module {
   public void onEnable() {
      if (mc.field_71439_g != null) {
         InventoryPlayer items = mc.field_71439_g.field_71071_by;
         ItemStack body = items.func_70440_f(2);
         String body2 = body.func_77973_b().func_77653_i(body);
         int t;
         int u;
         int j;
         if (body2.equals("Air")) {
            t = 0;
            u = 0;

            for(j = 9; j < 45; ++j) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(j).func_77973_b() == Items.field_185160_cR) {
                  t = j;
                  break;
               }
            }

            if (t != 0) {
               MessageBus.sendClientDeleteMessage("Equipping Elytra", Notification.Type.SUCCESS, "ElytraSwap", 1);
               mc.field_71442_b.func_187098_a(0, t, 0, ClickType.PICKUP, mc.field_71439_g);
               mc.field_71442_b.func_187098_a(0, 6, 0, ClickType.PICKUP, mc.field_71439_g);
            }

            if (t == 0) {
               for(j = 9; j < 45; ++j) {
                  if (mc.field_71439_g.field_71071_by.func_70301_a(j).func_77973_b() == Items.field_151163_ad) {
                     u = j;
                     break;
                  }
               }

               if (u != 0) {
                  MessageBus.sendClientDeleteMessage("Equipping Chestplate", Notification.Type.SUCCESS, "ElytraSwap", 1);
                  mc.field_71442_b.func_187098_a(0, u, 0, ClickType.PICKUP, mc.field_71439_g);
                  mc.field_71442_b.func_187098_a(0, 6, 0, ClickType.PICKUP, mc.field_71439_g);
               }
            }

            if (u == 0 && t == 0) {
               MessageBus.sendClientDeleteMessage("You do not have an Elytra or a Chestplate in your inventory. Doing nothing", Notification.Type.ERROR, "ElytraSwap", 1);
            }

            this.disable();
         }

         if (body2.equals("Elytra")) {
            t = 0;

            for(u = 9; u < 45; ++u) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(u).func_77973_b() == Items.field_151163_ad) {
                  t = u;
                  break;
               }
            }

            if (t != 0) {
               u = 0;
               MessageBus.sendClientDeleteMessage("Equipping Chestplate", Notification.Type.SUCCESS, "ElytraSwap", 1);
               mc.field_71442_b.func_187098_a(0, t, 0, ClickType.PICKUP, mc.field_71439_g);
               mc.field_71442_b.func_187098_a(0, 6, 0, ClickType.PICKUP, mc.field_71439_g);

               for(j = 9; j < 45; ++j) {
                  if (mc.field_71439_g.field_71071_by.func_70301_a(j).func_77973_b() == Items.field_190931_a) {
                     u = j;
                     break;
                  }
               }

               mc.field_71442_b.func_187098_a(0, u, 0, ClickType.PICKUP, mc.field_71439_g);
            }

            if (t == 0) {
               MessageBus.sendClientDeleteMessage("You do not have a Chestplate in your inventory. Keeping Elytra equipped", Notification.Type.ERROR, "ElytraSwap", 1);
            }

            this.disable();
         }

         if (body2.equals("Diamond Chestplate")) {
            t = 0;

            for(u = 9; u < 45; ++u) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(u).func_77973_b() == Items.field_185160_cR) {
                  t = u;
                  break;
               }
            }

            if (t != 0) {
               u = 0;
               MessageBus.sendClientDeleteMessage("Equipping Elytra", Notification.Type.SUCCESS, "ElytraSwap", 1);
               mc.field_71442_b.func_187098_a(0, t, 0, ClickType.PICKUP, mc.field_71439_g);
               mc.field_71442_b.func_187098_a(0, 6, 0, ClickType.PICKUP, mc.field_71439_g);

               for(j = 9; j < 45; ++j) {
                  if (mc.field_71439_g.field_71071_by.func_70301_a(j).func_77973_b() == Items.field_190931_a) {
                     u = j;
                     break;
                  }
               }

               mc.field_71442_b.func_187098_a(0, u, 0, ClickType.PICKUP, mc.field_71439_g);
            }

            if (t == 0) {
               MessageBus.sendClientDeleteMessage("You do not have a Elytra in your inventory. Keeping Chestplate equipped", Notification.Type.ERROR, "ElytraSwap", 1);
            }

            this.disable();
         }
      }

   }
}
