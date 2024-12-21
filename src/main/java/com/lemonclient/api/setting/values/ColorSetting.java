package com.lemonclient.api.setting.values;

import com.lemonclient.api.setting.Setting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.function.Supplier;

public class ColorSetting extends Setting<GSColor> {
   private boolean rainbow = false;
   private final boolean rainbowEnabled;
   private final boolean alphaEnabled;

   public ColorSetting(String name, Module module, boolean rainbow, GSColor value) {
      super(value, name, module);
      this.rainbow = rainbow;
      this.rainbowEnabled = true;
      this.alphaEnabled = false;
   }

   public ColorSetting(String name, Module module, boolean rainbow, GSColor value, boolean alphaEnabled) {
      super(value, name, module);
      this.rainbow = rainbow;
      this.rainbowEnabled = true;
      this.alphaEnabled = alphaEnabled;
   }

   public ColorSetting(String name, String configName, Module module, Supplier<Boolean> isVisible, boolean rainbow, boolean rainbowEnabled, boolean alphaEnabled, GSColor value) {
      super(value, name, configName, module, isVisible);
      this.rainbow = rainbow;
      this.rainbowEnabled = rainbowEnabled;
      this.alphaEnabled = alphaEnabled;
   }

   public GSColor getValue() {
      if (this.rainbow) {
         String var1 = (String)ColorMain.INSTANCE.rainbowMode.getValue();
         byte var2 = -1;
         switch(var1.hashCode()) {
         case 82993:
            if (var1.equals("Sec")) {
               var2 = 2;
            }
            break;
         case 83128:
            if (var1.equals("Sin")) {
               var2 = 0;
            }
            break;
         case 83841:
            if (var1.equals("Tan")) {
               var2 = 1;
            }
            break;
         case 65265701:
            if (var1.equals("CoSec")) {
               var2 = 4;
            }
            break;
         case 65266549:
            if (var1.equals("CoTan")) {
               var2 = 3;
            }
         }

         switch(var2) {
         case 0:
            return getRainbowSin(0, 0, 1.0D, 1, 1.0D, 0, false);
         case 1:
            return getRainbowTan(0, 0, 1.0D, 1, 1.0D, 0, false);
         case 2:
            return getRainbowSec(0, 0, 1.0D, 1, 1.0D, 0, false);
         case 3:
            return getRainbowCoTan(0, 0, 1.0D, 1, 1.0D, 0, false);
         case 4:
            return getRainbowCoSec(0, 0, 1.0D, 1, 1.0D, 0, false);
         default:
            return getRainbowColor(0, 0, 0, false);
         }
      } else {
         return (GSColor)super.getValue();
      }
   }

   public static GSColor getRainbowColor(int incr, int multiply, int start, boolean stop) {
      return GSColor.fromHSB((float)(((stop ? (long)start : System.currentTimeMillis()) + (long)(incr * multiply)) % 11520L) / 11520.0F * ((Double)((ColorMain)ModuleManager.getModule(ColorMain.class)).rainbowSpeed.getValue()).floatValue(), 1.0F, 1.0F);
   }

   public static GSColor getRainbowColor(double incr) {
      return GSColor.fromHSB((float)(incr % 11520.0D / 11520.0D) * ((Double)((ColorMain)ModuleManager.getModule(ColorMain.class)).rainbowSpeed.getValue()).floatValue(), 1.0F, 1.0F);
   }

   public static GSColor getRainbowSin(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
      return GSColor.fromHSB((float)(height * (double)multiplyHeight * Math.sin(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply) % 11520.0D / 11520.0D)) * ((Double)((ColorMain)ModuleManager.getModule(ColorMain.class)).rainbowSpeed.getValue()).floatValue(), 1.0F, 1.0F);
   }

   public static GSColor getRainbowTan(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
      return GSColor.fromHSB((float)(height * (double)multiplyHeight * Math.tan(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply % 11520.0D) / 11520.0D)) * ((Double)((ColorMain)ModuleManager.getModule(ColorMain.class)).rainbowSpeed.getValue()).floatValue(), 1.0F, 1.0F);
   }

   public static GSColor getRainbowSec(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
      return GSColor.fromHSB((float)(height * (double)multiplyHeight * (1.0D / Math.sin(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)((float)incr) / millSin * (double)multiply) % 11520.0D / 11520.0D))) * ((Double)((ColorMain)ModuleManager.getModule(ColorMain.class)).rainbowSpeed.getValue()).floatValue(), 1.0F, 1.0F);
   }

   public static GSColor getRainbowCoSec(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
      return GSColor.fromHSB((float)(height * (double)multiplyHeight * (1.0D / Math.cos(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply) % 11520.0D / 11520.0D))) * ((Double)((ColorMain)ModuleManager.getModule(ColorMain.class)).rainbowSpeed.getValue()).floatValue(), 1.0F, 1.0F);
   }

   public static GSColor getRainbowCoTan(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
      return GSColor.fromHSB((float)(height * (double)multiplyHeight * Math.tan(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply) % 11520.0D / 11520.0D)) * ((Double)((ColorMain)ModuleManager.getModule(ColorMain.class)).rainbowSpeed.getValue()).floatValue(), 1.0F, 1.0F);
   }

   public void setValue(GSColor value) {
      super.setValue(new GSColor(value));
   }

   public GSColor getColor() {
      return (GSColor)super.getValue();
   }

   public boolean getRainbow() {
      return this.rainbow;
   }

   public void setRainbow(boolean rainbow) {
      this.rainbow = rainbow;
   }

   public boolean rainbowEnabled() {
      return this.rainbowEnabled;
   }

   public boolean alphaEnabled() {
      return this.alphaEnabled;
   }

   public long toLong() {
      long temp = (long)(this.getColor().getRGB() & 16777215);
      if (this.rainbowEnabled) {
         temp += (long)((this.rainbow ? 1 : 0) << 24);
      }

      if (this.alphaEnabled) {
         temp += (long)this.getColor().getAlpha() << 32;
      }

      return temp;
   }

   public void fromLong(long number) {
      if (this.rainbowEnabled) {
         this.rainbow = (number & 16777216L) != 0L;
      } else {
         this.rainbow = false;
      }

      this.setValue(new GSColor((int)(number & 16777215L)));
      if (this.alphaEnabled) {
         this.setValue(new GSColor(this.getColor(), (int)((number & 1095216660480L) >> 32)));
      }

   }
}
