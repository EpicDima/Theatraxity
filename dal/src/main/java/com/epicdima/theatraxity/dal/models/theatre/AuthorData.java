package com.epicdima.theatraxity.dal.models.theatre;

import com.epicdima.lib.dal.annotations.Column;
import com.epicdima.lib.dal.annotations.PrimaryKey;
import com.epicdima.lib.dal.annotations.Table;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.domain.models.theatre.Author;

/**
 * @author EpicDima
 */
@Table(name = "authors")
public final class AuthorData {

    @PrimaryKey
    @Column
    public int id;

    @Column
    public String name;

    @Column
    public boolean deleted;

    public AuthorData() {}

    public AuthorData(int id, String name, boolean deleted) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
    }

    @Named("dal_to_author")
    @Singleton
    public static final class ToAuthor implements Mapper<AuthorData, Author> {
        @Override
        public Author map(AuthorData item) {
            return new Author(item.id, item.name, item.deleted);
        }
    }


    @Named("dal_from_author")
    @Singleton
    public static final class FromAuthor implements Mapper<Author, AuthorData> {
        @Override
        public AuthorData map(Author item) {
            return new AuthorData(item.getId(), item.getName(), item.isDeleted());
        }
    }
}
