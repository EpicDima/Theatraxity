package com.epicdima.theatraxity.domain.usecases.theatre.play;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.models.theatre.Play;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class DeletePlayUseCase {
    private final PlayDao playDao;

    public DeletePlayUseCase(PlayDao playDao) {
        this.playDao = playDao;
    }

    public Result<Void> execute(int playId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Play fromDao = playDao.select(playId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PLAY_NOT_FOUND);
        }
        if (fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PLAY_IS_DELETED);
        }
        Boolean updated = playDao.update(fromDao.toBuilder().deleted(true).build());
        if (updated != null) {
            if (updated) {
                return Result.empty();
            } else {
                return Result.failure(HttpCodes.OK, Codes.PLAY_NOT_DELETED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
