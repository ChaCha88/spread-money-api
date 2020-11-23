package me.chawon.kakaopay.spread.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.chawon.kakaopay.global.error.exception.ErrorCode;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommonApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Spread.Request spreadReq;
    private Spread.Request spreadReq2;
    private String roomId;
    private long userId;

    private String objectToJson(Object inObject) throws JsonProcessingException {
        return objectMapper.writeValueAsString(inObject);
    }

    @BeforeEach
    public void setUp() {
        //given
        spreadReq = Spread.Request.builder().amount(2000L).person(2).build();
        spreadReq2 = Spread.Request.builder().person(2).build();

        roomId = "_kakkaoPay001";
        userId = 7777L;
    }

    @Test
    @DisplayName("헤더 정보 누락")
    public void commonApi1() throws Exception {
        //given(before)
        //spreadReq

        //when
        mockMvc.perform(
                post("/api/v1/spread")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectToJson(spreadReq))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(ErrorCode.MISSING_REQUEST.getStatus())))
                .andExpect(jsonPath("$.code", is(ErrorCode.MISSING_REQUEST.getCode())))
                .andExpect(jsonPath("$.message", is(ErrorCode.MISSING_REQUEST.getMessage())))
        ;
    }

    @Test
    @DisplayName("헤더 정보 타입 변경")
    public void commonApi2() throws Exception {
        //given(before)
        //spreadReq
        String typeChangeUserId = "kris";

        //when
        mockMvc.perform(
                post("/api/v1/spread")
                        .header("X-ROOM-ID", roomId)
                        .header("X-USER-ID", typeChangeUserId)
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                        .content(objectToJson(spreadReq))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(ErrorCode.TYPE_MISMATCH.getStatus())))
                .andExpect(jsonPath("$.code", is(ErrorCode.TYPE_MISMATCH.getCode())))
                .andExpect(jsonPath("$.message", is(ErrorCode.TYPE_MISMATCH.getMessage())))
        ;
    }

    @Test
    @DisplayName("요청 값 검증")
    public void commonApi3() throws Exception {
        //given(before)
        //spreadReq2

        //when
        mockMvc.perform(
                post("/api/v1/spread")
                        .header("X-ROOM-ID", roomId)
                        .header("X-USER-ID", userId)
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                        .content(objectToJson(spreadReq2))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getStatus())))
                .andExpect(jsonPath("$.code", is(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getCode())))
                .andExpect(jsonPath("$.message", is(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage())))
        ;
    }
}
