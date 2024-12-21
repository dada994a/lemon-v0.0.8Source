package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.ColorUtil;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.manager.managers.TotemPopManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagList;

@Module.Declaration(
   name = "PvPInfo",
   category = Category.Misc
)
public class PvPInfo extends Module {
   BooleanSetting visualRange = this.registerBoolean("Visual Range", false);
   BooleanSetting coords = this.registerBoolean("Coords", true, () -> {
      return (Boolean)this.visualRange.getValue();
   });
   BooleanSetting pearlAlert = this.registerBoolean("Pearl Alert", false);
   BooleanSetting strengthDetect = this.registerBoolean("Strength Detect", false);
   BooleanSetting weaknessDetect = this.registerBoolean("Weakness Detect", false);
   BooleanSetting popCounter = this.registerBoolean("Pop Counter", false);
   BooleanSetting friend = this.registerBoolean("My Friend", false);
   BooleanSetting sharp32 = this.registerBoolean("sharp32", true);
   ModeSetting type = this.registerMode("Visual Type", Arrays.asList("Friend", "Enemy", "All"), "All");
   ModeSetting type1 = this.registerMode("Pearl Type", Arrays.asList("Friend", "Enemy", "All"), "All");
   ModeSetting type2 = this.registerMode("Strength Type", Arrays.asList("Friend", "Enemy", "All"), "All");
   ModeSetting type3 = this.registerMode("Weakness Type", Arrays.asList("Friend", "Enemy", "All"), "All");
   ModeSetting type4 = this.registerMode("Pop Type", Arrays.asList("Friend", "Enemy", "All"), "All");
   ModeSetting type5 = this.registerMode("32k Type", Arrays.asList("Friend", "Enemy", "All"), "All");
   ModeSetting self = this.registerMode("Self", Arrays.asList("I", "Name", "Disable"), "Name");
   ModeSetting chatColor;
   ModeSetting nameColor;
   ModeSetting friColor;
   ModeSetting numberColor;
   List<Entity> knownPlayers;
   List<Entity> antiPearlList;
   List<Entity> players;
   List<Entity> pearls;
   private final Set<EntityPlayer> strengthPlayers;
   private final Set<EntityPlayer> weaknessPlayers;
   private final Set<EntityPlayer> sword;

   public PvPInfo() {
      this.chatColor = this.registerMode("Color", ColorUtil.colors, "Light Purple");
      this.nameColor = this.registerMode("Name Color", ColorUtil.colors, "Light Purple");
      this.friColor = this.registerMode("Friend Color", ColorUtil.colors, "Light Purple");
      this.numberColor = this.registerMode("Number Color", ColorUtil.colors, "Light Purple");
      this.knownPlayers = new ArrayList();
      this.antiPearlList = new ArrayList();
      this.strengthPlayers = Collections.newSetFromMap(new WeakHashMap());
      this.weaknessPlayers = Collections.newSetFromMap(new WeakHashMap());
      this.sword = Collections.newSetFromMap(new WeakHashMap());
   }

   public void onUpdate() {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         TotemPopManager.INSTANCE.sendMsgs = (Boolean)this.popCounter.getValue();
         if ((Boolean)this.popCounter.getValue()) {
            TotemPopManager.INSTANCE.chatFormatting = ColorUtil.textToChatFormatting(this.chatColor);
            TotemPopManager.INSTANCE.nameFormatting = ColorUtil.textToChatFormatting(this.nameColor);
            TotemPopManager.INSTANCE.friFormatting = ColorUtil.textToChatFormatting(this.friColor);
            TotemPopManager.INSTANCE.numberFormatting = ColorUtil.textToChatFormatting(this.numberColor);
            TotemPopManager.INSTANCE.friend = (Boolean)this.friend.getValue();
            TotemPopManager.INSTANCE.self = (String)this.self.getValue();
            TotemPopManager.INSTANCE.type4 = (String)this.type4.getValue();
         }

         Iterator var1;
         Entity e;
         String name;
         String name;
         if ((Boolean)this.visualRange.getValue()) {
            this.players = (List)mc.field_71441_e.field_73010_i.stream().filter((entity) -> {
               return !entity.func_70005_c_().equals(mc.field_71439_g.func_70005_c_());
            }).collect(Collectors.toList());

            try {
               var1 = this.players.iterator();

               label561:
               while(true) {
                  do {
                     do {
                        if (!var1.hasNext()) {
                           break label561;
                        }

                        e = (Entity)var1.next();
                     } while(e.func_70005_c_().equalsIgnoreCase("fakeplayer"));
                  } while(this.knownPlayers.contains(e));

                  this.knownPlayers.add(e);
                  name = (Boolean)this.coords.getValue() ? " at x:" + (int)e.field_70165_t + " y:" + (int)e.field_70163_u + " z:" + (int)e.field_70161_v : "";
                  name = e.func_70005_c_();
                  if (name.equals("") || name.equals(" ")) {
                     return;
                  }

                  if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type.getValue()).equals("Enemy")) {
                     MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.chatColor) + "Found (" + ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + ")" + name, Notification.Type.INFO, "VisualRange" + name, 2000);
                  }

                  if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type.getValue()).equals("Friend")) {
                     MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.chatColor) + "Found (" + ColorUtil.textToChatFormatting(this.nameColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + ")" + name, Notification.Type.INFO, "VisualRange" + name, 2000);
                  }
               }
            } catch (Exception var7) {
            }

            try {
               var1 = this.knownPlayers.iterator();

               label532:
               while(true) {
                  do {
                     do {
                        if (!var1.hasNext()) {
                           break label532;
                        }

                        e = (Entity)var1.next();
                     } while(e.func_70005_c_().equalsIgnoreCase("fakeplayer"));
                  } while(this.players.contains(e));

                  this.knownPlayers.remove(e);
                  name = (Boolean)this.coords.getValue() ? " at x:" + (int)e.field_70165_t + " y:" + (int)e.field_70163_u + " z:" + (int)e.field_70161_v : "";
                  name = e.func_70005_c_();
                  if (name.equals("") || name.equals(" ")) {
                     return;
                  }

                  if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type.getValue()).equals("Enemy")) {
                     MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.chatColor) + "Gone (" + ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + ")" + name, Notification.Type.INFO, "VisualRange" + name, 2000);
                  }

                  if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type.getValue()).equals("Friend")) {
                     MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.chatColor) + "Gone (" + ColorUtil.textToChatFormatting(this.nameColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + ")" + name, Notification.Type.INFO, "VisualRange" + name, 2000);
                  }
               }
            } catch (Exception var6) {
            }
         }

         if ((Boolean)this.pearlAlert.getValue()) {
            this.pearls = (List)mc.field_71441_e.field_72996_f.stream().filter((ex) -> {
               return ex instanceof EntityEnderPearl;
            }).collect(Collectors.toList());

            try {
               var1 = this.pearls.iterator();

               label499:
               while(true) {
                  do {
                     do {
                        do {
                           if (!var1.hasNext()) {
                              break label499;
                           }

                           e = (Entity)var1.next();
                        } while(!(e instanceof EntityEnderPearl));
                     } while(e.func_130014_f_().func_72890_a(e, 3.0D).func_70005_c_().equalsIgnoreCase("fakeplayer"));
                  } while(this.antiPearlList.contains(e));

                  this.antiPearlList.add(e);
                  name = e.func_174811_aO().toString();
                  if (name.equals("west")) {
                     name = "east";
                  } else if (name.equals("east")) {
                     name = "west";
                  }

                  if (mc.field_71439_g.func_70005_c_().equals(e.func_130014_f_().func_72890_a(e, 3.0D).func_70005_c_()) && ((String)this.self.getValue()).equals("Disable")) {
                     return;
                  }

                  name = e.func_130014_f_().func_72890_a(e, 3.0D).func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) && ((String)this.self.getValue()).equals("I") ? "I" : e.func_130014_f_().func_72890_a(e, 3.0D).func_70005_c_();
                  if (name.equals("") || name.equals(" ")) {
                     return;
                  }

                  if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type1.getValue()).equals("Enemy")) {
                     MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + " has just thrown a pearl! (" + name + ")", Notification.Type.INFO);
                  }

                  if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type1.getValue()).equals("Friend")) {
                     MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + " has just thrown a pearl! (" + name + ")", Notification.Type.INFO);
                  }
               }
            } catch (Exception var5) {
            }
         }

         EntityPlayer player;
         if ((Boolean)this.strengthDetect.getValue()) {
            var1 = mc.field_71441_e.field_73010_i.iterator();

            label458:
            while(true) {
               do {
                  do {
                     do {
                        if (!var1.hasNext()) {
                           break label458;
                        }

                        player = (EntityPlayer)var1.next();
                     } while(player.func_70005_c_().equalsIgnoreCase("fakeplayer"));

                     if (player.func_70644_a(MobEffects.field_76420_g) && !this.strengthPlayers.contains(player)) {
                        if (mc.field_71439_g.func_70005_c_().equals(player.func_70005_c_()) && ((String)this.self.getValue()).equals("Disable")) {
                           return;
                        }

                        name = player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) && ((String)this.self.getValue()).equals("I") ? "I" : player.func_70005_c_();
                        if (name.equals("") || name.equals(" ")) {
                           return;
                        }

                        if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type2.getValue()).equals("Enemy")) {
                           MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + " has drank strength", Notification.Type.INFO, "Strength" + name, 2000);
                        }

                        if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type2.getValue()).equals("Friend")) {
                           MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + ChatFormatting.RED + " has drank strength", Notification.Type.INFO, "Strength" + name, 2000);
                        }

                        this.strengthPlayers.add(player);
                     }
                  } while(!this.strengthPlayers.contains(player));
               } while(player.func_70644_a(MobEffects.field_76420_g));

               if (mc.field_71439_g.func_70005_c_().equals(player.func_70005_c_()) && ((String)this.self.getValue()).equals("Disable")) {
                  return;
               }

               name = player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) && ((String)this.self.getValue()).equals("I") ? "I" : player.func_70005_c_();
               if (name.equals("") || name.equals(" ")) {
                  return;
               }

               if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type2.getValue()).equals("Enemy")) {
                  MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + " no longer has strength", Notification.Type.INFO, "Strength" + name, 2000);
               }

               if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type2.getValue()).equals("Friend")) {
                  MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + ChatFormatting.GREEN + " no longer has strength", Notification.Type.INFO, "Strength" + name, 2000);
               }

               this.strengthPlayers.remove(player);
            }
         }

         if ((Boolean)this.weaknessDetect.getValue()) {
            var1 = mc.field_71441_e.field_73010_i.iterator();

            label395:
            while(true) {
               do {
                  if (!var1.hasNext()) {
                     break label395;
                  }

                  player = (EntityPlayer)var1.next();
               } while(player.func_70005_c_().equalsIgnoreCase("FakePlayer"));

               if (player.func_70644_a(MobEffects.field_76437_t) && !this.weaknessPlayers.contains(player)) {
                  if (mc.field_71439_g.func_70005_c_().equals(player.func_70005_c_()) && ((String)this.self.getValue()).equals("Disable")) {
                     return;
                  }

                  name = player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) && ((String)this.self.getValue()).equals("I") ? "I" : player.func_70005_c_();
                  if (name.isEmpty() || name.equals(" ")) {
                     return;
                  }

                  if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type3.getValue()).equals("Enemy")) {
                     MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + " has drank weekness", Notification.Type.INFO, "Weakness" + name, 2000);
                  }

                  if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type3.getValue()).equals("Friend")) {
                     MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + ChatFormatting.GREEN + " has drank weekness", Notification.Type.INFO, "Weakness" + name, 2000);
                  }

                  this.weaknessPlayers.add(player);
               }

               if (this.weaknessPlayers.contains(player) && !player.func_70644_a(MobEffects.field_76437_t)) {
                  if (mc.field_71439_g.func_70005_c_().equals(player.func_70005_c_()) && ((String)this.self.getValue()).equals("Disable")) {
                     return;
                  }

                  name = player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) && ((String)this.self.getValue()).equals("I") ? "I" : player.func_70005_c_();
                  if (name.equals("") || name.equals(" ")) {
                     return;
                  }

                  if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type3.getValue()).equals("Enemy")) {
                     MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + " no longer has weekness", Notification.Type.INFO, "Weakness" + name, 2000);
                  }

                  if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type3.getValue()).equals("Friend")) {
                     MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + ChatFormatting.RED + " no longer has weekness", Notification.Type.INFO, "Weakness" + name, 2000);
                  }

                  this.weaknessPlayers.remove(player);
               }
            }
         }

         if ((Boolean)this.sharp32.getValue()) {
            var1 = mc.field_71441_e.field_73010_i.iterator();

            while(true) {
               do {
                  do {
                     do {
                        do {
                           if (!var1.hasNext()) {
                              return;
                           }

                           player = (EntityPlayer)var1.next();
                        } while(player.func_70005_c_().equalsIgnoreCase("fakeplayer"));
                     } while(player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()));

                     if (this.is32k(player.field_184831_bT) && !this.sword.contains(player)) {
                        name = player.func_70005_c_();
                        if (name.equals("") || name.equals(" ")) {
                           return;
                        }

                        if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type5.getValue()).equals("Enemy")) {
                           MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + " is " + ColorUtil.textToChatFormatting(this.chatColor) + "holding a 32k", Notification.Type.INFO, "32k" + name, 2000);
                        }

                        if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type5.getValue()).equals("Friend")) {
                           MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + " is " + ChatFormatting.RED + "holding" + ColorUtil.textToChatFormatting(this.chatColor) + " a 32k", Notification.Type.INFO, "32k" + name, 2000);
                        }

                        this.sword.add(player);
                     }
                  } while(!this.sword.contains(player));
               } while(this.is32k(player.field_184831_bT));

               name = player.func_70005_c_();
               if (name.equals("") || name.equals(" ")) {
                  return;
               }

               if (name.equals("I") || SocialManager.isFriend(name) && !((String)this.type5.getValue()).equals("Enemy")) {
                  MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name + " is " + ColorUtil.textToChatFormatting(this.chatColor) + "no longer holding a 32k", Notification.Type.INFO, "32k" + name, 2000);
               }

               if (!name.equals("I") && !SocialManager.isFriend(name) && !((String)this.type5.getValue()).equals("Friend")) {
                  MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + " is " + ChatFormatting.GREEN + "no longer holding" + ColorUtil.textToChatFormatting(this.chatColor) + " a 32k", Notification.Type.INFO, "32k" + name, 2000);
               }

               this.sword.remove(player);
            }
         }
      }
   }

   private boolean is32k(ItemStack stack) {
      if (stack.func_77973_b() instanceof ItemSword) {
         NBTTagList enchants = stack.func_77986_q();

         for(int i = 0; i < enchants.func_74745_c(); ++i) {
            if (enchants.func_150305_b(i).func_74765_d("lvl") >= 1000) {
               return true;
            }
         }
      }

      return false;
   }

   public void onDisable() {
      this.knownPlayers.clear();
      TotemPopManager.INSTANCE.sendMsgs = false;
   }
}
