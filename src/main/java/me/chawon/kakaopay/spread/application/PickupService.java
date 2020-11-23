package me.chawon.kakaopay.spread.application;

import me.chawon.kakaopay.spread.dto.Spread;

public interface PickupService {

    /**
     * 줍기
     * @param roomId 방이름
     * @param userId 사용자 ID
     * @param req    3자리 토큰(token)
     *
     * @return 줍기 한 금액
     * **/
    long pickup(String roomId, long userId, Spread.Token req);
}
