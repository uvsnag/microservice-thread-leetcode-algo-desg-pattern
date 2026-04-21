package com.example.userservice.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.event.UserEventPublisher;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.AdmUsr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserEventPublisher userEventPublisher;

    @InjectMocks
    private UserService userService;

    private AdmUsr sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new AdmUsr();
        sampleUser.setUsrId("admin");
        sampleUser.setCoCd("DEMO");
        sampleUser.setUsrNm("Admin User");
        sampleUser.setFullNm("Admin Full Name");
        sampleUser.setUsrEml("admin@example.com");
        sampleUser.setMphnNo("010-1234-5678");
        sampleUser.setAge(30);
        sampleUser.setActFlg("Y");
        sampleUser.setImgUrl(null);
        sampleUser.setCntCd("KR");
        sampleUser.setCtyNm("Seoul");
        sampleUser.setLocCd("HQ");
        sampleUser.setOfcCd("MAIN");
        sampleUser.setEmpeNo("EMP001");
        sampleUser.setEmpeTpCd("FT");
    }

    @Test
    @DisplayName("getAllUsers - returns mapped DTOs and publishes search event")
    void getAllUsers_shouldReturnMappedDTOs() {
        when(userMapper.findAll()).thenReturn(List.of(sampleUser));
        doNothing().when(userEventPublisher).publishSearchEvent(anyString(), any(), anyInt());

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).usrId()).isEqualTo("admin");
        assertThat(result.get(0).coCd()).isEqualTo("DEMO");
        assertThat(result.get(0).usrNm()).isEqualTo("Admin User");
        assertThat(result.get(0).age()).isEqualTo(30);
        verify(userEventPublisher).publishSearchEvent("LIST_ALL", null, 1);
    }

    @Test
    @DisplayName("getAllUsers - returns empty list when no users")
    void getAllUsers_shouldReturnEmptyList() {
        when(userMapper.findAll()).thenReturn(Collections.emptyList());
        doNothing().when(userEventPublisher).publishSearchEvent(anyString(), any(), anyInt());

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).isEmpty();
        verify(userEventPublisher).publishSearchEvent("LIST_ALL", null, 0);
    }

    @Test
    @DisplayName("getUserByKey - returns user when found")
    void getUserByKey_shouldReturnUser() {
        when(userMapper.findByCompositeKey("DEMO", "admin")).thenReturn(sampleUser);
        doNothing().when(userEventPublisher).publishViewEvent(anyString(), anyString());

        Optional<UserResponse> result = userService.getUserByKey("DEMO", "admin");

        assertThat(result).isPresent();
        assertThat(result.get().usrId()).isEqualTo("admin");
        verify(userEventPublisher).publishViewEvent("DEMO", "admin");
    }

    @Test
    @DisplayName("getUserByKey - returns empty when not found")
    void getUserByKey_shouldReturnEmptyWhenNotFound() {
        when(userMapper.findByCompositeKey("DEMO", "unknown")).thenReturn(null);

        Optional<UserResponse> result = userService.getUserByKey("DEMO", "unknown");

        assertThat(result).isEmpty();
        verify(userEventPublisher, never()).publishViewEvent(anyString(), anyString());
    }

    @Test
    @DisplayName("getUsersByCompany - returns filtered list")
    void getUsersByCompany_shouldReturnFilteredList() {
        when(userMapper.findByCoCd("DEMO")).thenReturn(List.of(sampleUser));
        doNothing().when(userEventPublisher).publishSearchEvent(anyString(), any(), anyInt());

        List<UserResponse> result = userService.getUsersByCompany("DEMO");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).coCd()).isEqualTo("DEMO");
        verify(userEventPublisher).publishSearchEvent("BY_COMPANY", "DEMO", 1);
    }

    @Test
    @DisplayName("searchUsers - returns matching users")
    void searchUsers_shouldReturnMatchingUsers() {
        when(userMapper.searchByKeyword("admin")).thenReturn(List.of(sampleUser));
        doNothing().when(userEventPublisher).publishSearchEvent(anyString(), any(), anyInt());

        List<UserResponse> result = userService.searchUsers("admin");

        assertThat(result).hasSize(1);
        verify(userEventPublisher).publishSearchEvent("KEYWORD", "admin", 1);
    }

    @Test
    @DisplayName("getUsersByAge - returns users with specified age")
    void getUsersByAge_shouldReturnUsersByAge() {
        when(userMapper.findByAge(30)).thenReturn(List.of(sampleUser));

        List<UserResponse> result = userService.getUsersByAge(30);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).age()).isEqualTo(30);
    }

    @Test
    @DisplayName("countUsers - returns total count")
    void countUsers_shouldReturnCount() {
        when(userMapper.countAll()).thenReturn(42L);

        long count = userService.countUsers();

        assertThat(count).isEqualTo(42L);
    }
}
