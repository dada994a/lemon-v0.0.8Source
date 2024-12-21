package com.lemonclient.api.util.chat;

import java.util.Collection;
import me.zero.alpine.listener.Listener;

public interface Subscriber {
   Collection<Listener<?>> getListeners();
}
