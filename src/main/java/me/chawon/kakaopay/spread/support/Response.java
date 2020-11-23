package me.chawon.kakaopay.spread.support;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Response {

    private int status;
    private Object data;

    public Response(Object data, int status) {
        this.data = data;
        this.status = status;
    }

}
