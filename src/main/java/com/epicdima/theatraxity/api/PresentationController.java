package com.epicdima.theatraxity.api;

import com.epicdima.lib.controller.Controller;
import com.epicdima.lib.controller.annotations.RequestBody;
import com.epicdima.lib.controller.annotations.RequestMapping;
import com.epicdima.lib.controller.annotations.RequestParam;
import com.epicdima.lib.controller.annotations.ResponseBody;
import com.epicdima.lib.di.ObjectInjector;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.PresentationDto;
import com.epicdima.theatraxity.helpers.ControllerUtils;
import com.epicdima.theatraxity.services.PresentationService;
import com.epicdima.theatraxity.services.VerifyUserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author EpicDima
 */
@WebServlet(value = "/api/presentations/*", name = "PresentationController", asyncSupported = true)
public final class PresentationController extends Controller {
    @Inject
    private VerifyUserService verifyUserService;
    @Inject
    private PresentationService presentationService;

    @Override
    public void init() {
        super.init();
        ObjectInjector.inject(this);
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.PUT)
    public Result<PresentationDto> change(@RequestBody PresentationDto requestDto, HttpServletRequest req,
                                          HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> presentationService.changePresentation(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.POST)
    public Result<PresentationDto> create(@RequestBody PresentationDto requestDto, HttpServletRequest req,
                                          HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> presentationService.createPresentation(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.DELETE)
    public Result<Void> delete(@RequestBody PresentationDto requestDto, HttpServletRequest req,
                               HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> presentationService.deletePresentation(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/")
    public Result<List<PresentationDto>> get(@RequestParam(value = "id", nullable = true) Integer id,
                                             @RequestParam(value = "begin", nullable = true) String begin,
                                             @RequestParam(value = "end", nullable = true) String end,
                                             @RequestParam(value = "playId", nullable = true) Integer playId,
                                             @RequestParam(value = "authorId", nullable = true) Integer authorId,
                                             @RequestParam(value = "genreId", nullable = true) Integer genreId,
                                             @RequestParam(value = "deleted", nullable = true) Boolean deleted,
                                             @RequestParam(value = "sort", nullable = true) String sort,
                                             @RequestParam(value = "desc", defaultValue = "false") boolean desc,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             HttpServletRequest req, HttpServletResponse resp) {
        if (id != null) {
            return Result.identity(ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> presentationService.getPresentationById(id, currentUser)));
        } else {
            if (ControllerUtils.isNotAuthorized(req)) {
                return presentationService.getPresentations(begin, end, playId,
                        authorId, genreId, deleted, sort, desc, page, null);
            } else {
                return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                        currentUser -> presentationService.getPresentations(begin, end, playId,
                                authorId, genreId, deleted, sort, desc, page, currentUser));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/restore", type = RequestMethod.POST)
    public Result<PresentationDto> restore(@RequestBody PresentationDto requestDto, HttpServletRequest req,
                                           HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> presentationService.restorePresentation(requestDto, currentUser));
    }
}
