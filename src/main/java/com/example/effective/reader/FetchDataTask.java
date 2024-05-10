package com.example.effective.reader;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author Orifjon Yunusjonov
 * @description
 * @since 5/3/2024
 */
public class FetchDataTask<T> extends RecursiveTask<List<T>> {

    private final int startPage;
    private final int endPage;
    private final int chunkSize;
    private final QueryBody<T> queryBody;

    public FetchDataTask(int startPage, int endPage, int chunkSize, QueryBody<T> queryBody) {
        this.startPage = startPage;
        this.endPage = endPage;
        this.chunkSize = chunkSize;
        this.queryBody = queryBody;
    }

    @Override
    protected List<T> compute() {
        if (endPage - startPage <= 1) {
            // Base case: fetch data for a single page
            Pageable pageRequest = PageRequest.of(startPage, chunkSize);
            Page<T> res = queryBody.get(pageRequest);
            return res.getContent();
        } else {
            // Recursive case: divide the task into subtasks
            int midPage = startPage + (endPage - startPage) / 2;
            FetchDataTask<T> leftTask = new FetchDataTask(startPage, midPage, chunkSize, queryBody);
            FetchDataTask<T> rightTask = new FetchDataTask(midPage, endPage, chunkSize, queryBody);

            // Fork both subtasks
            leftTask.fork();
            List<T> rightResult = rightTask.compute();

            // Join the left subtask and combine the results
            List<T> leftResult = new ArrayList<>(leftTask.join());
            leftResult.addAll(rightResult);
            return leftResult;
        }
    }


}