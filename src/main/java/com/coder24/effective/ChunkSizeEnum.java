package com.coder24.effective;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Orifjon Yunusjonov
 * @since 5/3/2024
 */
@AllArgsConstructor
@Getter
public enum ChunkSizeEnum {
    RANGE_1_TO_50000(30000),
    RANGE_50001_TO_100000(20000);

    private final int size;

    public static int getChunkSizeForValue(long value) {
        for (ChunkSizeEnum chunkSize : ChunkSizeEnum.values()) {
            if (value >= chunkSize.getStartValue() && value <= chunkSize.getEndValue()) {
                return chunkSize.getSize();
            }
        }
        return (int) (value * 0.1 + 1000);
    }

    public static int getTotalPages(long totalElements) {;
        return (int) (totalElements + getChunkSizeForValue(totalElements) - 1) / getChunkSizeForValue(totalElements);
    }

    private int getStartValue() {
        switch (this) {
            case RANGE_1_TO_50000:
                return 1;
            case RANGE_50001_TO_100000:
                return 50001;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    private int getEndValue() {
        switch (this) {
            case RANGE_1_TO_50000:
                return 50000;
            case RANGE_50001_TO_100000:
                return 100000;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
