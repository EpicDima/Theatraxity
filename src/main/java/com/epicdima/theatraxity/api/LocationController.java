package com.epicdima.theatraxity.api;

import com.epicdima.lib.controller.Controller;
import com.epicdima.lib.controller.annotations.RequestBody;
import com.epicdima.lib.controller.annotations.RequestMapping;
import com.epicdima.lib.controller.annotations.RequestParam;
import com.epicdima.lib.controller.annotations.ResponseBody;
import com.epicdima.lib.di.ObjectInjector;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.LocationDto;
import com.epicdima.theatraxity.domain.dto.TicketDto;
import com.epicdima.theatraxity.helpers.ControllerUtils;
import com.epicdima.theatraxity.services.LocationService;
import com.epicdima.theatraxity.services.VerifyUserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author EpicDima
 */
@WebServlet(value = "/api/locations/*", name = "LocationController", asyncSupported = true)
public final class LocationController extends Controller {
    @Inject
    private VerifyUserService verifyUserService;
    @Inject
    private LocationService locationService;

    @Override
    public void init() {
        super.init();
        ObjectInjector.inject(this);
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.POST)
    public Result<Void> changeCost(@RequestBody LocationDto requestDto, HttpServletRequest req,
                                           HttpServletResponse resp) {
        return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                currentUser -> locationService.changeLocationCost(requestDto, currentUser));
    }

    @ResponseBody
    @RequestMapping(value = "/")
    public Result<List<LocationDto>> get(HttpServletRequest req, HttpServletResponse resp) {
        if (ControllerUtils.isAuthorized(req)) {
            return locationService.getLocations();
        } else {
            ControllerUtils.sendUnauthorized(resp);
            return Result.empty();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/occupied")
    public Result<List<TicketDto>> getOccupied(@RequestParam(value = "presentationId") int presentationId,
                                               HttpServletRequest req, HttpServletResponse resp) {
        if (ControllerUtils.isAuthorized(req)) {
            return locationService.getOccupiedSeats(presentationId);
        } else {
            ControllerUtils.sendUnauthorized(resp);
            return Result.empty();
        }
    }
}
