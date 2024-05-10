package com.example.effective.excel.gen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * When you use Effective Excel Library you must implement this class.
 * <p>
 * Author: Orifjon Yunusjonov
 * since: 05/10/2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractExcelEntity {

    /**
     * You must fill this field in your query select when you use with <b>synchronized read<b/>
     * */
    private Integer excelIndex;

}
