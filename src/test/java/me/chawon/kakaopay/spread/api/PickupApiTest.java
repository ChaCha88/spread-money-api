package me.chawon.kakaopay.spread.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.chawon.kakaopay.spread.application.SpreadService;
import me.chawon.kakaopay.spread.dto.Spread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PickupApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpreadService spreadService;

    @Autowired
    private ObjectMapper objectMapper;

    private Spread.Request spreadReq;
    private String roomId;
    private long userId;

    private String objectToJson(Object inObject) throws JsonProcessingException {
        return objectMapper.writeValueAsString(inObject);
    }

    @BeforeEach
    public void setUp() {
        //given
        roomId = "_kakaopay02";
        userId = 999L;

        spreadReq = Spread.Request.builder().amount(1000L).person(4).build();
    }

    @Test
    @DisplayName("줍기 후 응답값 금액받기")
    public void pickUpApi1() throws Exception {
        //given

        long pickId = 5959L;

        //뿌리기
        String token = spreadService.spread(roomId, userId, spreadReq);
        Spread.Token resToken = new Spread.Token().builder().token(token).build();

        //when
        mockMvc.perform(
                put("/api/v1/pickup")
                        .header("X-ROOM-ID", roomId)
                        .header("X-USER-ID", pickId)
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                        .content(objectToJson(resToken))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status", is(201)))
                .andExpect(jsonPath("data.amount").isNotEmpty());
    }
}
