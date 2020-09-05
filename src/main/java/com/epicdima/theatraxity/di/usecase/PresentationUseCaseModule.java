package com.epicdima.theatraxity.di.usecase;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.usecases.theatre.presentation.*;

/**
 * @author EpicDima
 */
@Module
public final class PresentationUseCaseModule {
    @Provides
    public ChangePresentationUseCase provideChangePresentationUseCase(PlayDao playDao, PresentationDao presentationDao) {
        return new ChangePresentationUseCase(playDao, presentationDao);
    }
    
    @Provides
    public CreatePresentationUseCase provideCreatePresentationUseCase(PlayDao playDao, PresentationDao presentationDao) {
        return new CreatePresentationUseCase(playDao, presentationDao);
    }
    
    @Provides
    public DeletePresentationUseCase provideDeletePresentationUseCase(PresentationDao presentationDao) {
        return new DeletePresentationUseCase(presentationDao);
    }
    
    @Provides
    public GetPresentationByIdUseCase provideGetPresentationByIdUseCase(PresentationDao presentationDao) {
        return new GetPresentationByIdUseCase(presentationDao);
    }
    
    @Provides
    public GetPresentationsUseCase provideGetPresentationsUseCase(PresentationDao presentationDao) {
        return new GetPresentationsUseCase(presentationDao);
    }
    
    @Provides
    public RestorePresentationUseCase provideRestorePresentationUseCase(PresentationDao presentationDao) {
        return new RestorePresentationUseCase(presentationDao);
    }
}
