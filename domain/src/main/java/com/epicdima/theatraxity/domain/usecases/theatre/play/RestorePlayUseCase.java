package com.epicdima.theatraxity.domain.usecases.theatre.play;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.dto.PlayDto;
import com.epicdima.theatraxity.domain.models.theatre.Play;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class RestorePlayUseCase {
    private final PlayDao playDao;

    public RestorePlayUseCase(PlayDao playDao) {
        this.playDao = playDao;
    }

    public Result<PlayDto> execute(int playId, User currentUser) {
        if (currentUser.isNotAdmin()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Play fromDao = playDao.select(playId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PLAY_NOT_FOUND);
        }
        if (!fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PLAY_IS_NOT_DELETED);
        }
        Play forDao = fromDao.toBuilder().deleted(false).build();
        Boolean updated = playDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(PlayDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.PLAY_NOT_RESTORED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
