package com.example.effective.reader;

import com.example.effective.ChunkSizeEnum;
import com.example.effective.exception.EffectiveReaderException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Orifjon Yunusjonov
 * @since 5/3/2024
 */
@Slf4j
public class EffectiveReader {

    public static <T> List<T> invoke(Long totalElements, QueryBody<T> queryBody) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try {
            FetchDataTask<T> task = new FetchDataTask<>(0, ChunkSizeEnum.getTotalPages(totalElements), ChunkSizeEnum.getChunkSizeForValue(totalElements), queryBody);
            return forkJoinPool.invoke(task);
        } catch (Exception e) {
            log.error("Error on processing effective reader - {}", e.getMessage());
            throw new EffectiveReaderException("Error on processing effective reader - " + e.getMessage());
        } finally {
            forkJoinPool.shutdown();
        }
    }
}
