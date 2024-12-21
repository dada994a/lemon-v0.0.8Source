package com.lukflug.panelstudio.base;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

@FunctionalInterface
public interface IBoolean extends BooleanSupplier, Supplier<Boolean>, Predicate<Void> {
   boolean isOn();

   default boolean getAsBoolean() {
      return this.isOn();
   }

   default Boolean get() {
      return this.isOn();
   }

   default boolean test(Void t) {
      return this.isOn();
   }
}
