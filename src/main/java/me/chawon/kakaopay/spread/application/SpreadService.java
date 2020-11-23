package me.chawon.kakaopay.spread.application;


import me.chawon.kakaopay.spread.dto.Spread;

public interface SpreadService {

    /**
     * 뿌리기
     * @param roomId 방이름
     * @param userId 사용자 ID
     * @param req    금액(amount), 인원(person)
     *
     * @return 3자리 생성토큰
     * **/
    String spread(String roomId, long userId, Spread.Request req);

    /**
     * 조회
     * @param roomId 방이름
     * @param userId 사용자 ID
     * @param req    3자리 토큰(token)
     *
     * @return 뿌린 정보
     * **/
    Spread.Dto info(String roomId, long userId, Spread.Token req);

}
