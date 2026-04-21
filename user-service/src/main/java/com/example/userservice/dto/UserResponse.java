package com.example.userservice.dto;

public record UserResponse(
        String usrId,
        String coCd,
        String usrNm,
        String fullNm,
        String usrEml,
        String mphnNo,
        Integer age,
        String actFlg,
        String imgUrl,
        String cntCd,
        String ctyNm,
        String locCd,
        String ofcCd,
        String empeNo,
        String empeTpCd
) {
}
