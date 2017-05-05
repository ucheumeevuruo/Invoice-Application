package com.plexadasi.common;


import com.plexadasi.Helper.HelperAP;
import com.plexadasi.SiebelApplication.MyLogging;
import com.plexadasi.SiebelApplication.object.OAddress;
import com.plexadasi.SiebelApplication.object.OParts;
import com.plexadasi.SiebelApplication.object.OShippment;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelPropertySet;
import com.plexadasi.common.element.Attachment;
import com.plexadasi.common.element.InvoiceExcel;
import com.plexadasi.common.element.WaybillAttachment;
import com.plexadasi.common.element.WaybillAttachmentSales;
import com.plexadasi.common.element.XGenerator;
import com.plexadasi.common.impl.Generator;
import com.plexadasi.connect.siebel.SiebelConnect;
import com.plexadasi.invoiceapplication.ContactKey;
import com.plexadasi.invoiceapplication.ProductKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */
public class OrderExcelGenerator implements Generator
{
    private String inputFile;
    
    private String order_id;
    
    private String order_number;
    
    private String ship_id;
    
    private String orderType;
    
    private final StringWriter error_txt = new StringWriter();
    
    private FileInputStream input_document;

    public OrderExcelGenerator() {
        this.orderType = this.ship_id = this.inputFile = this.order_number = "";
        this.input_document = null;
    }
    
    /**
     *
     * @param inputs the value of inputs
     * @param outputs the value of outputs
     */
    @Override
    public void generateExcelDoc(SiebelPropertySet inputs, SiebelPropertySet outputs)
    {
        try {
            //
            SiebelDataBean conn = SiebelConnect.connectSiebelServer();
            //Get excel path
            inputFile = HelperAP.getWaybillTemplate();
            //Read Excel document first
            input_document = new FileInputStream(new File(inputFile));
            // Convert it into a POI object
            Workbook my_xlsx_workbook = WorkbookFactory.create(input_document);
            // Read excel sheet that needs to be updated
            Sheet my_worksheet = my_xlsx_workbook.getSheet("Sheet1");
            // Declare a Cell object
            this.order_id = inputs.getProperty("OrderId");
            this.order_number = inputs.getProperty("OrderNum");
            this.ship_id = inputs.getProperty("ShipId");
            this.orderType = inputs.getProperty("OrderType");
            
            InvoiceExcel customerInfo = new InvoiceExcel(my_xlsx_workbook, my_worksheet, 3);
            customerInfo.setJobId(this.ship_id);
            customerInfo.createCellFromList(new OShippment(conn), new ContactKey());
            customerInfo.setStartRow(8);
            customerInfo.createCellFromList(new OAddress(conn), new ContactKey());
            
            
            InvoiceExcel parts;
            
            int startRowAt = 17;
            parts = new InvoiceExcel(my_xlsx_workbook, my_worksheet);
            
            //
            parts.setStartRow(startRowAt);
            parts.setJobId(order_id);
            parts.createCellFromList(new OParts(conn), new ProductKey());
            my_xlsx_workbook.setForceFormulaRecalculation(true);
            input_document.close();
            XGenerator.doCreateBook(my_xlsx_workbook, "weststar_" + this.order_number.replace(" ", "_"));
            //String filepath = XGenerator.getProperty("filepath");
            String filename = XGenerator.getProperty("filename");
            String filepath = "/usr/app/siebel/intg/excel/weststar_TEST_02052017153845.xls";
            
            Attachment a = null;
            
            if(orderType.equalsIgnoreCase("Sales Order"))
            {
                a = new WaybillAttachmentSales(conn, order_id);
            }
            else if(orderType.equalsIgnoreCase("Purchase Order"))
            {
                a = new WaybillAttachment(conn, order_id);
            }
            
            //Attach the file to siebel
            a.Attach(
                filepath,
                filename,
                Boolean.FALSE
            );
            
            boolean logoff = conn.logoff();
            my_xlsx_workbook.close();
            System.out.println("Done");
            outputs.setProperty("status", "success");
        } 
        catch (FileNotFoundException ex) 
        {
            ex.printStackTrace(new PrintWriter(error_txt));
            MyLogging.log(Level.SEVERE, "Caught File Not Found Exception: " + ex.getMessage() + error_txt.toString());
            outputs.setProperty("status", "failed");
            outputs.setProperty("error_message", error_txt.toString());
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace(new PrintWriter(error_txt));
            MyLogging.log(Level.SEVERE, "Caught IO Exception: " + ex.getMessage() + error_txt.toString());
            outputs.setProperty("status", "failed");
            outputs.setProperty("error_message", error_txt.toString());
        } 
        catch (InvalidFormatException ex) 
        {
            ex.printStackTrace(new PrintWriter(error_txt));
            MyLogging.log(Level.SEVERE, "Caught Invalid Format Exception: " + ex.getMessage() + error_txt.toString());
            outputs.setProperty("status", "failed");
            outputs.setProperty("error_message", error_txt.toString());
        } 
        catch (EncryptedDocumentException ex) 
        {
            ex.printStackTrace(new PrintWriter(error_txt));
            MyLogging.log(Level.SEVERE, "Caught Encrypted Document Exception: " + ex.getMessage() + error_txt.toString());
            outputs.setProperty("status", "failed");
            outputs.setProperty("error_message", error_txt.toString());
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace(new PrintWriter(error_txt));
            MyLogging.log(Level.SEVERE, "Caught Exception: " + ex.getMessage() + error_txt.toString());
            outputs.setProperty("status", "failed");
            outputs.setProperty("error_message", error_txt.toString());
        }
    }
}
