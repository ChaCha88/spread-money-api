package me.chawon.kakaopay.spread.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.chawon.kakaopay.spread.application.SpreadService;
import me.chawon.kakaopay.spread.support.Response;
import me.chawon.kakaopay.spread.dto.Spread;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api("뿌리기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class SpreadApi {

    private final SpreadService spreadService;

    /**
     * 뿌리기 (POST) /api/v1/spread
     **/
    @ApiOperation(value = "뿌리기 요청", notes = "뿌리기 한다")
    @PostMapping("/spread")
    public ResponseEntity<Response> spread(
            @ApiParam(value = "방 정보", required = true, example = "mario7070") @RequestHeader("X-ROOM-ID") String roomId,
            @ApiParam(value = "사용자 정보", required = true, example = "7297") @RequestHeader("X-USER-ID") long userId,
            @ApiParam(value = "뿌리기 요청 정보", required = true) @RequestBody @Valid final Spread.Request req
    ){
        String token = spreadService.spread(roomId, userId, req);

        Response apiResponse = new Response(new Spread.Token().builder().token(token).build(),HttpStatus.CREATED.value());

        return new ResponseEntity(apiResponse, HttpStatus.CREATED);
    }

    /**
     * 조회 (POST) /api/v1/info
     **/
    @ApiOperation(value = "뿌리기 정보 조회 요청", notes = "뿌리기 한 정보를 조회한다")
    @PostMapping("/info")
    public ResponseEntity<Response> info(
            @ApiParam(value = "방 정보", required = true, example = "BC88") @RequestHeader("X-ROOM-ID") String roomId,
            @ApiParam(value = "사용자 정보", required = true, example = "23") @RequestHeader("X-USER-ID") Long userId,
            @ApiParam(value = "3자리 토큰", required = true) @RequestBody @Valid final Spread.Token req
    ){
        Spread.Dto spreadDto = spreadService.info(roomId, userId, req);

        Response apiResponse = new Response(spreadDto, HttpStatus.OK.value());

        return new ResponseEntity(apiResponse,HttpStatus.OK);
    }
}
