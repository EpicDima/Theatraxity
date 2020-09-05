package com.epicdima.theatraxity.di.usecase;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.usecases.theatre.play.*;

/**
 * @author EpicDima
 */
@Module
public final class PlayUseCaseModule {
    @Provides
    public ChangePlayUseCase provideChangePlayUseCase(PlayDao playDao, AuthorDao authorDao, GenreDao genreDao) {
        return new ChangePlayUseCase(playDao, authorDao, genreDao);
    }
    
    @Provides
    public CreatePlayUseCase provideCreatePlayUseCase(PlayDao playDao, AuthorDao authorDao, GenreDao genreDao) {
        return new CreatePlayUseCase(playDao, authorDao, genreDao);
    }
    
    @Provides
    public DeletePlayUseCase provideDeletePlayUseCase(PlayDao genreDao) {
        return new DeletePlayUseCase(genreDao);
    }
    
    @Provides
    public GetPlayByIdUseCase provideGetPlayByIdUseCase(PlayDao genreDao) {
        return new GetPlayByIdUseCase(genreDao);
    }
    
    @Provides
    public GetPlaysUseCase provideGetPlaysUseCase(PlayDao genreDao) {
        return new GetPlaysUseCase(genreDao);
    }
    
    @Provides
    public RestorePlayUseCase provideRestorePlayUseCase(PlayDao genreDao) {
        return new RestorePlayUseCase(genreDao);
    }
}
