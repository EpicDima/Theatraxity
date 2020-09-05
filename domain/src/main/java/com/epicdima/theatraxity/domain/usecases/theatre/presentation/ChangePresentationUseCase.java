package com.epicdima.theatraxity.domain.usecases.theatre.presentation;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.dto.PresentationDto;
import com.epicdima.theatraxity.domain.models.theatre.Play;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.Date;

/**
 * @author EpicDima
 */
public final class ChangePresentationUseCase {
    private final PlayDao playDao;
    private final PresentationDao presentationDao;

    public ChangePresentationUseCase(PlayDao playDao, PresentationDao presentationDao) {
        this.playDao = playDao;
        this.presentationDao = presentationDao;
    }

    public Result<PresentationDto> execute(PresentationDto presentation, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Presentation fromDao = presentationDao.select(presentation.id);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PRESENTATION_NOT_FOUND);
        }
        if (fromDao.isDeleted() && currentUser.isManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Play play = playDao.select(presentation.play.id);
        if (play == null || play.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PLAY_NOT_FOUND);
        }
        Date presentationDate = presentation.date;
        if (presentationDate == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PRESENTATION_DATE_NOT_SPECIFIED);
        }
        if (presentationDate.before(GetPresentationsUseCase.getCurrentDay())) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PRESENTATION_DATE_BEFORE_CURRENT_DAY);
        }
        presentationDate = GetPresentationsUseCase.toMidnight(presentationDate);
        if (presentationDao.selectByDate(presentationDate) != null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.DATE_ALREADY_CONTAINS_PRESENTATION);
        }
        Presentation forDao = fromDao.toBuilder()
                .date(presentationDate)
                .play(play)
                .build();
        Boolean updated = presentationDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(PresentationDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.NO_PRESENTATION_CHANGE);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
