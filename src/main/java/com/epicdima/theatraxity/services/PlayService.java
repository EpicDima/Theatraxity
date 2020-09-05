package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.PlayDto;
import com.epicdima.theatraxity.domain.usecases.theatre.play.*;
import com.epicdima.theatraxity.model.SessionUser;

import java.util.List;

/**
 * @author EpicDima
 */
@Singleton
public final class PlayService {
    private final ChangePlayUseCase changePlayUseCase;
    private final CreatePlayUseCase createPlayUseCase;
    private final DeletePlayUseCase deletePlayUseCase;
    private final GetPlayByIdUseCase getPlayByIdUseCase;
    private final GetPlaysUseCase getPlaysUseCase;
    private final RestorePlayUseCase restorePlayUseCase;

    @Inject
    public PlayService(ChangePlayUseCase changePlayUseCase, CreatePlayUseCase createPlayUseCase,
                       DeletePlayUseCase deletePlayUseCase, GetPlayByIdUseCase getPlayByIdUseCase,
                       GetPlaysUseCase getPlaysUseCase, RestorePlayUseCase restorePlayUseCase) {
        this.changePlayUseCase = changePlayUseCase;
        this.createPlayUseCase = createPlayUseCase;
        this.deletePlayUseCase = deletePlayUseCase;
        this.getPlayByIdUseCase = getPlayByIdUseCase;
        this.getPlaysUseCase = getPlaysUseCase;
        this.restorePlayUseCase = restorePlayUseCase;
    }
    
    public Result<PlayDto> changePlay(PlayDto request, SessionUser currentUser) {
        return changePlayUseCase.execute(request, currentUser.toUser());
    }

    public Result<PlayDto> createPlay(PlayDto request, SessionUser currentUser) {
        return createPlayUseCase.execute(request, currentUser.toUser());
    }

    public Result<Void> deletePlay(PlayDto request, SessionUser currentUser) {
        return deletePlayUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<PlayDto> getPlayById(int playId, SessionUser currentUser) {
        return getPlayByIdUseCase.execute(playId, currentUser.toUser());
    }

    public Result<List<PlayDto>> getPlays(Integer filterAuthorId, Integer filterGenreId,
                                                       Boolean filterDeleted, String sortString,
                                                       boolean desc, SessionUser currentUser) {
        return getPlaysUseCase.execute(
                new GetPlaysUseCase.Filter(filterAuthorId, filterGenreId, filterDeleted),
                GetPlaysUseCase.Sort.fromName(sortString),
                desc,
                currentUser != null ? currentUser.toUser() : null);
    }

    public Result<PlayDto> restorePlay(PlayDto request, SessionUser currentUser) {
        return restorePlayUseCase.execute(request.id, currentUser.toUser());
    }
}
