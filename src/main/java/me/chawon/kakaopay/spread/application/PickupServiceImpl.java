package me.chawon.kakaopay.spread.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chawon.kakaopay.global.error.exception.BusinessException;
import me.chawon.kakaopay.global.error.exception.ErrorCode;
import me.chawon.kakaopay.spread.dao.PickupRepository;
import me.chawon.kakaopay.spread.dao.SpreadRepository;
import me.chawon.kakaopay.spread.domain.PickupEntity;
import me.chawon.kakaopay.spread.domain.SpreadEntity;
import me.chawon.kakaopay.spread.dto.Spread;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PickupServiceImpl implements PickupService {

    private final SpreadRepository spreadRepository;
    private final PickupRepository pickupRepository;

    @Transactional
    public long pickup(String roomId, long userId, Spread.Token dto){

        /**
         *  줍기는 10분동안만 유효
         **/
        long expiredTime = 10L;

        /**
         *  뿌리기 시 발급된 token을 요청값으로 받습니다.
         *  PICK01 : 토큰의 정보가 유효하지 않음
         **/
        SpreadEntity spread =
                spreadRepository
                        .findByToken(dto.getToken())
                        .orElseThrow(()-> new BusinessException(ErrorCode.PICK001));

        /**
         *  PICK02 : 뿌린기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수있습니다.
         *  PICK03 : 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다.
         *  PICK04 : 자신이 뿌리기한 건은 자신이 받을 수 없습니다.
         *  PICK05 : 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.
         *  PICK06 : 모두 다 주워간 경우는 주울수없다
         **/
        if( spread.isDifferentRoom( roomId ) ) {  // 뿌린기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수있습니다.
            throw new BusinessException(ErrorCode.PICK002);
        }
        if( spread.isPickUpPossible( expiredTime ) ) {  // 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다.
            throw new BusinessException(ErrorCode.PICK003);
        }
        if ( userId == spread.getUserId() ) {    // 자신이 뿌리기한 건은 자신이 받을 수 없습니다.
            throw new BusinessException(ErrorCode.PICK004);
        }
        if( spread.getPickups().stream().filter(PickupEntity::isPicked).anyMatch(p -> p.getPickId().equals(userId) ) ){   // 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.
            throw new BusinessException(ErrorCode.PICK005);
        }
        if ( spread.getPerson() == spread.getPickups().stream().filter(PickupEntity::isPicked).count()){ // 줍기 마감
            throw  new BusinessException(ErrorCode.PICK006);
        }

        /**
         * token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를 API를 호출한 사용자에게 할당하고,
         * 그 금액을 응답값으로 내려줍니다.
         * **/
        List<PickupEntity> pick = spread.getPickups().stream()
                                    .filter(pickupEntity -> ! pickupEntity.isPicked())
                                    .collect(Collectors.toList());

        int rand =  new Random().nextInt(pick.size());

        PickupEntity pickup = pick.get(rand);
        pickup.updatePickup(userId);

        pickupRepository.save(pickup);

        return pickup.getAmount();
    }
}
