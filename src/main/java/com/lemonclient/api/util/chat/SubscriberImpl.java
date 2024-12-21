package com.lemonclient.api.util.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.zero.alpine.listener.Listener;

public class SubscriberImpl implements Subscriber {
   protected final List<Listener<?>> listeners = new ArrayList();

   public Collection<Listener<?>> getListeners() {
      return this.listeners;
   }
}
