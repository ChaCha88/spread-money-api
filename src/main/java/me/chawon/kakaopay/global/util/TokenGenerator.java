package me.chawon.kakaopay.global.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {
    /**
    * 랜덤 토큰 생성, 특수문자를 추가하여 경우의 수를 늘려줌 [사용이 불가한 특문은 제거 ex) \,<,>,[,] ]
    **/
    public String generate(int count) {
        return RandomStringUtils.random(count, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_-+={}|':;.?/");
    }
}
