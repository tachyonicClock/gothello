package gothello.gothelloserver;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;

/**
 * WSConfig sets up and configures the web-socket and its handler
 */
@Configuration
@EnableWebSocket
public class WSConfig implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new WSHandler(), "/api/v0/games/*/socket").setAllowedOrigins("*")
				.addInterceptors(new HttpSessionHandshakeInterceptor());
	}
}