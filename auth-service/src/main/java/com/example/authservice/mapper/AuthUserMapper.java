package com.example.authservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AuthUserMapper {

    Map<String, Object> findCredentials(@Param("coCd") String coCd, @Param("usrId") String usrId);
}
