package com.vialink.atlantis.authorizationserver.service;

import com.vialink.atlantis.authorizationserver.user.User;
import org.springframework.stereotype.Service;

@Service
public class DummyMFAService implements MFAService {

    @Override
    public boolean check(User user, String code) {
        return code.equals(user.getUsername());
    }

}
