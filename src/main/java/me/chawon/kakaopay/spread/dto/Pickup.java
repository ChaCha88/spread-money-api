package me.chawon.kakaopay.spread.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class Pickup {

    @ToString
    @Getter
    @RequiredArgsConstructor
    @ApiModel(value = "Pickup.Info", description = "줍기 요청")
    public static class Info {
        @ApiModelProperty(notes = "주운 금액", example = "200")
        private final long amount;
        @ApiModelProperty(notes = "주운 사용자 아이디", example = "7979")
        private final long pickupId;
    }

    @Getter
    @RequiredArgsConstructor
    @ApiModel(value = "Pickup.Response", description = "줍기 후 리턴값")
    public static class Response {
        @ApiModelProperty(notes = "주운 금액 리턴", example = "300")
        private long amount;

        @Builder
        public Response(long amount){
            this.amount = amount;
        }
    }
}
