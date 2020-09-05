package com.epicdima.theatraxity.di.usecase;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.usecases.theatre.genre.*;

/**
 * @author EpicDima
 */
@Module
public final class GenreUseCaseModule {
    @Provides
    public ChangeGenreUseCase provideChangeGenreUseCase(GenreDao genreDao) {
        return new ChangeGenreUseCase(genreDao);
    }
    
    @Provides
    public CreateGenreUseCase provideCreateGenreUseCase(GenreDao genreDao) {
        return new CreateGenreUseCase(genreDao);
    }
    
    @Provides
    public DeleteGenreUseCase provideDeleteGenreUseCase(GenreDao genreDao) {
        return new DeleteGenreUseCase(genreDao);
    }
    
    @Provides
    public GetGenreByIdUseCase provideGetGenreByIdUseCase(GenreDao genreDao) {
        return new GetGenreByIdUseCase(genreDao);
    }
    
    @Provides
    public GetGenresUseCase provideGetGenresUseCase(GenreDao genreDao) {
        return new GetGenresUseCase(genreDao);
    }
    
    @Provides
    public RestoreGenreUseCase provideRestoreGenreUseCase(GenreDao genreDao) {
        return new RestoreGenreUseCase(genreDao);
    }
}
