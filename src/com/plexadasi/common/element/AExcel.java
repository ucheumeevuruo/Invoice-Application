/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plexadasi.common.element;

import com.plexadasi.invoiceapplication.IKey;
import com.plexadasi.invoiceapplication.ProductKey;
import com.plexadasi.invoiceapplication.StringInt;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.plexadasi.SiebelApplication.object.Impl.Impl;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

/**
 *
 * @author Adeyemi
 */
public abstract class AExcel {

    /**
     *
     */
    protected static Workbook workbook;
    protected static Sheet worksheet;
    protected Row sheetrow;
    protected Cell sheetcell;
    
    protected static String value;
    protected String quote_id;
    protected Integer firstRow;
    protected Integer rowCount;
    /**
     *
     */
    protected Integer nextRow;
   
    public AExcel()
    {
        this.firstRow = 0;
        this.nextRow = 0;
        this.rowCount = 0;
    }

    public AExcel(Workbook book, Sheet sheet) {
        workbook = book;
        worksheet = sheet;
        this.firstRow = 0;
        this.nextRow = 0;
        this.rowCount = 0;
    }
    
    public AExcel(Workbook book, Sheet sheet, int startRow) {
        workbook = book;
        worksheet = sheet;
        this.nextRow = 0;
        this.firstRow = startRow;
        this.rowCount = 0;
    }
    
    /**
     *
     * @param qM
     * @param iKey
     * @throws Exception
     */
    abstract public void createCellFromList(Impl qM, IKey iKey) throws Exception;
    
    /**
     *
     * @param qM
     * @param iKey
     * @param book
     * @param sheet
     * @throws Exception
     */
    abstract public void createCellFromList( Impl qM, IKey iKey, Workbook book, Sheet sheet) throws Exception;
    
    protected void createCell(List<Map<String, String>> list, IKey iKey) throws Exception{
        List<Map<String, String>> List = list;
        rowCount = List.size();
        CellStyle style = workbook.createCellStyle();
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        int rowNum = firstRow;
        for(int i = 0; i < rowCount; i++){
            Map<String, String> temp = List.get(i);
            
            sheetrow = worksheet.getRow(rowNum);
           
            // Before writing value to a row, check if a value exist and shift 
            // it to the next row including all its properties
            // or create a new row
            if(iKey instanceof ProductKey)
            {
                if(i != rowCount - 1)
                {
                    CopyRow.copyRow(workbook, worksheet, RowIndex.row(sheetrow.getRowNum() + 1), RowIndex.row(sheetrow.getRowNum() + 2));
                }
            }
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                int key = iKey.productKeyToInt(entry.getKey());
                value = entry.getValue();
                sheetcell = sheetrow.getCell(key, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                //sheetcell.setCellStyle(style);
                //  
                // Check if the value returned is an integer.
                // If true, set the cell to integer.
                // Else set the cell to string
                if(StringInt.isStringInt(value) || StringInt.isStringFloat(value)){
                    sheetcell.setCellValue(new BigDecimal(value).doubleValue());
                }else{
                    sheetcell.setCellValue(value);
                }
            }
            nextRow = rowNum;
            rowNum++;
        }
    }
    
    public void setJobId(String quote_id)
    {
        this.quote_id = quote_id;
    }
    
    public void setStartRow(int start)
    {
        firstRow = start;
    }
    
    public void setNextRow(int value)
    {
        nextRow = value;
    }
    
    public Integer getNextRow()
    {
        return nextRow;
    }
    
    /**
     *
     * @param jump
     * @return
    */
    public Integer next(Integer jump)
    {
        rowCount = rowCount <= 0 ? 1 : rowCount;
        return firstRow + rowCount + jump;
    }
}
