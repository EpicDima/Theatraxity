package com.epicdima.theatraxity.dal.mysql.dao.business;

import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.dal.utils.ConfigurationManager;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.models.business.TicketData;
import com.epicdima.theatraxity.dal.mysql.dao.MySqlBaseDao;
import com.epicdima.theatraxity.domain.dao.TicketDao;
import com.epicdima.theatraxity.domain.models.business.Ticket;

import java.util.List;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlTicketDao extends MySqlBaseDao<TicketData, Ticket> implements TicketDao {

    private static final String SELECT_NOT_CANCELLED_BY_PRESENTATION_ID_KEY = "select.not.cancelled.tickets.by.presentation.id";

    private final ConfigurationManager configurationManager;

    @Inject
    public MySqlTicketDao(ConnectionPool connectionPool,
                          @Named("dal_to_ticket") Mapper<TicketData, Ticket> to,
                          @Named("dal_from_ticket") Mapper<Ticket, TicketData> from,
                          ConfigurationManager configurationManager) {
        super(connectionPool, to, from);
        this.configurationManager = configurationManager;
    }

    @Override
    public List<Ticket> selectNotCancelledByPresentationId(int presentationId) {
        return doSelect(configurationManager.getProperty(SELECT_NOT_CANCELLED_BY_PRESENTATION_ID_KEY),
                (statement) -> statement.setInt(1, presentationId),
                this::executeAndConvertToList);
    }

    @Override
    public List<Ticket> selectByOrderId(int orderId) {
        return selectWhere("order_id", orderId);
    }
}
