package com.epicdima.theatraxity.domain.dao;

import com.epicdima.theatraxity.domain.models.business.Ticket;

import java.util.List;

/**
 * @author EpicDima
 */
public interface TicketDao extends BaseDao<Ticket> {
    List<Ticket> selectNotCancelledByPresentationId(int presentationId);
    List<Ticket> selectByOrderId(int orderId);
}
