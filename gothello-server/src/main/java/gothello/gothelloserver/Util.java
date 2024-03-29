package gothello.gothelloserver;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Util is a class for static utility and wrapper functions
 */
public class Util {

  public static String JSONStringify(Object object) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      return "{}";
    }
  }

  public static void JSONMessage(WebSocketSession session, Object object) throws IOException {
    synchronized (session) {
      session.sendMessage(new TextMessage(JSONStringify(object)));
    }
  }

  public static String getMessageType(String json) throws IOException {
    ObjectNode node = new ObjectMapper().readValue(json, ObjectNode.class);
    if (node.has("messageType")) {
      return node.get("messageType").asText();
    } else {
      throw new IOException("All messages sent via the websocket must have a 'messageType'");
    }
  }

  public static <E> E parseMessage(String json, Class<E> typeClass) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper()
        .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build());
    return objectMapper.readValue(json, typeClass);
  }

}