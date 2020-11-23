package me.chawon.kakaopay.spread.application;

import me.chawon.kakaopay.global.error.exception.BusinessException;
import me.chawon.kakaopay.global.error.exception.ErrorCode;
import me.chawon.kakaopay.spread.dao.SpreadRepository;
import me.chawon.kakaopay.spread.domain.SpreadEntity;
import me.chawon.kakaopay.spread.dto.Spread;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
public class PickupServiceTest {

    @Autowired
    private PickupService pickupService;

    @Autowired
    private SpreadRepository spreadRepository;

    private Spread.Request spreadReq;

    private String roomId;
    private long userId;

    private long amount;
    private int person;
    private String token;

    @BeforeEach
    void setUp(){
        roomId = "dream0070";
        userId = 22L;
        amount = 100L;
        person = 4;
        token = "gcM";

        spreadReq = Spread.Request.builder().amount(amount).person(person).build();
    }

    @Test
    @DisplayName("줍기 후 응답값 금액 받기")
    void pickupService1(){

        Spread.Token resToken = new Spread.Token().builder().token(token).build();
        long pickId = 7979L;

        long pickAmount = pickupService.pickup(roomId, pickId, resToken);

        assertThat(pickAmount).isLessThanOrEqualTo(amount);
    }

    @Test
    @DisplayName("다른 방의 사용자 줍기 시도 실패")
    void pickupService2(){

        Spread.Token resToken = new Spread.Token().builder().token(token).build();
        long pickId = 7979L;
        roomId = "pick007";

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            pickupService.pickup(roomId, pickId, resToken);
        });

        MatcherAssert.assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.PICK002));

    }

    @Test
    @DisplayName("10분 유효기간 만료후 시도 실패 케이스")
    void pickupService3(){

        Spread.Token resToken = new Spread.Token().builder().token(token).build();
        long pickId = 7979L;

        SpreadEntity spread = spreadRepository.findByToken(token).orElseThrow(() -> new AssertionError("TEST 실패"));
        spread.setCreateAt(LocalDateTime.now().minusSeconds((600L) + 1));

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            pickupService.pickup(roomId, pickId, resToken);
        });

        MatcherAssert.assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.PICK003));

    }

    @Test
    @DisplayName("자신이 뿌리기한거 줍기 시도 실패 케이스")
    void pickupService4(){

        Spread.Token resToken = new Spread.Token().builder().token(token).build();

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            pickupService.pickup(roomId, userId, resToken);
        });

        MatcherAssert.assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.PICK004));
    }

    @Test
    @DisplayName("두번 줍기 시도 실패")
    void pickupService5(){

        Spread.Token resToken = new Spread.Token().builder().token(token).build();
        long pickId = 7979L;
        long pickAmount = pickupService.pickup(roomId, pickId, resToken);

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            pickupService.pickup(roomId, pickId, resToken);
        });

        MatcherAssert.assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.PICK005));
    }

    @Test
    @DisplayName("마감된 뿌리기 줍기 시도 실패")
    void pickupService6(){

        Spread.Token resToken = new Spread.Token().builder().token(token).build();

        pickupService.pickup(roomId, 1004L, resToken);
        pickupService.pickup(roomId, 4949L, resToken);
        pickupService.pickup(roomId, 2580L, resToken);
        pickupService.pickup(roomId, 1588L, resToken);

        BusinessException bex =  assertThrows(BusinessException.class, () ->{
            pickupService.pickup(roomId, 7942L, resToken);
        });

        MatcherAssert.assertThat(bex.getErrorCode(), IsEqual.equalTo(ErrorCode.PICK006));
    }


}
