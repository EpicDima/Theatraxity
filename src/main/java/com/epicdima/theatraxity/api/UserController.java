package com.epicdima.theatraxity.api;

import com.epicdima.lib.controller.Controller;
import com.epicdima.lib.controller.annotations.RequestBody;
import com.epicdima.lib.controller.annotations.RequestMapping;
import com.epicdima.lib.controller.annotations.RequestParam;
import com.epicdima.lib.controller.annotations.ResponseBody;
import com.epicdima.lib.di.ObjectInjector;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.UserDto;
import com.epicdima.theatraxity.dto.ChangeYourselfDto;
import com.epicdima.theatraxity.helpers.ControllerUtils;
import com.epicdima.theatraxity.model.SessionUser;
import com.epicdima.theatraxity.services.UserService;
import com.epicdima.theatraxity.services.VerifyUserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author EpicDima
 */
@WebServlet(value = "/api/users/*", name = "UserController", asyncSupported = true)
public final class UserController extends Controller {
    @Inject
    private VerifyUserService verifyUserService;
    @Inject
    private UserService userService;

    @Override
    public void init() {
        super.init();
        ObjectInjector.inject(this);
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.PUT)
    public Result<UserDto> change(@RequestBody UserDto requestDto, HttpServletRequest req,
                                  HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> userService.changeUser(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/profile", type = RequestMethod.PUT)
    public Result<UserDto> changeYourself(@RequestBody ChangeYourselfDto requestDto, HttpServletRequest req,
                                          HttpServletResponse resp) {
        Result<UserDto> result = ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> userService.changeYourself(requestDto, currentUser));
        if (result.isSuccess()) {
            UserDto responseDto = result.getValue();
            SessionUser user = new SessionUser(responseDto.id, responseDto.email, responseDto.role);
            ControllerUtils.setUserToSession(req, user);
            ControllerUtils.getAndVerifyCurrentUser(verifyUserService.getRawUserUseCase(), req, resp);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.DELETE)
    public Result<Void> delete(@RequestBody UserDto requestDto, HttpServletRequest req,
                               HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp, currentUser -> {
            Result<Void> result = userService.deleteUser(requestDto, currentUser);
            if (result.isSuccess()) {
                SessionUser.removeSession(requestDto.id);
            }
            return result;
        });
    }

    @ResponseBody
    @RequestMapping(value = "/profile", type = RequestMethod.DELETE)
    public Result<Void> deleteYourself(HttpServletRequest req, HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp, currentUser -> {
            Result<Void> result = userService.deleteYourself(currentUser);
            if (result.isSuccess()) {
                ControllerUtils.clearCookies(resp);
                currentUser.invalidateSession();
            }
            return result;
        });
    }

    @ResponseBody
    @RequestMapping("/")
    public Result<List<UserDto>> get(
            @RequestParam(value = "id", nullable = true) Integer userId,
            @RequestParam(value = "role", nullable = true) String role,
            @RequestParam(value = "deleted", nullable = true) Boolean deleted,
            @RequestParam(value = "sort", nullable = true) String sort,
            @RequestParam(value = "desc", defaultValue = "false") boolean desc,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpServletRequest req, HttpServletResponse resp) {
        if (userId != null) {
            return Result.identity(ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> userService.getUserById(userId, currentUser)));
        } else {
            return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> userService.getUsers(role, deleted, sort, desc, page, currentUser));
        }
    }

    @ResponseBody
    @RequestMapping("/profile")
    public Result<UserDto> getYourself(HttpServletRequest req, HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> userService.getYourself(currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/restore", type = RequestMethod.POST)
    public Result<UserDto> restore(@RequestBody UserDto requestDto,
                                   HttpServletRequest req, HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> userService.restoreUser(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/signIn", type = RequestMethod.POST)
    public Result<UserDto> signIn(@RequestBody UserDto requestDto, HttpServletRequest req,
                                  HttpServletResponse resp) {
        if (ControllerUtils.isNotAuthorized(req)) {
            Result<UserDto> result = ControllerUtils.proccessErrorCodes(userService.signIn(requestDto), resp);
            if (result.isSuccess()) {
                UserDto responseDto = result.getValue();
                SessionUser user = new SessionUser(responseDto.id, responseDto.email, responseDto.role);
                ControllerUtils.setUserToSession(req, user);
                ControllerUtils.getAndVerifyCurrentUser(verifyUserService.getRawUserUseCase(), req, resp);
            }
            return result;
        } else {
            ControllerUtils.sendForbidden(resp);
            return Result.empty();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.POST)
    public Result<UserDto> signUp(@RequestBody UserDto requestDto, HttpServletRequest req,
                                  HttpServletResponse resp) {
        if (ControllerUtils.isNotAuthorized(req)) {
            Result<UserDto> result = ControllerUtils.proccessErrorCodes(userService.signUpYourself(requestDto), resp);
            if (result.isSuccess()) {
                UserDto responseDto = result.getValue();
                SessionUser user = new SessionUser(responseDto.id, responseDto.email, responseDto.role);
                ControllerUtils.setUserToSession(req, user);
                ControllerUtils.getAndVerifyCurrentUser(verifyUserService.getRawUserUseCase(), req, resp);
            }
            return result;
        } else {
            SessionUser sessionUser = ControllerUtils.getAndVerifyCurrentUser(verifyUserService.getRawUserUseCase(), req, resp);
            if (sessionUser != null && sessionUser.toUser().isAdminOrManager()) {
                return ControllerUtils.proccessErrorCodes(userService.signUp(requestDto, sessionUser), resp);
            } else {
                ControllerUtils.sendForbidden(resp);
                return Result.empty();
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/signOut", type = RequestMethod.POST)
    public Result<Void> signOut(HttpServletRequest req, HttpServletResponse resp) {
        if (ControllerUtils.isAuthorized(req)) {
            SessionUser sessionUser = ControllerUtils.getAndVerifyCurrentUser(
                    verifyUserService.getRawUserUseCase(), req, resp);
            if (sessionUser != null) {
                sessionUser.invalidateSession();
            }
        } else {
            ControllerUtils.sendUnauthorized(resp);
        }
        ControllerUtils.clearCookies(resp);
        return Result.empty();
    }
}
