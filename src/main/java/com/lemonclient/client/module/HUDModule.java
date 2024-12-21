package com.lemonclient.client.module;

import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Point;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class HUDModule extends Module {
   public static final int LIST_BORDER = 1;
   protected IFixedComponent component;
   protected Point position = new Point(this.getDeclaration().posX(), this.getDeclaration().posZ());

   private HUDModule.Declaration getDeclaration() {
      return (HUDModule.Declaration)this.getClass().getAnnotation(HUDModule.Declaration.class);
   }

   public abstract void populate(ITheme var1);

   public IFixedComponent getComponent() {
      return this.component;
   }

   public void resetPosition() {
      this.component.setPosition(LemonClientGUI.guiInterface, this.position);
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE})
   public @interface Declaration {
      int posX();

      int posZ();
   }
}
