package com.epicdima.theatraxity.api;

import com.epicdima.lib.controller.Controller;
import com.epicdima.lib.controller.annotations.RequestBody;
import com.epicdima.lib.controller.annotations.RequestMapping;
import com.epicdima.lib.controller.annotations.RequestParam;
import com.epicdima.lib.controller.annotations.ResponseBody;
import com.epicdima.lib.di.ObjectInjector;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.PlayDto;
import com.epicdima.theatraxity.helpers.ControllerUtils;
import com.epicdima.theatraxity.services.PlayService;
import com.epicdima.theatraxity.services.VerifyUserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author EpicDima
 */
@WebServlet(value = "/api/plays/*", name = "PlayController", asyncSupported = true)
public final class PlayController extends Controller {
    @Inject
    private VerifyUserService verifyUserService;
    @Inject
    private PlayService playService;

    @Override
    public void init() {
        super.init();
        ObjectInjector.inject(this);
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.PUT)
    public Result<PlayDto> change(@RequestBody PlayDto requestDto, HttpServletRequest req,
                                  HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> playService.changePlay(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.POST)
    public Result<PlayDto> create(@RequestBody PlayDto requestDto, HttpServletRequest req,
                                  HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> playService.createPlay(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.DELETE)
    public Result<Void> delete(@RequestBody PlayDto requestDto, HttpServletRequest req,
                               HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> playService.deletePlay(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/")
    public Result<List<PlayDto>> get(@RequestParam(value = "id", nullable = true) Integer id,
                                     @RequestParam(value = "authorId", nullable = true) Integer authorId,
                                     @RequestParam(value = "genreId", nullable = true) Integer genreId,
                                     @RequestParam(value = "deleted", nullable = true) Boolean deleted,
                                     @RequestParam(value = "sort", nullable = true) String sort,
                                     @RequestParam(value = "desc", defaultValue = "false") boolean desc,
                                     HttpServletRequest req, HttpServletResponse resp) {
        if (id != null) {
            return Result.identity(ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> playService.getPlayById(id, currentUser)));
        } else {
            if (ControllerUtils.isNotAuthorized(req)) {
                return playService.getPlays(authorId, genreId, deleted, sort, desc, null);
            } else {
                return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                        currentUser -> playService.getPlays(authorId, genreId, deleted, sort, desc, currentUser));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/restore", type = RequestMethod.POST)
    public Result<PlayDto> restore(@RequestBody PlayDto requestDto, HttpServletRequest req,
                                   HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> playService.restorePlay(requestDto, currentUser));
    }
}
