package com.example.popupmap.service;

import com.example.popupmap.domain.User;
import com.example.popupmap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    public void register(String username, String nickname, String email, String rawPassword) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        if (userRepository.existsByNickname(nickname))
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");

        userRepository.save(User.builder()
                .username(username)
                .nickname(nickname)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .build());
    }

    public String getNicknameByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getNickname)
                .orElse(username);
    }
}
