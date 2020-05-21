package gothello.gothelloserver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void createPublicGame() throws Exception {
		this.mockMvc.perform(get("/api/v0/game/new")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.ok").value("true")).andExpect(jsonPath("$.type").value("game"))
				.andExpect(jsonPath("$.open").value(true)).andExpect(jsonPath("$.gameType").value("PUBLIC"));
	}

	@Test
	public void createPrivateGame() throws Exception {
		this.mockMvc.perform(get("/api/v0/game/new?type=private")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.ok").value("true")).andExpect(jsonPath("$.type").value("game"))
				.andExpect(jsonPath("$.open").value(false)).andExpect(jsonPath("$.gameType").value("PRIVATE"));
	}

	@Test
	public void joinFail() throws Exception {
		this.mockMvc.perform(get("/api/v0/game/join")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.ok").value("false")).andExpect(jsonPath("$.type").value("error"));
	}
}
