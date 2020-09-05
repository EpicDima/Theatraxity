package com.epicdima.theatraxity.helpers;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.usecases.user.GetRawUserUseCase;
import com.epicdima.theatraxity.model.SessionUser;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * @author EpicDima
 */
public final class ControllerUtils {
    private static final String USER_SESSION_KEY = "current_user";

    private static final String ID_KEY = "id";
    private static final String EMAIL_KEY = "email";
    private static final String ROLE_KEY = "role";

    private ControllerUtils() {
        throw new AssertionError();
    }

    public static <T> Result<T> proccessErrorCodes(Result<T> result, HttpServletResponse response) {
        if (result.isFailure()) {
            sendErrorStatus(response, result.asFailure().getValue());
        }
        return result;
    }

    public static void sendErrorStatus(HttpServletResponse response, int errorCode) {
        switch (errorCode) {
            case SC_BAD_REQUEST:
                sendBadRequest(response);
                break;
            case SC_UNAUTHORIZED:
                sendUnauthorized(response);
                break;
            case SC_FORBIDDEN:
                sendForbidden(response);
                break;
            case SC_NOT_FOUND:
                sendNotFound(response);
                break;
            case SC_INTERNAL_SERVER_ERROR:
                sendInternalServerError(response);
                break;
        }
    }

    public static void sendBadRequest(HttpServletResponse resp) {
        resp.setStatus(SC_BAD_REQUEST);

    }

    public static void sendUnauthorized(HttpServletResponse resp) {
        resp.setStatus(SC_UNAUTHORIZED);
    }

    public static void sendForbidden(HttpServletResponse resp) {
        resp.setStatus(SC_FORBIDDEN);
    }

    public static void sendNotFound(HttpServletResponse resp) {
        resp.setStatus(SC_NOT_FOUND);
    }

    public static void sendInternalServerError(HttpServletResponse resp) {
        resp.setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    public static void setUserToSession(HttpServletRequest req, SessionUser user) {
        req.getSession().setAttribute(USER_SESSION_KEY, user);
    }

    public static SessionUser getUserFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session == null) {
            return null;
        }
        return (SessionUser) session.getAttribute(USER_SESSION_KEY);
    }

    public static boolean isAuthorized(HttpServletRequest req) {
        return getUserFromSession(req) != null;
    }

    public static boolean isNotAuthorized(HttpServletRequest req) {
        return !isAuthorized(req);
    }

    public static void closeSessionWithError(HttpServletRequest req, HttpServletResponse resp) {
        req.getSession().invalidate();
        sendNotFound(resp);
    }
    
    public static SessionUser getAndVerifyCurrentUser(GetRawUserUseCase getRawUserUseCase, HttpServletRequest req,
                                                      HttpServletResponse resp) {
        SessionUser currentSessionUser = getUserFromSession(req);
        if (currentSessionUser == null) {
            clearCookies(resp);
            sendUnauthorized(resp);
            return null;
        }
        User currentUser = getRawUserUseCase.execute(currentSessionUser.getId());
        if (currentUser != null && !currentUser.isDeleted()
                && currentSessionUser.equalToUser(currentUser)) {
            setCookies(currentUser, resp);
            return currentSessionUser;
        } else {
            clearCookies(resp);
            closeSessionWithError(req, resp);
            return null;
        }
    }

    public static boolean hasAnyAppCookie(HttpServletRequest req) {
        for (Cookie cookie : req.getCookies()) {
            String name = cookie.getName();
            if (ID_KEY.equals(name) || EMAIL_KEY.equals(name) || ROLE_KEY.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static void setCookies(User user, HttpServletResponse resp) {
        resp.addCookie(createSessionCookie(ID_KEY, String.valueOf(user.getId())));
        resp.addCookie(createSessionCookie(EMAIL_KEY, user.getEmail()));
        resp.addCookie(createSessionCookie(ROLE_KEY, user.getRole().toString()));
    }

    public static Cookie createSessionCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        return cookie;
    }

    public static void clearCookies(HttpServletResponse resp) {
        resp.addCookie(createDeadCookie(ID_KEY, null));
        resp.addCookie(createDeadCookie(EMAIL_KEY, null));
        resp.addCookie(createDeadCookie(ROLE_KEY, null));
    }

    public static Cookie createDeadCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    public static <T> Result<T> doWithVerifiedUser(GetRawUserUseCase getRawUserUseCase, HttpServletRequest req,
                                                   HttpServletResponse resp, DoWithVerifiedUser<T> applier) {
        SessionUser currentUser = getAndVerifyCurrentUser(getRawUserUseCase, req, resp);
        if (currentUser != null) {
            try {
                return proccessErrorCodes(applier.apply(currentUser), resp);
            } catch (Exception e) {
                e.printStackTrace();
                return proccessErrorCodes(Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR), resp);
            }
        }
        return proccessErrorCodes(Result.failure(HttpCodes.NOT_FOUND, Codes.NOT_ALLOWED), resp);
    }


    @FunctionalInterface
    public interface DoWithVerifiedUser<T> {
        Result<T> apply(SessionUser currentUser);
    }
}
