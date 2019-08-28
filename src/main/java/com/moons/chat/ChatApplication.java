package com.moons.chat;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);

		Configuration config = new Configuration();
		config.setHostname("localhost");
		config.setPort(3700);
		final SocketIOServer server = new SocketIOServer(config);
		server.addConnectListener(new ConnectListener() {
			@Override
			public void onConnect(SocketIOClient client) {
				System.out.println("onConnected");
				UUID uuid = UUID.randomUUID();
			}
		});
		server.addDisconnectListener(new DisconnectListener() {
			@Override
			public void onDisconnect(SocketIOClient client) {
				System.out.println("onDisconnected");
			}
		});
		server.addEventListener("send", Message.class, new DataListener<Message>() {

			@Override
			public void onData(SocketIOClient client, Message data, AckRequest ackSender) throws Exception {
				System.out.println("onSend: " + data.toString());
				UUID uuid = UUID.randomUUID();
				data.setKey(uuid);
				server.getBroadcastOperations().sendEvent("message", data);
			}
		});
		System.out.println("Starting server...");
		server.start();
		System.out.println("Server started");
	}

}
