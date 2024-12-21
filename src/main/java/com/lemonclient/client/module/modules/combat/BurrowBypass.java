package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "BurrowBypass",
   category = Category.Combat
)
public class BurrowBypass extends Module {
   BooleanSetting multiPlace = this.registerBoolean("MultiPlace", false);
   BooleanSetting tpCenter = this.registerBoolean("TPCenter", false);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting packet = this.registerBoolean("Packet Place", true);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting strict = this.registerBoolean("Strict", true);
   BooleanSetting raytrace = this.registerBoolean("RayTrace", true);
   ModeSetting jumpMode = this.registerMode("JumpMode", Arrays.asList("Normal", "Future", "Strict"), "Normal");
   ModeSetting bypassMode = this.registerMode("Bypass", Arrays.asList("Normal", "Middle", "Test"), "Normal");
   ModeSetting rubberBand = this.registerMode("RubberBand", Arrays.asList("Cn", "Strict", "Future", "FutureStrict", "Troll", "Void", "Auto", "Test", "Custom"), "Cn");
   DoubleSetting offsetX = this.registerDouble("OffsetX", -7.0D, -10.0D, 10.0D, () -> {
      return ((String)this.rubberBand.getValue()).equals("Custom");
   });
   DoubleSetting offsetY = this.registerDouble("OffsetY", -7.0D, -10.0D, 10.0D, () -> {
      return ((String)this.rubberBand.getValue()).equals("Custom");
   });
   DoubleSetting offsetZ = this.registerDouble("OffsetZ", -7.0D, -10.0D, 10.0D, () -> {
      return ((String)this.rubberBand.getValue()).equals("Custom");
   });
   BooleanSetting head = this.registerBoolean("Head", true);
   BooleanSetting breakCrystal = this.registerBoolean("BreakCrystal", true);
   BooleanSetting onlyOnGround = this.registerBoolean("OnGround Only", true);
   BooleanSetting air = this.registerBoolean("NotAir", true);
   BooleanSetting antiWk = this.registerBoolean("AntiWeak", true, () -> {
      return (Boolean)this.breakCrystal.getValue();
   });
   ModeSetting mode = this.registerMode("BlockMode", Arrays.asList("Obsidian", "EChest", "ObbyEChest", "EChestObby"), "ObbyEChest");
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", false);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting testMode = this.registerBoolean("Test Mode", true);
   BooleanSetting move = this.registerBoolean("Move", true, () -> {
      return (Boolean)this.testMode.getValue();
   });
   boolean moved;
   Vec3d[] offsets = new Vec3d[]{new Vec3d(0.3D, 0.0D, 0.3D), new Vec3d(-0.3D, 0.0D, 0.3D), new Vec3d(0.3D, 0.0D, -0.3D), new Vec3d(-0.3D, 0.0D, -0.3D)};
   @EventHandler
   private final Listener<PlayerMoveEvent> playerMoveListener = new Listener((event) -> {
      if (mc.field_71439_g.func_70089_S() && !mc.field_71439_g.func_184613_cA() && !mc.field_71439_g.field_71075_bZ.field_75100_b) {
         if (!this.moved) {
            BlockPos blockPos = PlayerUtil.getPlayerPos();
            Vec3d[] var3 = new Vec3d[]{new Vec3d(0.4D, 0.0D, 0.4D), new Vec3d(0.4D, 0.0D, -0.4D), new Vec3d(-0.4D, 0.0D, 0.4D), new Vec3d(-0.4D, 0.0D, -0.4D)};
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Vec3d vec3d = var3[var5];
               BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t + vec3d.field_72450_a, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + vec3d.field_72449_c);
               if (BlockUtil.isAir(pos.func_177977_b()) && mc.field_71441_e.func_175623_d(pos) && mc.field_71441_e.func_175623_d(pos.func_177984_a()) && mc.field_71441_e.func_175623_d(pos.func_177981_b(2))) {
                  blockPos = pos;
                  break;
               }
            }

            double x = this.roundToClosest(mc.field_71439_g.field_70165_t, (double)blockPos.field_177962_a + 0.02D, (double)blockPos.field_177962_a + 0.98D);
            double y = mc.field_71439_g.field_70163_u;
            double z = this.roundToClosest(mc.field_71439_g.field_70161_v, (double)blockPos.field_177961_c + 0.02D, (double)blockPos.field_177961_c + 0.98D);
            Vec3d playerPos = mc.field_71439_g.func_174791_d();
            double yawRad = Math.toRadians((double)RotationUtil.getRotationTo(playerPos, new Vec3d(x, y, z)).field_189982_i);
            double dist = Math.hypot(x - playerPos.field_72450_a, z - playerPos.field_72449_c);
            if (x - playerPos.field_72450_a == 0.0D && z - playerPos.field_72449_c == 0.0D) {
               this.moved = true;
            }

            double playerSpeed = MotionUtil.getBaseMoveSpeed() * (EntityUtil.isColliding(0.0D, -0.5D, 0.0D) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.91D : 1.0D);
            double speed = Math.min(dist, playerSpeed);
            event.setX(-Math.sin(yawRad) * speed);
            event.setZ(Math.cos(yawRad) * speed);
            if (LemonClient.speedUtil.getPlayerSpeed(mc.field_71439_g) == 0.0D) {
               this.moved = true;
            }
         }

      }
   }, new Predicate[0]);

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

   public void breakCrystal() {
      AxisAlignedBB axisAlignedBB = new AxisAlignedBB(getFlooredPosition(mc.field_71439_g));
      List<Entity> l = mc.field_71441_e.func_72839_b((Entity)null, axisAlignedBB);
      Iterator var3 = l.iterator();

      while(true) {
         while(true) {
            Entity entity;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               entity = (Entity)var3.next();
            } while(!(entity instanceof EntityEnderCrystal));

            if ((Boolean)this.antiWk.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
               int newSlot = -1;

               int i;
               for(i = 0; i < 9; ++i) {
                  ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
                  if (stack != ItemStack.field_190927_a) {
                     if (stack.func_77973_b() instanceof ItemSword) {
                        newSlot = i;
                        break;
                     }

                     if (stack.func_77973_b() instanceof ItemTool) {
                        newSlot = i;
                        break;
                     }
                  }
               }

               i = mc.field_71439_g.field_71071_by.field_70461_c;
               if (newSlot != -1) {
                  this.switchTo(newSlot);
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(entity));
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.OFF_HAND));
               this.switchTo(i);
            } else {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(entity));
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.OFF_HAND));
            }
         }
      }
   }

   public static void back() {
      Iterator var0 = ((List)mc.field_71441_e.field_72996_f.stream().filter((e) -> {
         return e instanceof EntityEnderCrystal && !e.field_70128_L;
      }).sorted(Comparator.comparing((e) -> {
         return mc.field_71439_g.func_70032_d(e);
      })).collect(Collectors.toList())).iterator();

      while(var0.hasNext()) {
         Entity crystal = (Entity)var0.next();
         if (crystal instanceof EntityEnderCrystal) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(crystal));
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.OFF_HAND));
         }
      }

   }

   private double roundToClosest(double num, double low, double high) {
      double d2 = high - num;
      double d1 = num - low;
      return d2 > d1 ? low : high;
   }

   private boolean canGoTo(BlockPos pos) {
      return isAir(pos) && isAir(pos.func_177984_a());
   }

   public void onEnable() {
      this.moved = !(Boolean)this.move.getValue();
      if ((Boolean)this.onlyOnGround.getValue() && !mc.field_71439_g.field_70122_E) {
         this.disable();
      } else {
         if ((Boolean)this.air.getValue() && mc.field_71441_e.func_180495_p(getFlooredPosition(mc.field_71439_g).func_177972_a(EnumFacing.DOWN)).func_177230_c().equals(Blocks.field_150350_a)) {
            this.disable();
         }

      }
   }

   public void onUpdate() {
      BlockPos playerPos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.5D, mc.field_71439_g.field_70161_v);
      Vec3d vecPos = new Vec3d(mc.field_71439_g.field_70165_t, (double)((int)(mc.field_71439_g.field_70163_u + 0.5D)), mc.field_71439_g.field_70161_v);
      int a = mc.field_71439_g.field_71071_by.field_70461_c;
      int slot = -1;
      String var5 = (String)this.mode.getValue();
      byte var6 = -1;
      switch(var5.hashCode()) {
      case -2100723386:
         if (var5.equals("EChestObby")) {
            var6 = 2;
         }
         break;
      case 416515707:
         if (var5.equals("Obsidian")) {
            var6 = 0;
         }
         break;
      case 1669394374:
         if (var5.equals("ObbyEChest")) {
            var6 = 3;
         }
         break;
      case 2040486332:
         if (var5.equals("EChest")) {
            var6 = 1;
         }
      }

      switch(var6) {
      case 0:
         slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         break;
      case 1:
         slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
         break;
      case 2:
         slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
         if (slot == -1) {
            slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         }
         break;
      case 3:
         slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         if (slot == -1) {
            slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
         }
      }

      if (slot == -1) {
         this.disable();
      } else {
         int var7;
         int i;
         Vec3d vec3d;
         boolean bypassed;
         Vec3d[] var21;
         if ((Boolean)this.testMode.getValue()) {
            if (!this.moved) {
               return;
            }

            bypassed = false;
            var21 = this.offsets;
            var7 = var21.length;

            for(i = 0; i < var7; ++i) {
               vec3d = var21[i];
               if (!this.isPos2(new BlockPos(vecPos.func_178787_e(vec3d)), playerPos)) {
                  bypassed = true;
                  break;
               }
            }

            if (!bypassed) {
               this.disable();
               return;
            }
         }

         if ((Boolean)this.breakCrystal.getValue()) {
            back();
         }

         if (mc.field_71441_e.func_175667_e(mc.field_71439_g.func_180425_c()) && !mc.field_71439_g.func_180799_ab() && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.field_70134_J) {
            if ((Boolean)this.tpCenter.getValue()) {
               PlayerUtil.centerPlayer();
            }

            bypassed = false;
            BlockPos pos;
            byte var20;
            String var23;
            if (!this.fakeBBoxCheck()) {
               if ((!(Boolean)this.testMode.getValue() || this.bypassBurrowed()) && (BlockUtil.canReplace(playerPos) && BlockUtil.canReplace(playerPos.func_177984_a()) || !this.intersect(playerPos.func_177984_a()))) {
                  List<BlockPos> posList = new ArrayList();
                  List<BlockPos> airList = new ArrayList();
                  int var10;
                  Vec3d vec;
                  Vec3d[] var24;
                  int var25;
                  if ((Boolean)this.testMode.getValue()) {
                     airList.add(playerPos);
                     var24 = this.offsets;
                     var25 = var24.length;

                     for(var10 = 0; var10 < var25; ++var10) {
                        vec = var24[var10];
                        pos = new BlockPos(vecPos.func_178787_e(vec));
                        if (!BlockUtil.isAir(pos.func_177984_a())) {
                           posList.add(pos.func_177984_a());
                        }
                     }
                  } else {
                     var24 = this.offsets;
                     var25 = var24.length;

                     for(var10 = 0; var10 < var25; ++var10) {
                        vec = var24[var10];
                        boolean air = true;
                        BlockPos pos = new BlockPos(vecPos.func_178787_e(vec));

                        for(int i = 0; i < 2; ++i) {
                           BlockPos blockPos = pos.func_177981_b(i);
                           if (!isAir(blockPos)) {
                              air = false;
                           }
                        }

                        if (this.intersect(pos) && !air) {
                           posList.add(pos);
                        } else {
                           airList.add(pos);
                        }
                     }
                  }

                  BlockPos movePos = posList.isEmpty() ? (BlockPos)airList.stream().min(Comparator.comparing((p) -> {
                     return mc.field_71439_g.func_70011_f((double)p.field_177962_a + 0.5D, mc.field_71439_g.field_70163_u, (double)p.field_177961_c + 0.5D);
                  })).orElse((Object)null) : (BlockPos)posList.stream().min(Comparator.comparing((p) -> {
                     return mc.field_71439_g.func_70011_f((double)p.field_177962_a + 0.5D, mc.field_71439_g.field_70163_u, (double)p.field_177961_c + 0.5D);
                  })).orElse((Object)null);
                  this.gotoPos(movePos);
               } else {
                  this.gotoPos(playerPos);
               }

               bypassed = true;
            } else {
               var23 = (String)this.jumpMode.getValue();
               var20 = -1;
               switch(var23.hashCode()) {
               case -1955878649:
                  if (var23.equals("Normal")) {
                     var20 = 0;
                  }
                  break;
               case -1808119063:
                  if (var23.equals("Strict")) {
                     var20 = 2;
                  }
                  break;
               case 2115664355:
                  if (var23.equals("Future")) {
                     var20 = 1;
                  }
               }

               switch(var20) {
               case 0:
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419999986886978D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7531999805212015D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.001335979112147D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.166109260938214D, mc.field_71439_g.field_70161_v, false));
                  break;
               case 1:
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419997486886978D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7500025D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.999995D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.170005001788139D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.2426050013947485D, mc.field_71439_g.field_70161_v, false));
                  break;
               case 2:
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419998586886978D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7500014D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.9999972D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.170002801788139D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.170009801788139D, mc.field_71439_g.field_70161_v, false));
               }
            }

            this.switchTo(slot);
            if (!(Boolean)this.multiPlace.getValue()) {
               this.placeBlock(new BlockPos(this.getPlayerPosFixY(mc.field_71439_g)));
            } else {
               var21 = this.offsets;
               var7 = var21.length;

               for(i = 0; i < var7; ++i) {
                  vec3d = var21[i];
                  this.placeBlock(vecPos.func_178787_e(vec3d));
               }

               if ((Boolean)this.head.getValue() && bypassed) {
                  var21 = this.offsets;
                  var7 = var21.length;

                  for(i = 0; i < var7; ++i) {
                     vec3d = var21[i];
                     this.placeBlock(vecPos.func_178787_e(vec3d).func_72441_c(0.0D, 1.0D, 0.0D));
                  }
               }
            }

            this.switchTo(a);
            var23 = (String)this.rubberBand.getValue();
            var20 = -1;
            switch(var23.hashCode()) {
            case -1808119063:
               if (var23.equals("Strict")) {
                  var20 = 4;
               }
               break;
            case 2187:
               if (var23.equals("Cn")) {
                  var20 = 0;
               }
               break;
            case 2052559:
               if (var23.equals("Auto")) {
                  var20 = 6;
               }
               break;
            case 2672052:
               if (var23.equals("Void")) {
                  var20 = 5;
               }
               break;
            case 81082065:
               if (var23.equals("Troll")) {
                  var20 = 3;
               }
               break;
            case 1358728844:
               if (var23.equals("FutureStrict")) {
                  var20 = 2;
               }
               break;
            case 2029746065:
               if (var23.equals("Custom")) {
                  var20 = 7;
               }
               break;
            case 2115664355:
               if (var23.equals("Future")) {
                  var20 = 1;
               }
            }

            double distance;
            BlockPos bestPos;
            label244:
            switch(var20) {
            case 0:
               distance = 0.0D;
               bestPos = null;
               Iterator var31 = BlockUtil.getBox(6.0F).iterator();

               while(true) {
                  do {
                     do {
                        do {
                           if (!var31.hasNext()) {
                              if (bestPos != null) {
                                 mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)bestPos.func_177958_n() + 0.5D, (double)bestPos.func_177956_o(), (double)bestPos.func_177952_p() + 0.5D, false));
                              } else {
                                 mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, -7.0D, mc.field_71439_g.field_70161_v, false));
                              }
                              break label244;
                           }

                           pos = (BlockPos)var31.next();
                        } while(!this.canGoTo(pos));
                     } while(mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) <= 3.0D);
                  } while(bestPos != null && mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) >= distance);

                  bestPos = pos;
                  distance = mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D);
               }
            case 1:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.242609801394749D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.340028003576279D, mc.field_71439_g.field_70161_v, false));
               break;
            case 2:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.315205001001358D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.315205001001358D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.485225002789497D, mc.field_71439_g.field_70161_v, false));
               break;
            case 3:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 3.3400880035762786D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 1.0D, mc.field_71439_g.field_70161_v, false));
               break;
            case 4:
               distance = 0.0D;
               bestPos = null;

               for(int i = 0; i < 20; ++i) {
                  pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.5D + (double)i, mc.field_71439_g.field_70161_v);
                  if (this.canGoTo(pos) && mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) > 5.0D && (bestPos == null || mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) < distance)) {
                     bestPos = pos;
                     distance = mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D);
                  }
               }

               if (bestPos != null) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)bestPos.func_177958_n() + 0.5D, (double)bestPos.func_177956_o(), (double)bestPos.func_177952_p() + 0.5D, false));
               } else {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, -7.0D, mc.field_71439_g.field_70161_v, false));
               }
               break;
            case 5:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, -7.0D, mc.field_71439_g.field_70161_v, false));
               break;
            case 6:
               i = -10;

               while(true) {
                  if (i >= 10) {
                     break label244;
                  }

                  if (i == -1) {
                     i = 4;
                  }

                  if (mc.field_71441_e.func_180495_p(getFlooredPosition(mc.field_71439_g).func_177982_a(0, i, 0)).func_177230_c().equals(Blocks.field_150350_a) && mc.field_71441_e.func_180495_p(getFlooredPosition(mc.field_71439_g).func_177982_a(0, i + 1, 0)).func_177230_c().equals(Blocks.field_150350_a)) {
                     BlockPos pos = getFlooredPosition(mc.field_71439_g).func_177982_a(0, i, 0);
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)pos.func_177958_n() + 0.3D, (double)pos.func_177956_o(), (double)pos.func_177952_p() + 0.3D, false));
                     break label244;
                  }

                  ++i;
               }
            case 7:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + (Double)this.offsetX.getValue(), mc.field_71439_g.field_70163_u + (Double)this.offsetY.getValue(), mc.field_71439_g.field_70161_v + (Double)this.offsetZ.getValue(), false));
            }

            this.disable();
         } else {
            this.disable();
         }
      }
   }

   private void gotoPos(BlockPos pos) {
      String var2 = (String)this.bypassMode.getValue();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1990474315:
         if (var2.equals("Middle")) {
            var3 = 1;
         }
         break;
      case -1955878649:
         if (var2.equals("Normal")) {
            var3 = 0;
         }
         break;
      case 2603186:
         if (var2.equals("Test")) {
            var3 = 2;
         }
      }

      switch(var3) {
      case 0:
         if (Math.abs((double)pos.func_177958_n() + 0.5D - mc.field_71439_g.field_70165_t) < Math.abs((double)pos.func_177952_p() + 0.5D - mc.field_71439_g.field_70161_v)) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.2D, (double)pos.func_177952_p() + 0.5D, true));
         } else {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)pos.func_177958_n() + 0.5D, mc.field_71439_g.field_70163_u + 0.2D, mc.field_71439_g.field_70161_v, true));
         }
         break;
      case 1:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)pos.func_177958_n() + 0.5D, mc.field_71439_g.field_70163_u + 0.2D, (double)pos.func_177952_p() + 0.5D, true));
         break;
      case 2:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + ((double)pos.func_177958_n() + 0.5D - mc.field_71439_g.field_70165_t) * 0.42132D, mc.field_71439_g.field_70163_u + 0.12160004615784D, mc.field_71439_g.field_70161_v + ((double)pos.func_177952_p() + 0.5D - mc.field_71439_g.field_70161_v) * 0.42132D, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + ((double)pos.func_177958_n() + 0.5D - mc.field_71439_g.field_70165_t) * 0.95D, mc.field_71439_g.field_70163_u + 0.200000047683716D, mc.field_71439_g.field_70161_v + ((double)pos.func_177952_p() + 0.5D - mc.field_71439_g.field_70161_v) * 0.95D, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + ((double)pos.func_177958_n() + 0.5D - mc.field_71439_g.field_70165_t) * 1.03D, mc.field_71439_g.field_70163_u + 0.200000047683716D, mc.field_71439_g.field_70161_v + ((double)pos.func_177952_p() + 0.5D - mc.field_71439_g.field_70161_v) * 1.03D, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + ((double)pos.func_177958_n() + 0.5D - mc.field_71439_g.field_70165_t) * 1.0933D, mc.field_71439_g.field_70163_u + 0.12160004615784D, mc.field_71439_g.field_70161_v + ((double)pos.func_177952_p() + 0.5D - mc.field_71439_g.field_70161_v) * 1.0933D, false));
      }

   }

   private boolean intersect(BlockPos pos) {
      return mc.field_71439_g.field_70121_D.func_72326_a(mc.field_71441_e.func_180495_p(pos).func_185918_c(mc.field_71441_e, pos));
   }

   public static BlockPos getFlooredPosition(Entity entity) {
      return new BlockPos(Math.floor(entity.field_70165_t), (double)Math.round(entity.field_70163_u), Math.floor(entity.field_70161_v));
   }

   private boolean fakeBBoxCheck() {
      Vec3d playerPos = mc.field_71439_g.func_174791_d();
      playerPos = new Vec3d(playerPos.field_72450_a, (double)((int)(playerPos.field_72448_b + 0.5D)), playerPos.field_72449_c);
      Vec3d[] var2 = this.offsets;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Vec3d vec = var2[var4];

         for(int i = 0; i < 3; ++i) {
            BlockPos pos = new BlockPos(playerPos.func_178787_e(vec).func_72441_c(0.0D, (double)i, 0.0D));
            if ((i >= 2 || this.intersect(pos)) && !isAir(pos)) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean isAir(Vec3d vec3d) {
      return isAir(new BlockPos(vec3d));
   }

   public static boolean isAir(BlockPos pos) {
      return BlockUtil.canReplace(pos);
   }

   private void placeBlock(BlockPos pos) {
      BlockUtil.placeBlockBoolean(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.swing.getValue());
   }

   public static Vec3d getEyesPos() {
      return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   private void placeBlock(Vec3d vec3d) {
      BlockPos pos = new BlockPos(vec3d);
      if (!(Boolean)this.testMode.getValue() || this.bypassBurrowed() && (Boolean)this.head.getValue() || !this.isPos2(pos, PlayerUtil.getPlayerPos())) {
         this.placeBlock(pos);
      }
   }

   private BlockPos getPlayerPosFixY(EntityPlayer player) {
      return new BlockPos(Math.floor(player.field_70165_t), (double)Math.round(player.field_70163_u), Math.floor(player.field_70161_v));
   }

   private boolean bypassBurrowed() {
      Vec3d pos = new Vec3d(mc.field_71439_g.field_70165_t, (double)((int)(mc.field_71439_g.field_70163_u + 0.5D)), mc.field_71439_g.field_70161_v);
      Vec3d[] var2 = this.offsets;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Vec3d vec3d = var2[var4];
         if (!BlockUtil.isAir((new BlockPos(pos.func_178787_e(vec3d))).func_177984_a())) {
            return true;
         }
      }

      return false;
   }
}
