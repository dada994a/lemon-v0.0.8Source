package com.lemonclient.api.util.misc;

import java.io.IOException;
import java.net.URI;
import shaded.websocket.ClientEndpoint;
import shaded.websocket.CloseReason;
import shaded.websocket.ContainerProvider;
import shaded.websocket.OnClose;
import shaded.websocket.OnMessage;
import shaded.websocket.OnOpen;
import shaded.websocket.RemoteEndpoint;
import shaded.websocket.Session;
import shaded.websocket.WebSocketContainer;

@ClientEndpoint
public class WebsocketClientEndpoint {
   Session userSession = null;
   private WebsocketClientEndpoint.MessageHandler messageHandler;

   public int getUserSession() {
      return this.userSession == null ? 0 : 1;
   }

   public void close() {
      try {
         if (this.userSession != null) {
            this.userSession.close();
         }
      } catch (NullPointerException | IOException var2) {
      }

   }

   public WebsocketClientEndpoint(URI endpointURI) {
      try {
         Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
         WebSocketContainer container = ContainerProvider.getWebSocketContainer();
         this.userSession = container.connectToServer((Object)this, endpointURI);
      } catch (Exception var3) {
      }

   }

   @OnOpen
   public void onOpen(Session userSession) {
      System.out.println("opening websocket");
      this.userSession = userSession;
   }

   @OnClose
   public void onClose(Session userSession, CloseReason reason) {
      System.out.println("closing websocket");
      this.userSession = null;
   }

   @OnMessage
   public void onMessage(String message) {
      if (this.messageHandler != null) {
         this.messageHandler.handleMessage(message);
      }

   }

   public void addMessageHandler(WebsocketClientEndpoint.MessageHandler msgHandler) {
      this.messageHandler = msgHandler;
   }

   public void sendMessage(String message) {
      RemoteEndpoint.Async remoteEndpoint = this.userSession.getAsyncRemote();
      remoteEndpoint.sendText(message);
   }

   public interface MessageHandler {
      void handleMessage(String var1);
   }
}
