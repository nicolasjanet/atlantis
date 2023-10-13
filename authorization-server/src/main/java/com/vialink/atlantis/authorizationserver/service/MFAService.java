package com.vialink.atlantis.authorizationserver.service;

import com.vialink.atlantis.authorizationserver.user.User;

public interface MFAService {

    boolean check(User user, String code);
}
