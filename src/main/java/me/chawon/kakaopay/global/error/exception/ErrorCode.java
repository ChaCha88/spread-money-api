package me.chawon.kakaopay.global.error.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    //Common
    MISSING_REQUEST(400, "MissingRequestHeaderException", "Header 정보가 누락되었습니다."),
    TYPE_MISMATCH(400, "MethodArgumentTypeMismatchException", "Header 정보가 올바르지 않습니다."),
    METHOD_ARGUMENT_NOT_VALID(400,"MethodArgumentNotValidException","Body 정보가 올바르지 않습니다."),
    INVALID_FORMAT(400,"InvalidFormatException", "입력 형식이 올바르지 않습니다."),
    //Spread
    SP001(500, "SP001", "인원수 보다 많은 금액을 입력하세요."),

    //Spread Info
    SI001(404, "SI001", "뿌리기 정보가 존재하지 않습니다."),
    SI002(404, "SI002", "뿌리기 정보가 존재하지 않습니다."),
    SI003(500, "SI003", "뿌린 사람 자신만 조회를 할 수 있습니다."),
    SI004(500, "SI004", "뿌린 건에 대한 조회는 7일 동안 할 수 있습니다."),

    //pickup
    PICK001(404, "PICK001", "뿌리기 정보가 존재 하지 않습니다."),
    PICK002(400, "PICK002", "다른 방의 사용자는 받을 수 없습니다."),
    PICK003(500, "PICK003", "뿌린 건은 10분간만 유효합니다."),
    PICK004(500, "PICK004", "자신이 뿌리기한 건은 자신이 받을 수 없습니다."),
    PICK005(500, "PICK005", "뿌리기 당 한번만 받을 수 있습니다."),
    PICK006(500, "PICK006", "받기가 마감 되었습니다."),
;

    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
