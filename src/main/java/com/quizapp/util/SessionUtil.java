package com.quizapp.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class SessionUtil {

    private SessionUtil() {
    }

    public static Integer getLoggedInUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object userId = session.getAttribute(AppConstants.SESSION_USER_ID);
        return userId instanceof Integer ? (Integer) userId : null;
    }
}
