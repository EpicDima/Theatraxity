package com.epicdima.theatraxity.api;

import com.epicdima.lib.controller.Controller;
import com.epicdima.lib.controller.annotations.RequestBody;
import com.epicdima.lib.controller.annotations.RequestMapping;
import com.epicdima.lib.controller.annotations.RequestParam;
import com.epicdima.lib.controller.annotations.ResponseBody;
import com.epicdima.lib.di.ObjectInjector;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.OrderDto;
import com.epicdima.theatraxity.helpers.ControllerUtils;
import com.epicdima.theatraxity.services.VerifyUserService;
import com.epicdima.theatraxity.model.SessionUser;
import com.epicdima.theatraxity.services.OrderService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author EpicDima
 */
@WebServlet(value = "/api/orders/*", name = "OrderController", asyncSupported = true)
public final class OrderController extends Controller {
    @Inject
    private VerifyUserService verifyUserService;
    @Inject
    private OrderService orderService;

    @Override
    public void init() {
        super.init();
        ObjectInjector.inject(this);
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.DELETE)
    public Result<Void> cancel(@RequestBody OrderDto requestDto, HttpServletRequest req,
                                    HttpServletResponse resp) {
        SessionUser sessionUser = ControllerUtils.getAndVerifyCurrentUser(verifyUserService.getRawUserUseCase(), req, resp);
        if (sessionUser == null) {
            ControllerUtils.sendNotFound(resp);
            return Result.empty();
        }
        if (sessionUser.toUser().isAdminOrManager()) {
            return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> orderService.cancelOrder(requestDto, currentUser));
        } else {
            return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> orderService.cancelOrderYourself(requestDto, currentUser));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.PUT)
    public Result<Void> confirm(@RequestBody OrderDto requestDto, HttpServletRequest req,
                               HttpServletResponse resp) {
        SessionUser sessionUser = ControllerUtils.getAndVerifyCurrentUser(verifyUserService.getRawUserUseCase(), req, resp);
        if (sessionUser == null) {
            ControllerUtils.sendNotFound(resp);
            return Result.empty();
        }
        if (sessionUser.toUser().isAdminOrManager()) {
            return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> orderService.confirmOrderDelivery(requestDto, currentUser));
        } else {
            return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> orderService.confirmOrderDeliveryYourself(requestDto, currentUser));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/")
    public Result<List<OrderDto>> get(@RequestParam(value = "id", nullable = true) Integer id,
                                      @RequestParam(value = "userId", nullable = true) Integer userId,
                                      @RequestParam(value = "userEmail", nullable = true) String userEmail,
                                      @RequestParam(value = "presentationId", nullable = true) Integer presentationId,
                                      @RequestParam(value = "presentationDate", nullable = true) String presentationDate,
                                      @RequestParam(value = "begin", nullable = true) String begin,
                                      @RequestParam(value = "end", nullable = true) String end,
                                      @RequestParam(value = "status", nullable = true) String status,
                                      @RequestParam(value = "deleted", nullable = true) Boolean deleted,
                                      @RequestParam(value = "sort", nullable = true) String sort,
                                      @RequestParam(value = "desc", defaultValue = "false") boolean desc,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      HttpServletRequest req, HttpServletResponse resp) {
        if (id != null) {
            return Result.identity(ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> orderService.getOrderById(id, currentUser)));
        } else {
            SessionUser sessionUser = ControllerUtils.getAndVerifyCurrentUser(verifyUserService.getRawUserUseCase(), req, resp);
            if (sessionUser == null) {
                ControllerUtils.sendNotFound(resp);
                return Result.empty();
            }
            if (sessionUser.toUser().isAdminOrManager()) {
                return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                        currentUser -> orderService.getOrders(userId, userEmail, presentationId, presentationDate,
                                begin, end, status, deleted, sort, desc, page, currentUser));
            } else {
                return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                        currentUser -> orderService.getYourselfOrders(begin, end,
                                status, sort, desc, page, currentUser));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/", type = RequestMethod.POST)
    public Result<OrderDto> make(@RequestBody OrderDto requestDto, HttpServletRequest req,
                               HttpServletResponse resp) {
        SessionUser sessionUser = ControllerUtils.getAndVerifyCurrentUser(verifyUserService.getRawUserUseCase(), req, resp);
        if (sessionUser == null) {
            ControllerUtils.sendNotFound(resp);
            return Result.empty();
        }
        if (sessionUser.toUser().isAdminOrManager()) {
            return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> orderService.makeOrder(requestDto, currentUser));
        } else {
            return ControllerUtils.doWithVerifiedUser(verifyUserService.getRawUserUseCase(), req, resp,
                    currentUser -> orderService.makeOrderYourself(requestDto, currentUser));
        }
    }
}
