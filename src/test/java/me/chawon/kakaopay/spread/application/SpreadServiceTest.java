package me.chawon.kakaopay.spread.application;

import me.chawon.kakaopay.global.error.exception.BusinessException;
import me.chawon.kakaopay.global.error.exception.ErrorCode;
import me.chawon.kakaopay.spread.dao.SpreadRepository;
import me.chawon.kakaopay.spread.domain.SpreadEntity;
import me.chawon.kakaopay.spread.dto.Spread;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
public class SpreadServiceTest {

    @Autowired
    private SpreadService spreadService;

    @Autowired
    private PickupService pickupService;

    @Autowired
    private SpreadRepository spreadRepository;



    private Spread.Request spreadReq;
    private String roomId;
    private long userId;
    private long amount;
    private int person;

    @BeforeEach
    void setUp(){
        roomId = "dream0070";
        userId = 22L;
        amount = 100L;
        person = 4;

        spreadReq = Spread.Request.builder().amount(amount).person(person).build();
    }

    @Test
    @DisplayName("뿌리기 요청 후 토큰 3자리 받기")
    void spreadService1(){

        String token = spreadService.spread(roomId, userId, spreadReq);

        SpreadEntity spread = spreadRepository.findByToken(token).orElseThrow(() -> new AssertionError("TEST 실패"));

        assertThat(spread.getToken(), IsEqual.equalTo(token));

        assertThat(spread.getRoomId(), IsEqual.equalTo(roomId));
        assertThat(spread.getUserId(), IsEqual.equalTo(userId));

        assertThat(spread.getAmount(), IsEqual.equalTo(spreadReq.getAmount()));
        assertThat(spread.getPerson(), IsEqual.equalTo(spreadReq.getPerson()));
    }

    @Test
    @DisplayName("인원수 보다 적은 금액을 입력한 케이스")
    void spreadService2(){

        long amount = 2L;
        int person = 5;

        spreadReq = Spread.Request.builder().amount(amount).person(person).build();

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            spreadService.spread(roomId, userId, spreadReq);
        });

        assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.SP001));

    }

    @Test
    @DisplayName("뿌리기 조회 후 응답값 결과 받기")
    void spreadService3(){

        Spread.Token resToken = new Spread.Token().builder().token("gcM").build();

        long pickId = 5959L;
        long amount = pickupService.pickup(roomId, pickId, resToken);

        Spread.Dto spreadInfo = spreadService.info(roomId, userId, resToken);

        assertThat(spreadInfo.getAmount(), IsEqual.equalTo(spreadReq.getAmount()));
        assertThat(spreadInfo.getPickupAmount(), IsEqual.equalTo(amount));
        assertThat(spreadInfo.getSpreadTime(),  is(notNullValue()));
    }

    @Test
    @DisplayName("뿌리기 조회 결과가 존재하지 않음 - 조회결과 없음")
    void spreadService4(){

        Spread.Token resToken = new Spread.Token().builder().token("F)e").build();

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            spreadService.info(roomId, userId, resToken);
        });

        assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.SI001));
    }

    @Test
    @DisplayName("뿌리기 조회 결과가 존재하지 않음2 - 방정보가 다른 케이스")
    void spreadService5(){

        String token = spreadService.spread(roomId, userId, spreadReq);
        Spread.Token resToken = new Spread.Token().builder().token(token).build();
        String otherRoomId = "otherRoom";

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            spreadService.info(otherRoomId, userId, resToken);
        });
        assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.SI002));
    }

    @Test
    @DisplayName("뿌린 유저가 아닌 유저가 조회하는 케이스")
    void spreadService6(){

        String token = spreadService.spread(roomId, userId, spreadReq);
        Spread.Token resToken = new Spread.Token().builder().token(token).build();
        long otherUserId = 666L;

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            spreadService.info(roomId, otherUserId, resToken);
        });
        assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.SI003));
    }

    @Test
    @DisplayName("7일이 지난 뿌리기 조회 불가능")
    void spreadService7(){

        String token = spreadService.spread(roomId, userId, spreadReq);

        SpreadEntity spread = spreadRepository.findByToken(token).orElseThrow(() -> new AssertionError("TEST 실패"));
        spread.setCreateAt(LocalDateTime.now().minusSeconds((60L * 60L * 24L * 7L) + 1));

        Spread.Token resToken = new Spread.Token().builder().token(token).build();

        BusinessException bex = assertThrows(BusinessException.class, () ->{
            spreadService.info(roomId, userId, resToken);
        });

        assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.SI004));
    }

}
