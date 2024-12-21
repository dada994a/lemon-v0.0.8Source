package com.lemonclient.api.util.player.social;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.qwq.Friends;
import java.util.ArrayList;
import java.util.Iterator;

public class SocialManager {
   private static final ArrayList<Friend> friends = new ArrayList();
   private static final ArrayList<Enemy> enemies = new ArrayList();
   private static final ArrayList<Ignore> ignores = new ArrayList();

   public static ArrayList<Friend> getFriends() {
      return friends;
   }

   public static ArrayList<Enemy> getEnemies() {
      return enemies;
   }

   public static ArrayList<Ignore> getIgnores() {
      return ignores;
   }

   public static ArrayList<String> getFriendsByName() {
      ArrayList<String> friendNames = new ArrayList();
      getFriends().forEach((friend) -> {
         friendNames.add(friend.getName());
      });
      return friendNames;
   }

   public static ArrayList<String> getEnemiesByName() {
      ArrayList<String> enemyNames = new ArrayList();
      getEnemies().forEach((enemy) -> {
         enemyNames.add(enemy.getName());
      });
      return enemyNames;
   }

   public static ArrayList<String> getIgnoresByName() {
      ArrayList<String> ignoreNames = new ArrayList();
      getIgnores().forEach((ignore) -> {
         ignoreNames.add(ignore.getName());
      });
      return ignoreNames;
   }

   public static boolean isFriend(String name) {
      Iterator var1 = getFriends().iterator();

      Friend friend;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         friend = (Friend)var1.next();
      } while(!friend.getName().equalsIgnoreCase(name) || !ModuleManager.isModuleEnabled(Friends.class));

      return true;
   }

   public static boolean isOnFriendList(String name) {
      boolean value = false;
      Iterator var2 = getFriends().iterator();

      while(var2.hasNext()) {
         Friend friend = (Friend)var2.next();
         if (friend.getName().equalsIgnoreCase(name)) {
            value = true;
            break;
         }
      }

      return value;
   }

   public static boolean isEnemy(String name) {
      Iterator var1 = getEnemies().iterator();

      Enemy enemy;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         enemy = (Enemy)var1.next();
      } while(!enemy.getName().equalsIgnoreCase(name));

      return true;
   }

   public static boolean isOnEnemyList(String name) {
      boolean value = false;
      Iterator var2 = getEnemies().iterator();

      while(var2.hasNext()) {
         Enemy enemy = (Enemy)var2.next();
         if (enemy.getName().equalsIgnoreCase(name)) {
            value = true;
            break;
         }
      }

      return value;
   }

   public static boolean isIgnore(String name) {
      Iterator var1 = getIgnores().iterator();

      Ignore ignore;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         ignore = (Ignore)var1.next();
      } while(!ignore.getName().equalsIgnoreCase(name));

      return true;
   }

   public static boolean isOnIgnoreList(String name) {
      boolean value = false;
      Iterator var2 = getIgnores().iterator();

      while(var2.hasNext()) {
         Ignore ignore = (Ignore)var2.next();
         if (ignore.getName().equalsIgnoreCase(name)) {
            value = true;
            break;
         }
      }

      return value;
   }

   public static Friend getFriend(String name) {
      Iterator var1 = getFriends().iterator();

      Friend friend;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         friend = (Friend)var1.next();
      } while(!friend.getName().equalsIgnoreCase(name));

      return friend;
   }

   public static Enemy getEnemy(String name) {
      Iterator var1 = getEnemies().iterator();

      Enemy enemy;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         enemy = (Enemy)var1.next();
      } while(!enemy.getName().equalsIgnoreCase(name));

      return enemy;
   }

   public static Ignore getIgnore(String name) {
      Iterator var1 = getIgnores().iterator();

      Ignore ignore;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         ignore = (Ignore)var1.next();
      } while(!ignore.getName().equalsIgnoreCase(name));

      return ignore;
   }

   public static void addFriend(String name) {
      if (!isOnFriendList(name)) {
         getFriends().add(new Friend(name));
      }

   }

   public static void delFriend(String name) {
      getFriends().remove(getFriend(name));
   }

   public static void addEnemy(String name) {
      if (!isOnEnemyList(name)) {
         getEnemies().add(new Enemy(name));
      }

   }

   public static void delEnemy(String name) {
      getEnemies().remove(getEnemy(name));
   }

   public static void addIgnore(String name) {
      if (!isOnIgnoreList(name)) {
         getIgnores().add(new Ignore(name));
      }

   }

   public static void delIgnore(String name) {
      getIgnores().remove(getIgnore(name));
   }
}
