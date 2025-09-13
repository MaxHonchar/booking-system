package com.test.booking.service.impl;

import com.test.booking.domain.User;
import com.test.booking.repository.IUserRepository;
import com.test.booking.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    @Transactional
    @Override
    public User getOrSaveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    return userRepository.save(user);
                });
    }
}
