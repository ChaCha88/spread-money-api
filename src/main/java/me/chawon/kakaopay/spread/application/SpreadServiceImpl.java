package me.chawon.kakaopay.spread.application;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chawon.kakaopay.global.error.exception.BusinessException;
import me.chawon.kakaopay.global.error.exception.ErrorCode;
import me.chawon.kakaopay.global.util.TokenGenerator;
import me.chawon.kakaopay.spread.dao.PickupRepository;
import me.chawon.kakaopay.spread.dao.SpreadRepository;
import me.chawon.kakaopay.spread.domain.PickupEntity;
import me.chawon.kakaopay.spread.domain.SpreadEntity;
import me.chawon.kakaopay.spread.dto.Pickup;
import me.chawon.kakaopay.spread.dto.Spread;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpreadServiceImpl implements SpreadService {

    private final SpreadRepository spreadRepository;
    private final PickupRepository pickupRepository;

    private final TokenGenerator tokenGenerator;

    @Transactional
    public String spread(String roomId, long userId, Spread.Request dto){

        /**
         * token은 3자리 문자열로 구성되며 예측이 불가능해야 합니다
         **/
        String token = tokenGenerator.generate(3);

        /**
         *  뿌릴 금액, 뿌릴 인원을 요청값으로 받습니다.
         **/
        long amount = dto.getAmount();
        int person = dto.getPerson();

        /**
         *  인원수보다 금액이 적은경우 분배가 불가능 (SP001 : 인원수 보다 많은 금액을 입력하세요.)
         **/
        if( amount < person ) {
            throw new BusinessException(ErrorCode.SP001);
        }

        SpreadEntity spread = SpreadEntity.builder()
                                .token(token)
                                .roomId(roomId)
                                .userId(userId)
                                .amount(amount)
                                .person(person)
                                .build();

        /**
         *  뿌릴 금액을 인원수에 맞게 분배하여 저장합니다. (분배 로직은 자유롭게 구현해 주세요.)
         **/
        long[] separateArray = new long[person];
        for(int i=0; i < person; i++){
            /**
             *  받는 인원은 최소 1원씩 받아야한다
             **/
            long randomAmount = RandomUtils.nextLong(1, amount - ( person -1 - i) );

            separateArray[i] = (i < person-1) ? randomAmount : amount;
            amount -= separateArray[i];

            /**
             *  분배 된 금액을 담아준다
             **/
            spread.addPickups(separateArray[i]);
        }

        /**
         * Table : 뿌리기 (Spread), 줍기 (Pickup)에 저장
         * **/
        spreadRepository.save(spread);

        log.info("분배금액 : " + Arrays.toString(separateArray));
        log.info(spread.toString());

        return spread.getToken();
    }

    /**
     *  뿌리기 정보 조회
     **/
    public Spread.Dto info(String roomId, long userId, Spread.Token dto){

        /**
         *  뿌리기 정보 조회 유효기간은 일주일
         **/
        long expiredDay = 7L;

        /**
         *  뿌리기 시 발급된 token을 요청값으로 받습니다.
         *  유효하지 않은 token에 대해서는 조회 실패 응답이 내려가야 합니다.
         **/
        SpreadEntity spread =
                spreadRepository
                        .findByToken(dto.getToken())
                        .orElseThrow(()-> new BusinessException(ErrorCode.SI001)); // 유효하지 않은 token CASE1 : 결과없음

        /**
         *  유효하지 않은 token CASE2 : 방정보가 다른경우
         *  뿌린 사람 자신만 조회를 할 수 있습니다.
         *  뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.
         **/
        if( !roomId.equals(spread.getRoomId()) ) {
            throw new BusinessException(ErrorCode.SI002);  // 유효하지 않은 token CASE2 : 방정보가 다른경우
        }else if( userId != spread.getUserId() ) {
            throw new BusinessException(ErrorCode.SI003);  // 뿌린 사람 자신만 조회를 할 수 있습니다.
        } else if( spread.isShowInfoPossible(expiredDay) ){
            throw new BusinessException(ErrorCode.SI004);  // 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.
        }

        /**
         *  뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은사용자 아이디] 리스트)
         **/
        Spread.Dto spreadDto = new Spread.Dto().builder()
                .spreadTime(spread.getCreateAt())
                .amount(spread.getAmount())
                .pickupAmount(spread.getPickups().stream().filter(PickupEntity::isPicked).mapToLong(PickupEntity::getAmount).sum())
                .pickupList(spread.getPickups().stream().filter(PickupEntity::isPicked).map(p -> new Pickup.Info(p.getAmount(), p.getPickId())).collect(Collectors.toList()))
                .build();

        return spreadDto;
    }


}
