package gothello.gothelloserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

/**
 * Util is a class for static utility and wrapper functions
 */
public class Util {
  
  public static String JSONStringify(Object object){
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      return "{}";
    }
  }

  public static TextMessage JSONMessage(Object object){
    return new TextMessage(JSONStringify(object));
  }

}