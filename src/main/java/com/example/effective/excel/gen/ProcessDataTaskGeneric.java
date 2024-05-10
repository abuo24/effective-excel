package com.example.effective.excel.gen;

import com.example.effective.excel.ExcelBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Orifjon Yunusjonov
 * @since 5/3/2024
 */
@Slf4j
public class ProcessDataTaskGeneric<T extends AbstractExcelEntity> extends RecursiveTask<Void> {
    private final int THRESHOLD;
    private final List<T> result;
    private final ExcelBody<T> excelBody;
    private final Sheet sheet;
    private final Integer fromHeader;

    public ProcessDataTaskGeneric(List<T> result, Sheet sheet, Integer fromHeader, ExcelBody<T> excelBody, int chunkSize) {
        this.result = result;
        this.excelBody = excelBody;
        this.fromHeader = fromHeader;
        this.sheet = sheet;
        this.THRESHOLD = chunkSize;
    }

    @Override
    protected Void compute() {
        int size = result.size();
        if (size <= THRESHOLD) {
            processChunk(new AtomicInteger(size));
        } else {
            int mid = size / 2;
            ProcessDataTaskGeneric<T> leftTask = new ProcessDataTaskGeneric<>(result.subList(0, mid), sheet, fromHeader, excelBody, THRESHOLD);
            ProcessDataTaskGeneric<T> rightTask = new ProcessDataTaskGeneric<>(result.subList(mid, size), sheet, fromHeader, excelBody, THRESHOLD);
            invokeAll(leftTask, rightTask);
        }
        return null;
    }

    private void processChunk(AtomicInteger end) {
        AtomicInteger index = new AtomicInteger(0);
        for (; index.get() < end.get(); index.getAndIncrement()) {
            try {
                synchronized (sheet) {
                    T item = result.get(index.get());
                    Row currentRow = sheet.getRow(item.getExcelIndex() + fromHeader);
                    if (currentRow != null) {
                        sheet.removeRow(currentRow);
                    }
                    currentRow = sheet.createRow(item.getExcelIndex() + fromHeader);
                    excelBody.accept(currentRow, item);
                }
            } catch (Exception e) {
                log.error("error thread - {}, message - {}", Thread.currentThread().getName(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

}

