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
public final class GetPlayByIdUseCase {
    public PlayDao playDao;

    public GetPlayByIdUseCase(PlayDao playDao) {
        this.playDao = playDao;
    }

    public Result<PlayDto> execute(int playId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Play fromDao = playDao.select(playId);
        if (fromDao != null) {
            return Result.success(PlayDto.from(fromDao));
        } else {
            return Result.failure(HttpCodes.NOT_FOUND, Codes.PLAY_NOT_FOUND);
        }
    }
}
