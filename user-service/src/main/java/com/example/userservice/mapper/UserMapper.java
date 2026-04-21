package com.example.userservice.mapper;

import java.util.List;

import com.example.userservice.model.AdmUsr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    List<AdmUsr> findAll();

    AdmUsr findByCompositeKey(@Param("coCd") String coCd, @Param("usrId") String usrId);

    List<AdmUsr> findByCoCd(@Param("coCd") String coCd);

    List<AdmUsr> searchByKeyword(@Param("keyword") String keyword);

    List<AdmUsr> findByAge(@Param("age") Integer age);

    long countAll();
}
