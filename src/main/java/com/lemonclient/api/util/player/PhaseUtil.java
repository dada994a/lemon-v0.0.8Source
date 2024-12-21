package com.lemonclient.api.util.player;

import com.lemonclient.api.util.world.MotionUtil;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;

public class PhaseUtil {
   public static List<String> bound = Arrays.asList("Up", "Alternate", "Down", "Zero", "Min", "Forward", "Flat", "LimitJitter", "Constrict", "None");
   public static String normal = "Forward";
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static CPacketPlayer doBounds(String mode, boolean send) {
      CPacketPlayer packet = new PositionRotation(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      byte var4 = -1;
      switch(mode.hashCode()) {
      case 2747:
         if (mode.equals("Up")) {
            var4 = 0;
         }
         break;
      case 77362:
         if (mode.equals("Min")) {
            var4 = 3;
         }
         break;
      case 2136258:
         if (mode.equals("Down")) {
            var4 = 1;
         }
         break;
      case 2192281:
         if (mode.equals("Flat")) {
            var4 = 6;
         }
         break;
      case 2781896:
         if (mode.equals("Zero")) {
            var4 = 2;
         }
         break;
      case 595943514:
         if (mode.equals("Alternate")) {
            var4 = 4;
         }
         break;
      case 987507365:
         if (mode.equals("Forward")) {
            var4 = 5;
         }
         break;
      case 1582192299:
         if (mode.equals("Constrict")) {
            var4 = 7;
         }
      }

      double[] dir;
      switch(var4) {
      case 0:
         packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 1:
         packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 2:
         packet = new PositionRotation(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 3:
         packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 100.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 4:
         if (mc.field_71439_g.field_70173_aa % 2 == 0) {
            packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         } else {
            packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         }
         break;
      case 5:
         dir = MotionUtil.forward(67.0D);
         packet = new PositionRotation(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u + 33.4D, mc.field_71439_g.field_70161_v + dir[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 6:
         dir = MotionUtil.forward(100.0D);
         packet = new PositionRotation(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + dir[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 7:
         dir = MotionUtil.forward(67.0D);
         packet = new PositionRotation(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u + (mc.field_71439_g.field_70163_u > 64.0D ? -33.4D : 33.4D), mc.field_71439_g.field_70161_v + dir[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
      }

      mc.field_71439_g.field_71174_a.func_147297_a(packet);
      return packet;
   }

   public static CPacketPlayer doBounds(String mode, int c) {
      CPacketPlayer packet = new PositionRotation(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      byte var4 = -1;
      switch(mode.hashCode()) {
      case 2747:
         if (mode.equals("Up")) {
            var4 = 0;
         }
         break;
      case 77362:
         if (mode.equals("Min")) {
            var4 = 3;
         }
         break;
      case 2136258:
         if (mode.equals("Down")) {
            var4 = 1;
         }
         break;
      case 2192281:
         if (mode.equals("Flat")) {
            var4 = 6;
         }
         break;
      case 2781896:
         if (mode.equals("Zero")) {
            var4 = 2;
         }
         break;
      case 595943514:
         if (mode.equals("Alternate")) {
            var4 = 4;
         }
         break;
      case 987507365:
         if (mode.equals("Forward")) {
            var4 = 5;
         }
         break;
      case 1582192299:
         if (mode.equals("Constrict")) {
            var4 = 7;
         }
      }

      double[] dir;
      switch(var4) {
      case 0:
         packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 1:
         packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 2:
         packet = new PositionRotation(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 3:
         packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 100.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 4:
         if (mc.field_71439_g.field_70173_aa % 2 == 0) {
            packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         } else {
            packet = new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         }
         break;
      case 5:
         dir = MotionUtil.forward(67.0D);
         packet = new PositionRotation(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u + 33.4D, mc.field_71439_g.field_70161_v + dir[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 6:
         dir = MotionUtil.forward(100.0D);
         packet = new PositionRotation(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + dir[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         break;
      case 7:
         dir = MotionUtil.forward(67.0D);
         packet = new PositionRotation(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u + (mc.field_71439_g.field_70163_u > 64.0D ? -33.4D : 33.4D), mc.field_71439_g.field_70161_v + dir[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
      }

      for(int i = 1; i < c; ++i) {
         mc.field_71439_g.field_71174_a.func_147297_a(packet);
      }

      return packet;
   }
}
