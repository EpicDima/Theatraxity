package com.epicdima.theatraxity.api;

import com.epicdima.lib.controller.Controller;
import com.epicdima.lib.controller.annotations.RequestBody;
import com.epicdima.lib.controller.annotations.RequestMapping;
import com.epicdima.lib.controller.annotations.RequestParam;
import com.epicdima.lib.controller.annotations.ResponseBody;
import com.epicdima.lib.di.ObjectInjector;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.AuthorDto;
import com.epicdima.theatraxity.helpers.ControllerUtils;
import com.epicdima.theatraxity.services.AuthorService;
import com.epicdima.theatraxity.services.VerifyUserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author EpicDima
 */
@WebServlet(value = "/api/authors/*", name = "AuthorController", asyncSupported = true)
public final class AuthorController extends Controller {
    @Inject
    private VerifyUserService verifyUserService;
    @Inject
    private AuthorService authorService;

    @Override
    public void init() {
        super.init();
        ObjectInjector.inject(this);
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.PUT)
    public Result<AuthorDto> change(@RequestBody AuthorDto requestDto, HttpServletRequest req,
                                    HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> authorService.changeAuthor(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.POST)
    public Result<AuthorDto> create(@RequestBody AuthorDto requestDto, HttpServletRequest req,
                                    HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> authorService.createAuthor(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.DELETE)
    public Result<Void> delete(@RequestBody AuthorDto requestDto, HttpServletRequest req,
                               HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> authorService.deleteAuthor(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/")
    public Result<List<AuthorDto>> get(@RequestParam(value = "id", nullable = true) Integer id,
                                       @RequestParam(value = "deleted", nullable = true) Boolean deleted,
                                       @RequestParam(value = "sort", nullable = true) String sort,
                                       @RequestParam(value = "desc", defaultValue = "false") boolean desc,
                                       HttpServletRequest req, HttpServletResponse resp) {
        if (id != null) {
            return Result.identity(ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> authorService.getAuthorById(id, currentUser)));
        } else {
            if (ControllerUtils.isNotAuthorized(req)) {
                return authorService.getAuthors(deleted, sort, desc, null);
            } else {
                return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                        currentUser -> authorService.getAuthors(deleted, sort, desc, currentUser));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/restore", type = RequestMethod.POST)
    public Result<AuthorDto> restore(@RequestBody AuthorDto requestDto, HttpServletRequest req,
                                     HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> authorService.restoreAuthor(requestDto, currentUser));
    }
}
