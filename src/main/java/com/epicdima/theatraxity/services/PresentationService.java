package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.PresentationDto;
import com.epicdima.theatraxity.domain.usecases.theatre.presentation.*;
import com.epicdima.theatraxity.helpers.DateFormat;
import com.epicdima.theatraxity.model.SessionUser;

import java.util.List;

/**
 * @author EpicDima
 */
@Singleton
public final class PresentationService {

    private final ChangePresentationUseCase changePresentationUseCase;
    private final CreatePresentationUseCase createPresentationUseCase;
    private final DeletePresentationUseCase deletePresentationUseCase;
    private final GetPresentationByIdUseCase getPresentationByIdUseCase;
    private final GetPresentationsUseCase getPresentationsUseCase;
    private final RestorePresentationUseCase restorePresentationUseCase;
    private final DateFormat dateFormat;

    @Inject
    public PresentationService(ChangePresentationUseCase changePresentationUseCase,
                               CreatePresentationUseCase createPresentationUseCase,
                               DeletePresentationUseCase deletePresentationUseCase,
                               GetPresentationByIdUseCase getPresentationByIdUseCase,
                               GetPresentationsUseCase getPresentationsUseCase,
                               RestorePresentationUseCase restorePresentationUseCase,
                               DateFormat dateFormat) {
        this.changePresentationUseCase = changePresentationUseCase;
        this.createPresentationUseCase = createPresentationUseCase;
        this.deletePresentationUseCase = deletePresentationUseCase;
        this.getPresentationByIdUseCase = getPresentationByIdUseCase;
        this.getPresentationsUseCase = getPresentationsUseCase;
        this.restorePresentationUseCase = restorePresentationUseCase;
        this.dateFormat = dateFormat;
    }

    public Result<PresentationDto> changePresentation(PresentationDto request, SessionUser currentUser) {
        return changePresentationUseCase.execute(request, currentUser.toUser());
    }

    public Result<PresentationDto> createPresentation(PresentationDto request, SessionUser currentUser) {
        return createPresentationUseCase.execute(request, currentUser.toUser());
    }

    public Result<Void> deletePresentation(PresentationDto request, SessionUser currentUser) {
        return deletePresentationUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<PresentationDto> getPresentationById(int presentationId, SessionUser currentUser) {
        return getPresentationByIdUseCase.execute(presentationId, currentUser.toUser());
    }

    public Result<List<PresentationDto>> getPresentations(String filterBegin, String filterEnd,
                                                          Integer playId, Integer authorId,
                                                          Integer genreId, Boolean deleted,
                                                          String sortString, Boolean desc,
                                                          int page, SessionUser currentUser) {
        return getPresentationsUseCase.execute(
                new GetPresentationsUseCase.Filter(
                        dateFormat.format(filterBegin), dateFormat.format(filterEnd),
                        playId, authorId, genreId, deleted),
                GetPresentationsUseCase.Sort.fromName(sortString),
                desc,
                page,
                currentUser != null ? currentUser.toUser() : null);
    }

    public Result<PresentationDto> restorePresentation(PresentationDto request, SessionUser currentUser) {
        return restorePresentationUseCase.execute(request.id, currentUser.toUser());
    }
}
