package com.coder24.effective.excel;

import org.apache.poi.ss.usermodel.Row;

/**
 *
 * Your Excel body implemention here.
 * <p>
 * You need to implement this for only one record. Effective reader library accept this for all records
 * <p>
 * Author: Orifjon Yunusjonov
 * since: 05/10/2024
 * */
@FunctionalInterface
public interface ExcelBody<T> {

    void accept(Row currentRow, T item);

}
