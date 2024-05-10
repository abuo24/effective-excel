package com.example.effective.reader;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@FunctionalInterface
public interface QueryBody<T> {
    Page<T> get(Pageable pageable);
}
