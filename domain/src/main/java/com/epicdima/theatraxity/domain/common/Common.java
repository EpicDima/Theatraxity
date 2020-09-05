package com.epicdima.theatraxity.domain.common;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EpicDima
 */
public final class Common {
    private Common() {
        throw new AssertionError();
    }

    public static final int PAGE_SIZE = 20;

    public static <T> List<T> getPage(List<T> list, int page) {
        int size = list.size();
        int min = PAGE_SIZE * page;
        if (min >= size) {
            return Collections.emptyList();
        }
        int max = PAGE_SIZE * (page + 1);
        return list.subList(min, Math.min(max, size));
    }

    public static <T> Stream<T> reverse(Stream<T> stream) {
        List<T> list = stream.collect(Collectors.toList());
        Collections.reverse(list);
        return list.stream();
    }
}
