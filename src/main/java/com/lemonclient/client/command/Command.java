package com.lemonclient.client.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.minecraft.client.Minecraft;

public abstract class Command {
   protected static final Minecraft mc = Minecraft.func_71410_x();
   private final String name = this.getDeclaration().name();
   private final String[] alias = this.getDeclaration().alias();
   private final String syntax = this.getDeclaration().syntax();

   private Command.Declaration getDeclaration() {
      return (Command.Declaration)this.getClass().getAnnotation(Command.Declaration.class);
   }

   public String getName() {
      return this.name;
   }

   public String getSyntax() {
      return CommandManager.getCommandPrefix() + this.syntax;
   }

   public String[] getAlias() {
      return this.alias;
   }

   public abstract void onCommand(String var1, String[] var2, boolean var3);

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE})
   public @interface Declaration {
      String name();

      String syntax();

      String[] alias();
   }
}
