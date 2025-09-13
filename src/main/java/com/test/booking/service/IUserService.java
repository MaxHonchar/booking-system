package com.test.booking.service;

import com.test.booking.domain.User;

public interface IUserService {
    User getOrSaveUser(String email);
}
