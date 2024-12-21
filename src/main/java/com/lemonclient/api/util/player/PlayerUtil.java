package com.lemonclient.api.util.player;

import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static void setPosition(double x, double y, double z) {
      mc.field_71439_g.func_70107_b(x, y, z);
   }

   public static void setPosition(BlockPos pos) {
      mc.field_71439_g.func_70107_b((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o(), (double)pos.func_177952_p() + 0.5D);
   }

   public static Vec3d getMotionVector() {
      return new Vec3d(mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70181_x, mc.field_71439_g.field_70179_y);
   }

   public static void vClip(double d) {
      mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + d, mc.field_71439_g.field_70161_v);
   }

   public static void move(double x, double y, double z) {
      mc.field_71439_g.func_70091_d(MoverType.SELF, x, y, z);
   }

   public static void setMotionVector(Vec3d vec) {
      mc.field_71439_g.field_70159_w = vec.field_72450_a;
      mc.field_71439_g.field_70181_x = vec.field_72448_b;
      mc.field_71439_g.field_70179_y = vec.field_72449_c;
   }

   public static boolean isInsideBlock() {
      try {
         AxisAlignedBB playerBoundingBox = mc.field_71439_g.func_174813_aQ();

         for(int x = MathHelper.func_76128_c(playerBoundingBox.field_72340_a); x < MathHelper.func_76128_c(playerBoundingBox.field_72336_d) + 1; ++x) {
            for(int y = MathHelper.func_76128_c(playerBoundingBox.field_72338_b); y < MathHelper.func_76128_c(playerBoundingBox.field_72337_e) + 1; ++y) {
               for(int z = MathHelper.func_76128_c(playerBoundingBox.field_72339_c); z < MathHelper.func_76128_c(playerBoundingBox.field_72334_f) + 1; ++z) {
                  Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                  if (!(block instanceof BlockAir)) {
                     AxisAlignedBB boundingBox = ((AxisAlignedBB)Objects.requireNonNull(block.func_180646_a(mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)), mc.field_71441_e, new BlockPos(x, y, z)))).func_72317_d((double)x, (double)y, (double)z);
                     if (block instanceof BlockHopper) {
                        boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
                     }

                     if (playerBoundingBox.func_72326_a(boundingBox)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } catch (Exception var6) {
         return false;
      }
   }

   public static BlockPos getPlayerPos() {
      return new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u + 0.5D), Math.floor(mc.field_71439_g.field_70161_v));
   }

   public static BlockPos getPlayerFloorPos() {
      return new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
   }

   public static boolean isPlayerClipped() {
      return !mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ()).isEmpty();
   }

   public static void fakeJump() {
      fakeJump(5);
   }

   public static void fakeJump(int packets) {
      if (packets > 0 && packets != 5) {
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, true));
      }

      if (packets > 1) {
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419999986887D, mc.field_71439_g.field_70161_v, true));
      }

      if (packets > 2) {
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7531999805212D, mc.field_71439_g.field_70161_v, true));
      }

      if (packets > 3) {
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0013359791121D, mc.field_71439_g.field_70161_v, true));
      }

      if (packets > 4) {
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.1661092609382D, mc.field_71439_g.field_70161_v, true));
      }

   }

   public static double getDistance(Entity entity) {
      return (double)mc.field_71439_g.func_70032_d(entity);
   }

   public static double getDistance(BlockPos pos) {
      return mc.field_71439_g.func_70011_f((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
   }

   public static double getDistanceI(BlockPos pos) {
      return getEyeVec().func_72438_d(new Vec3d((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D));
   }

   public static double getDistanceL(BlockPos pos) {
      double x = (double)pos.field_177962_a - mc.field_71439_g.field_70165_t;
      double z = (double)pos.field_177961_c - mc.field_71439_g.field_70161_v;
      return Math.hypot(x, z);
   }

   public static BlockPos getEyesPos() {
      return new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
   }

   public static Vec3d getEyeVec() {
      return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
   }

   public static EntityPlayer getNearestPlayer(double range) {
      List<EntityPlayer> playerList = (List)mc.field_71441_e.field_73010_i.stream().filter((p) -> {
         return (double)mc.field_71439_g.func_70032_d(p) <= range;
      }).filter((p) -> {
         return !EntityUtil.basicChecksEntity(p);
      }).filter((p) -> {
         return mc.field_71439_g.field_145783_c != p.field_145783_c;
      }).filter((p) -> {
         return !EntityUtil.isDead(p);
      }).collect(Collectors.toList());
      List<EntityPlayer> players = (List)playerList.stream().filter((p) -> {
         return SocialManager.isEnemy(p.func_70005_c_());
      }).collect(Collectors.toList());
      if (players.isEmpty()) {
         players.addAll(playerList);
      }

      Stream var10000 = players.stream();
      EntityPlayerSP var10001 = mc.field_71439_g;
      var10001.getClass();
      return (EntityPlayer)var10000.min(Comparator.comparing(var10001::func_70032_d)).orElse((Object)null);
   }

   public static EntityPlayer findLookingPlayer(double rangeMax) {
      ArrayList<EntityPlayer> listPlayer = new ArrayList();
      Iterator var3 = mc.field_71441_e.field_73010_i.iterator();

      while(var3.hasNext()) {
         EntityPlayer playerSin = (EntityPlayer)var3.next();
         if (!EntityUtil.basicChecksEntity(playerSin) && (double)mc.field_71439_g.func_70032_d(playerSin) <= rangeMax) {
            listPlayer.add(playerSin);
         }
      }

      EntityPlayer target = null;
      Vec3d positionEyes = mc.field_71439_g.func_174824_e(mc.func_184121_ak());
      Vec3d rotationEyes = mc.field_71439_g.func_70676_i(mc.func_184121_ak());
      int precision = 2;

      for(int i = 0; i < (int)rangeMax; ++i) {
         for(int j = precision; j > 0; --j) {
            Iterator var9 = listPlayer.iterator();

            while(var9.hasNext()) {
               EntityPlayer targetTemp = (EntityPlayer)var9.next();
               AxisAlignedBB playerBox = targetTemp.func_174813_aQ();
               double xArray = positionEyes.field_72450_a + rotationEyes.field_72450_a * (double)i + rotationEyes.field_72450_a / (double)j;
               double yArray = positionEyes.field_72448_b + rotationEyes.field_72448_b * (double)i + rotationEyes.field_72448_b / (double)j;
               double zArray = positionEyes.field_72449_c + rotationEyes.field_72449_c * (double)i + rotationEyes.field_72449_c / (double)j;
               if (playerBox.field_72337_e >= yArray && playerBox.field_72338_b <= yArray && playerBox.field_72336_d >= xArray && playerBox.field_72340_a <= xArray && playerBox.field_72334_f >= zArray && playerBox.field_72339_c <= zArray) {
                  target = targetTemp;
               }
            }
         }
      }

      return target;
   }

   public static List<EntityPlayer> getNearPlayers(double range, int count) {
      List<EntityPlayer> targetList = new ArrayList();
      List<EntityPlayer> list = new ArrayList();
      Iterator var5 = mc.field_71441_e.field_73010_i.iterator();

      while(var5.hasNext()) {
         EntityPlayer player = (EntityPlayer)var5.next();
         if (!((double)mc.field_71439_g.func_70032_d(player) > range) && !EntityUtil.basicChecksEntity(player) && !EntityUtil.isDead(player)) {
            targetList.add(player);
         }
      }

      List<EntityPlayer> players = (List)targetList.stream().filter((p) -> {
         return SocialManager.isEnemy(p.func_70005_c_());
      }).collect(Collectors.toList());
      if (players.isEmpty()) {
         players.addAll(targetList);
      }

      players.stream().sorted(Comparator.comparing(PlayerUtil::getDistance)).forEach(list::add);
      return new ArrayList(list.subList(0, Math.min(count, list.size())));
   }

   public static float getHealth() {
      return mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj();
   }

   public static void centerPlayer() {
      double newX = -2.0D;
      double newZ = -2.0D;
      int xRel = mc.field_71439_g.field_70165_t < 0.0D ? -1 : 1;
      int zRel = mc.field_71439_g.field_70161_v < 0.0D ? -1 : 1;
      if (BlockUtil.getBlock(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 1.0D, mc.field_71439_g.field_70161_v) instanceof BlockAir) {
         if (Math.abs(mc.field_71439_g.field_70165_t % 1.0D) * 100.0D <= 30.0D) {
            newX = (double)Math.round(mc.field_71439_g.field_70165_t - 0.3D * (double)xRel) + 0.5D * (double)(-xRel);
         } else if (Math.abs(mc.field_71439_g.field_70165_t % 1.0D) * 100.0D >= 70.0D) {
            newX = (double)Math.round(mc.field_71439_g.field_70165_t + 0.3D * (double)xRel) - 0.5D * (double)(-xRel);
         }

         if (Math.abs(mc.field_71439_g.field_70161_v % 1.0D) * 100.0D <= 30.0D) {
            newZ = (double)Math.round(mc.field_71439_g.field_70161_v - 0.3D * (double)zRel) + 0.5D * (double)(-zRel);
         } else if (Math.abs(mc.field_71439_g.field_70161_v % 1.0D) * 100.0D >= 70.0D) {
            newZ = (double)Math.round(mc.field_71439_g.field_70161_v + 0.3D * (double)zRel) - 0.5D * (double)(-zRel);
         }
      }

      if (newX == -2.0D) {
         if (mc.field_71439_g.field_70165_t > (double)Math.round(mc.field_71439_g.field_70165_t)) {
            newX = (double)Math.round(mc.field_71439_g.field_70165_t) + 0.5D;
         } else if (mc.field_71439_g.field_70165_t < (double)Math.round(mc.field_71439_g.field_70165_t)) {
            newX = (double)Math.round(mc.field_71439_g.field_70165_t) - 0.5D;
         } else {
            newX = mc.field_71439_g.field_70165_t;
         }
      }

      if (newZ == -2.0D) {
         if (mc.field_71439_g.field_70161_v > (double)Math.round(mc.field_71439_g.field_70161_v)) {
            newZ = (double)Math.round(mc.field_71439_g.field_70161_v) + 0.5D;
         } else if (mc.field_71439_g.field_70161_v < (double)Math.round(mc.field_71439_g.field_70161_v)) {
            newZ = (double)Math.round(mc.field_71439_g.field_70161_v) - 0.5D;
         } else {
            newZ = mc.field_71439_g.field_70161_v;
         }
      }

      mc.field_71439_g.field_71174_a.func_147297_a(new Position(newX, mc.field_71439_g.field_70163_u, newZ, true));
      mc.field_71439_g.func_70107_b(newX, mc.field_71439_g.field_70163_u, newZ);
   }
}
