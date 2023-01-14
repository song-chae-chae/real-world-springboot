package com.chaechae.realworldspringboot.user.repository;

import com.chaechae.realworldspringboot.config.TestConfig;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @AfterEach
    void afterClean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    public void 회원가입_성공() throws Exception {
        //given
        User user = User.builder()
                .role(Role.USER)
                .name("이름")
                .email("email@com")
                .image("imagesource")
                .socialId("kakaoid").build();
        User savedUser = userRepository.save(user);

        //when
        User findUser = userRepository.findById(savedUser.getId()).orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        //then
        assertThat(findUser).isSameAs(savedUser);
        assertThat(findUser).isSameAs(user);
    }

    @Test
    @DisplayName("socialId로 회원 찾기")
    public void socialId_회원_찾기() throws Exception {
        //given
        User user = User.builder()
                .role(Role.USER)
                .name("이름")
                .email("email@com")
                .image("imagesource")
                .socialId("kakaoid").build();
        User savedUser = userRepository.save(user);

        //when
        User findUser = userRepository.findBySocialId("kakaoid").orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        //then
        assertThat(findUser).isSameAs(savedUser);
        assertThat(findUser).isSameAs(user);
    }

    @Test
    @DisplayName("회원 생성 시 시간 저장")
    public void 회원_생성_시간_저장() throws Exception {
        //given
        User user = User.builder()
                .role(Role.USER)
                .name("이름")
                .email("email@com")
                .image("imagesource")
                .socialId("kakaoid").build();
        User savedUser = userRepository.save(user);

        //when
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        //then
        assertThat(findUser.getCreatedAt()).isNotNull();
        assertThat(findUser.getModifiedAt()).isNotNull();
    }

}