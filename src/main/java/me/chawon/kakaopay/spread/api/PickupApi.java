package me.chawon.kakaopay.spread.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.chawon.kakaopay.spread.application.PickupService;
import me.chawon.kakaopay.spread.support.Response;
import me.chawon.kakaopay.spread.dto.Pickup;
import me.chawon.kakaopay.spread.dto.Spread;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "줍기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class PickupApi {

    private final PickupService pickupService;
    /**
     * 줍기 (PUT) /api/v1/pickup
     **/
    @ApiOperation(value = "줍기 요청", notes = "남이 뿌리기한 금액을 줍는다")
    @PutMapping("/pickup")
    public ResponseEntity<Response> pickup(
            @ApiParam(value = "방 정보", required = true, example = "p0190") @RequestHeader("X-ROOM-ID") String roomId,
            @ApiParam(value = "사용자 정보", required = true, example = "8215") @RequestHeader("X-USER-ID") long userId,
            @ApiParam(value = "3자리 토큰", required = true) @RequestBody @Valid final Spread.Token req
    ){
        long amount = pickupService.pickup(roomId, userId, req);

        Response apiResponse = new Response(new Pickup.Response().builder().amount(amount).build(), HttpStatus.CREATED.value());

        return new ResponseEntity(apiResponse, HttpStatus.CREATED);
    }
}
