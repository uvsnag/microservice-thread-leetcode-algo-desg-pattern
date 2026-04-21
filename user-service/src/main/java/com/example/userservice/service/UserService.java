package com.example.userservice.service;

import java.util.List;
import java.util.Optional;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.event.UserEventPublisher;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.AdmUsr;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserEventPublisher userEventPublisher;

    public UserService(UserMapper userMapper, UserEventPublisher userEventPublisher) {
        this.userMapper = userMapper;
        this.userEventPublisher = userEventPublisher;
    }

    public List<UserResponse> getAllUsers() {
        List<UserResponse> users = userMapper.findAll().stream()
                .map(this::toResponse)
                .toList();
        userEventPublisher.publishSearchEvent("LIST_ALL", null, users.size());
        return users;
    }

    public Optional<UserResponse> getUserByKey(String coCd, String usrId) {
        AdmUsr user = userMapper.findByCompositeKey(coCd, usrId);
        if (user != null) {
            userEventPublisher.publishViewEvent(coCd, usrId);
        }
        return Optional.ofNullable(user).map(this::toResponse);
    }

    public List<UserResponse> getUsersByCompany(String coCd) {
        List<UserResponse> users = userMapper.findByCoCd(coCd).stream()
                .map(this::toResponse)
                .toList();
        userEventPublisher.publishSearchEvent("BY_COMPANY", coCd, users.size());
        return users;
    }

    public List<UserResponse> searchUsers(String keyword) {
        List<UserResponse> users = userMapper.searchByKeyword(keyword).stream()
                .map(this::toResponse)
                .toList();
        userEventPublisher.publishSearchEvent("KEYWORD", keyword, users.size());
        return users;
    }

    public List<UserResponse> getUsersByAge(Integer age) {
        return userMapper.findByAge(age).stream()
                .map(this::toResponse)
                .toList();
    }

    public long countUsers() {
        return userMapper.countAll();
    }

    private UserResponse toResponse(AdmUsr u) {
        return new UserResponse(
                u.getUsrId(), u.getCoCd(), u.getUsrNm(), u.getFullNm(),
                u.getUsrEml(), u.getMphnNo(), u.getAge(), u.getActFlg(),
                u.getImgUrl(), u.getCntCd(), u.getCtyNm(), u.getLocCd(),
                u.getOfcCd(), u.getEmpeNo(), u.getEmpeTpCd()
        );
    }
}
