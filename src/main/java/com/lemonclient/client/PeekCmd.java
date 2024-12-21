package com.lemonclient.client;

import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.misc.ShulkerBypass;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(
   modid = "peek",
   name = "PeekBypass",
   version = "1",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class PeekCmd {
   public static int metadataTicks = -1;
   public static int guiTicks = -1;
   public static ItemStack shulker;
   public static EntityItem drop;
   public static InventoryBasic toOpen;
   public static Minecraft mc;

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      ClientCommandHandler.instance.func_71560_a(new PeekCmd.PeekCommand());
   }

   public static NBTTagCompound getShulkerNBT(ItemStack stack) {
      if (mc.field_71439_g == null) {
         return null;
      } else {
         NBTTagCompound compound = stack.func_77978_p();
         if (compound != null && compound.func_150297_b("BlockEntityTag", 10)) {
            NBTTagCompound tags = compound.func_74775_l("BlockEntityTag");
            if (ModuleManager.getModule("Peek").isEnabled() && ShulkerBypass.shulkers) {
               if (tags.func_150297_b("Items", 9)) {
                  return tags;
               }

               MessageBus.sendMessage("Shulker is empty.", Notification.Type.INFO, "Peek", 3, ShulkerBypass.notification);
            }
         }

         return null;
      }
   }

   static {
      shulker = ItemStack.field_190927_a;
      mc = Minecraft.func_71410_x();
   }

   public static class PeekCommand extends CommandBase implements IClientCommand {
      public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
         return false;
      }

      public String func_71517_b() {
         return "peek";
      }

      public String func_71518_a(ICommandSender sender) {
         return null;
      }

      public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) {
         if (PeekCmd.mc.field_71439_g != null && ModuleManager.getModule("Peek").isEnabled() && ShulkerBypass.shulkers) {
            if (!PeekCmd.shulker.func_190926_b()) {
               NBTTagCompound shulkerNBT = PeekCmd.getShulkerNBT(PeekCmd.shulker);
               if (shulkerNBT != null) {
                  TileEntityShulkerBox fakeShulker = new TileEntityShulkerBox();
                  fakeShulker.func_190586_e(shulkerNBT);
                  String customName = "container.shulkerBox";
                  boolean hasCustomName = false;
                  if (shulkerNBT.func_150297_b("CustomName", 8)) {
                     customName = shulkerNBT.func_74779_i("CustomName");
                     hasCustomName = true;
                  }

                  InventoryBasic inv = new InventoryBasic(customName, hasCustomName, 27);

                  for(int i = 0; i < 27; ++i) {
                     inv.func_70299_a(i, fakeShulker.func_70301_a(i));
                  }

                  PeekCmd.toOpen = inv;
                  PeekCmd.guiTicks = 0;
               }
            } else {
               MessageBus.sendMessage("No shulker detected! please drop and pickup your shulker.", Notification.Type.ERROR, "Peek", 3, ShulkerBypass.notification);
            }
         }

      }

      public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
         return true;
      }
   }
}
