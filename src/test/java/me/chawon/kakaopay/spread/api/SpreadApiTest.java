package me.chawon.kakaopay.spread.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.chawon.kakaopay.spread.application.PickupService;
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

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpreadApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpreadService spreadService;

    @Autowired
    private PickupService pickupService;

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
        roomId = "_kakaopay01";
        userId = 7777L;

        spreadReq = Spread.Request.builder().amount(2000L).person(2).build();
    }

    @Test
    @DisplayName("뿌리기 요청 후 토큰 3자리 받기")
    public void spreadApi1() throws Exception {
        //given

        //when
        mockMvc.perform(
                post("/api/v1/spread")
                        .header("X-ROOM-ID", roomId)
                        .header("X-USER-ID", userId)
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                        .content(objectToJson(spreadReq))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status", is(201)))
                .andExpect(jsonPath("data.token", hasLength(3)))
        ;
    }

    @Test
    @DisplayName("뿌리기 조회 후 응답값 결과 받기")
    public void spreadApi2() throws Exception {
        //given

        long pickId = 486L;

        //뿌리기
        String token = spreadService.spread(roomId, userId, spreadReq);
        Spread.Token resToken = new Spread.Token().builder().token(token).build();

        //줍기
        long pickAmount = pickupService.pickup(roomId,pickId,resToken);

        //조회
        //when
        mockMvc.perform(
                post("/api/v1/info")
                        .header("X-ROOM-ID", roomId)
                        .header("X-USER-ID", userId)
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                        .content(objectToJson(resToken))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(200)));
    }
}