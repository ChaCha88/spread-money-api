package me.chawon.kakaopay.spread.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

public class Spread {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ApiModel(value = "Spread.Request", description = "뿌리기 전송 값(금액,인원)")
    public static class Request {
        @NotNull
        @Min(value = 1, message = "1원 이상의 금액을 뿌릴수있습니다.")
        @ApiModelProperty(notes = "뿌릴 금액", required = true, example = "2500")
        private Long amount;

        @NotNull
        @ApiModelProperty(notes = "뿌릴 인원", required = true, example = "3")
        @Min(value = 1,message = "1명 이상에게 금액을 뿌릴수있습니다.")
        private int person;
    }

    @Getter
    @RequiredArgsConstructor
    @ApiModel(value = "Spread.Token", description = "3자리 토큰")
    public static class Token {
        @Pattern(regexp = "[\\w~!@#$%^&*()\\-+={}|':;.?/]{3}",message = "유효하지 않는 토큰입니다.")
        @ApiModelProperty(notes = "3자리 토큰", required = true, example = "j6I")
        private String token;

        @Builder
        public Token(String token){
            this.token = token;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @ApiModel(value = "Spread.Dto", description = "뿌리기 정보 조회")
    public static class Dto {

        @ApiModelProperty(notes = "뿌린 시간", required = true, example = "2020-12-25T12:25:12.25")
        private LocalDateTime spreadTime;

        @ApiModelProperty(notes = "뿌린 금액", required = true, example = "3000")
        private long amount;

        @ApiModelProperty(notes = "주운 총 금액", required = true, example = "200")
        private long pickupAmount;

        @ApiModelProperty(notes = "주운 사용자 리스트 정보", required = true)
        private List<Pickup.Info> pickupList;

        @Builder
        public Dto(LocalDateTime spreadTime, Long amount, Long pickupAmount, List<Pickup.Info> pickupList){
            this.spreadTime = spreadTime;
            this.amount = amount;
            this.pickupAmount = pickupAmount;
            this.pickupList = pickupList;
        }
    }

}
