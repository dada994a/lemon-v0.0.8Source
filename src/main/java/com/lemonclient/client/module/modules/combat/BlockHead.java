package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.dev.PistonAura;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "BlockHead",
   category = Category.Combat
)
public class BlockHead extends Module {
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 20);
   DoubleSetting range = this.registerDouble("Range", 5.0D, 0.0D, 10.0D);
   IntegerSetting maxTarget = this.registerInteger("Max Target", 1, 1, 10);
   DoubleSetting maxSpeed = this.registerDouble("Max Target Speed", 10.0D, 0.0D, 50.0D);
   IntegerSetting bpt = this.registerInteger("BlocksPerTick", 4, 0, 20);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting packet = this.registerBoolean("Packet Place", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting pause = this.registerBoolean("BedrockHole", true);
   int ob;
   int waited;
   int placed;
   BlockPos[] block = new BlockPos[]{new BlockPos(0, 0, 0), new BlockPos(0, 1, 0)};
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};

   public static boolean isPlayerInHole(EntityPlayer target) {
      BlockPos blockPos = getLocalPlayerPosFloored(target);
      HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(blockPos, true, true, false);
      HoleUtil.HoleType holeType = holeInfo.getType();
      return holeType == HoleUtil.HoleType.SINGLE;
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
            mc.field_71442_b.func_78765_e();
         }
      }

   }

   public static BlockPos getLocalPlayerPosFloored(EntityPlayer target) {
      return new BlockPos(target.func_174791_d());
   }

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(entity instanceof EntityItem || entity instanceof EntityArmorStand || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.placed = 0;
         if (this.waited++ >= (Integer)this.delay.getValue()) {
            this.waited = 0;
            this.ob = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            if (this.ob != -1) {
               int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
               Iterator var2 = PlayerUtil.getNearPlayers((Double)this.range.getValue(), (Integer)this.maxTarget.getValue()).iterator();

               while(true) {
                  BlockPos pos;
                  label140:
                  while(true) {
                     do {
                        int bedrock;
                        do {
                           do {
                              EntityPlayer target;
                              do {
                                 do {
                                    do {
                                       do {
                                          if (!var2.hasNext()) {
                                             return;
                                          }

                                          target = (EntityPlayer)var2.next();
                                       } while(target == null);
                                    } while(EntityUtil.isDead(target));
                                 } while(LemonClient.speedUtil.getPlayerSpeed(target) > (Double)this.maxSpeed.getValue());
                              } while(!isPlayerInHole(target));

                              pos = new BlockPos(target.field_70165_t, target.field_70163_u + 0.5D, target.field_70161_v);
                              bedrock = 0;
                              BlockPos[] var6 = this.sides;
                              int var7 = var6.length;

                              for(int var8 = 0; var8 < var7; ++var8) {
                                 BlockPos side = var6[var8];
                                 if (mc.field_71441_e.func_180495_p(pos.func_177971_a(side)).func_177230_c() == Blocks.field_150357_h) {
                                    ++bedrock;
                                 }
                              }
                           } while(bedrock >= 4 && !(Boolean)this.pause.getValue());
                        } while(!mc.field_71441_e.func_175623_d(pos.func_177981_b(2)));
                     } while(this.intersectsWithEntity(pos.func_177981_b(2)));

                     if (BurrowUtil.getFirstFacing(pos.func_177981_b(2)) != null) {
                        break;
                     }

                     List<BlockPos> posList = new ArrayList();
                     List<BlockPos> list = new ArrayList();
                     BlockPos[] var16 = this.sides;
                     int var17 = var16.length;

                     int var10;
                     BlockPos side;
                     BlockPos add;
                     for(var10 = 0; var10 < var17; ++var10) {
                        side = var16[var10];
                        add = pos.func_177971_a(side);
                        if (!PistonAura.INSTANCE.canPistonCrystal(add, pos)) {
                           posList.add(add);
                        }

                        list.add(add);
                     }

                     if (posList.isEmpty()) {
                        var16 = this.sides;
                        var17 = var16.length;

                        for(var10 = 0; var10 < var17; ++var10) {
                           side = var16[var10];
                           add = pos.func_177971_a(side);
                           if (!PistonAura.INSTANCE.canPistonCrystal(add.func_177984_a(), pos)) {
                              posList.add(add);
                           }

                           list.add(add);
                        }
                     }

                     if (posList.isEmpty()) {
                        posList.addAll(list);
                     }

                     BlockPos side = (BlockPos)posList.stream().max(Comparator.comparing(PlayerUtil::getDistance)).orElse((Object)null);
                     if (side != null) {
                        BlockPos[] var19 = this.block;
                        var10 = var19.length;
                        int var20 = 0;

                        while(true) {
                           if (var20 >= var10) {
                              break label140;
                           }

                           add = var19[var20];
                           if (this.placed > (Integer)this.bpt.getValue()) {
                              return;
                           }

                           BlockPos obsi = side.func_177984_a().func_177971_a(add);
                           if (!this.intersectsWithEntity(obsi) && BlockUtil.canReplace(obsi)) {
                              this.switchTo(this.ob);
                              BurrowUtil.placeBlock(obsi, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                              this.switchTo(oldSlot);
                              ++this.placed;
                           }

                           ++var20;
                        }
                     }
                  }

                  if (this.placed > (Integer)this.bpt.getValue()) {
                     return;
                  }

                  this.switchTo(this.ob);
                  BurrowUtil.placeBlock(pos.func_177981_b(2), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  this.switchTo(oldSlot);
                  ++this.placed;
               }
            }
         }
      }
   }
}
