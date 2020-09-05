package com.epicdima.theatraxity.domain.usecases.business.location;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.dao.TicketDao;
import com.epicdima.theatraxity.domain.dto.LocationDto;
import com.epicdima.theatraxity.domain.dto.TicketDto;
import com.epicdima.theatraxity.domain.models.business.Ticket;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EpicDima
 */
public class GetOccupiedSeatsUseCase {

    private final PresentationDao presentationDao;
    private final TicketDao ticketDao;

    public GetOccupiedSeatsUseCase(PresentationDao presentationDao, TicketDao ticketDao) {
        this.presentationDao = presentationDao;
        this.ticketDao = ticketDao;
    }

    public Result<List<TicketDto>> execute(int presentationId) {
        Presentation presentation = presentationDao.select(presentationId);
        if (presentation == null) {
            return Result.failure(HttpCodes.NOT_FOUND, Codes.PRESENTATION_NOT_FOUND);
        }
        List<Ticket> ticketList = ticketDao.selectNotCancelledByPresentationId(presentationId);
        if (ticketList == null) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        return Result.success(ticketList.stream()
                .map(ticket -> TicketDto.builder()
                        .location(LocationDto.from(ticket.getLocation()))
                        .row(ticket.getRow())
                        .seat(ticket.getSeat())
                        .build())
                .collect(Collectors.toList()));
    }
}
