package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Iterator;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AutoConcrete",
   category = Category.Dev
)
public class AutoConcrete extends Module {
   DoubleSetting range = this.registerDouble("Range", 5.5D, 0.0D, 10.0D);
   BooleanSetting packet = this.registerBoolean("Packet Place", true);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting air = this.registerBoolean("Air Check", true);
   BooleanSetting disable = this.registerBoolean("Disable", true);
   IntegerSetting delay = this.registerInteger("Delay", 5, 0, 100, () -> {
      return !(Boolean)this.disable.getValue();
   });
   DoubleSetting maxTargetSpeed = this.registerDouble("Max Target Speed", 10.0D, 0.0D, 50.0D);
   int waited;
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};

   private void switchTo(int slot, Runnable runnable) {
      int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (slot >= 0 && slot != oldslot) {
         if (slot < 9) {
            boolean packetSwitch = (Boolean)this.packetSwitch.getValue();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }

            runnable.run();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
            }
         }

      } else {
         runnable.run();
      }
   }

   public void onEnable() {
      this.waited = 100;
   }

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (this.waited++ >= (Integer)this.delay.getValue()) {
            this.waited = 0;
            int slot = BurrowUtil.findHotbarBlock(BlockAnvil.class);
            if (slot == -1) {
               slot = BurrowUtil.findHotbarBlock(BlockConcretePowder.class);
               if (slot == -1) {
                  return;
               }
            }

            EntityPlayer player = PlayerUtil.getNearestPlayer((Double)this.range.getValue());
            if (!(LemonClient.speedUtil.getPlayerSpeed(player) > (Double)this.maxTargetSpeed.getValue())) {
               if (player == null) {
                  if ((Boolean)this.disable.getValue()) {
                     this.disable();
                  }

               } else {
                  BlockPos pos = new BlockPos(player.field_70165_t, player.field_70163_u, player.field_70161_v);
                  if (!BlockUtil.airBlocks.contains(mc.field_71441_e.func_180495_p(pos).func_177230_c()) && (Boolean)this.air.getValue()) {
                     if ((Boolean)this.disable.getValue()) {
                        this.disable();
                     }

                  } else {
                     BlockPos placePos = pos.func_177981_b(2);
                     if (!this.intersectsWithEntity(placePos)) {
                        if (BurrowUtil.getFirstFacing(placePos) == null) {
                           int obby = BurrowUtil.findHotbarBlock(BlockObsidian.class);
                           if (obby == -1) {
                              return;
                           }

                           boolean helped = false;
                           BlockPos[] var7 = this.sides;
                           int var8 = var7.length;

                           for(int var9 = 0; var9 < var8; ++var9) {
                              BlockPos side = var7[var9];
                              BlockPos helpingBlock = placePos.func_177971_a(side);
                              if (!this.intersectsWithEntity(helpingBlock)) {
                                 if (BurrowUtil.getFirstFacing(helpingBlock) != null) {
                                    this.switchTo(obby, () -> {
                                       BurrowUtil.placeBlock(helpingBlock, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                                    });
                                    helped = true;
                                    break;
                                 }

                                 if (!this.intersectsWithEntity(helpingBlock.func_177977_b())) {
                                    if (BurrowUtil.getFirstFacing(helpingBlock.func_177977_b()) != null) {
                                       this.switchTo(obby, () -> {
                                          BurrowUtil.placeBlock(helpingBlock.func_177977_b(), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                                          BurrowUtil.placeBlock(helpingBlock, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                                       });
                                       helped = true;
                                       break;
                                    }

                                    if (!this.intersectsWithEntity(helpingBlock.func_177979_c(2)) && BurrowUtil.getFirstFacing(helpingBlock.func_177979_c(2)) != null) {
                                       this.switchTo(obby, () -> {
                                          BurrowUtil.placeBlock(helpingBlock.func_177979_c(2), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                                          BurrowUtil.placeBlock(helpingBlock.func_177977_b(), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                                          BurrowUtil.placeBlock(helpingBlock, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                                       });
                                       helped = true;
                                       break;
                                    }
                                 }
                              }
                           }

                           if (!helped) {
                              return;
                           }
                        }

                        this.switchTo(slot, () -> {
                           BurrowUtil.placeBlock(placePos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                        });
                        if ((Boolean)this.disable.getValue()) {
                           this.disable();
                        }

                     }
                  }
               }
            }
         }
      } else {
         if ((Boolean)this.disable.getValue()) {
            this.disable();
         }

      }
   }

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(entity instanceof EntityItem || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }
}
