package com.epicdima.theatraxity.dal.getters;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.domain.models.theatre.Author;

/**
 * @author EpicDima
 */
@Named("author")
@Singleton
public final class GetAuthorById implements GetById<Author> {

    private final AuthorDao authorDao;

    @Inject
    public GetAuthorById(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Override
    public synchronized Author getById(int id) {
        return authorDao.select(id);
    }
}
