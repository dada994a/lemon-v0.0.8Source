package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AntiContainer",
   category = Category.Misc
)
public class AntiContainer extends Module {
   BooleanSetting Chest = this.registerBoolean("Chest", true);
   BooleanSetting EnderChest = this.registerBoolean("EnderChest", true);
   BooleanSetting Trapped_Chest = this.registerBoolean("Trapped_Chest", true);
   BooleanSetting Hopper = this.registerBoolean("Hopper", true);
   BooleanSetting Dispenser = this.registerBoolean("Dispenser", true);
   BooleanSetting Furnace = this.registerBoolean("Furnace", true);
   BooleanSetting Beacon = this.registerBoolean("Beacon", true);
   BooleanSetting Crafting_Table = this.registerBoolean("Crafting_Table", true);
   BooleanSetting Anvil = this.registerBoolean("Anvil", true);
   BooleanSetting Enchanting_table = this.registerBoolean("Enchanting_table", true);
   BooleanSetting Brewing_Stand = this.registerBoolean("Brewing_Stand", true);
   BooleanSetting ShulkerBox = this.registerBoolean("ShulkerBox", true);
   @EventHandler
   private final Listener<PacketEvent.Send> listener = new Listener((event) -> {
      if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
         BlockPos pos = ((CPacketPlayerTryUseItemOnBlock)event.getPacket()).func_187023_a();
         if (this.check(pos)) {
            event.cancel();
         }
      }

   }, new Predicate[0]);

   public boolean check(BlockPos pos) {
      return mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150486_ae && (Boolean)this.Chest.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150477_bB && (Boolean)this.EnderChest.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150447_bR && (Boolean)this.Trapped_Chest.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150438_bZ && (Boolean)this.Hopper.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150367_z && (Boolean)this.Dispenser.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150460_al && (Boolean)this.Furnace.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150461_bJ && (Boolean)this.Beacon.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150462_ai && (Boolean)this.Crafting_Table.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150467_bQ && (Boolean)this.Anvil.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150381_bn && (Boolean)this.Enchanting_table.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150382_bo && (Boolean)this.Brewing_Stand.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockShulkerBox && (Boolean)this.ShulkerBox.getValue();
   }
}
