package com.epicdima.theatraxity.dal.di;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.theatraxity.dal.models.business.OrderData;
import com.epicdima.theatraxity.dal.models.business.TicketData;
import com.epicdima.theatraxity.dal.models.theatre.AuthorData;
import com.epicdima.theatraxity.dal.models.theatre.GenreData;
import com.epicdima.theatraxity.dal.models.theatre.PlayData;
import com.epicdima.theatraxity.dal.models.theatre.PresentationData;
import com.epicdima.theatraxity.dal.models.user.UserData;

/**
 * @author EpicDima
 */
@Module(implementations = {
        UserData.ToUser.class,
        UserData.FromUser.class,
        AuthorData.ToAuthor.class,
        AuthorData.FromAuthor.class,
        GenreData.ToGenre.class,
        GenreData.FromGenre.class,
        PlayData.ToPlay.class,
        PlayData.FromPlay.class,
        PresentationData.ToPresentation.class,
        PresentationData.FromPresentation.class,
        OrderData.ToOrder.class,
        OrderData.FromOrder.class,
        TicketData.ToTicket.class,
        TicketData.FromTicket.class
})
public final class MappersModule {
}
