
import com.siebel.data.SiebelPropertySet;
import com.siebel.eai.SiebelBusinessServiceException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Adeyemi
 */
public class ApachePOIExcelWriteTest 
{

    /**
     *
     * @param args
     * @throws SiebelBusinessServiceException
     */
    public static void main(String[] args) throws SiebelBusinessServiceException 
    {
        ApachePOIExcelWrite eia = new ApachePOIExcelWrite();
        SiebelPropertySet inputs = new SiebelPropertySet();
        SiebelPropertySet outputs = new SiebelPropertySet();
        inputs.setProperty("QuoteId", "1-1025Q");//1-1028K//1-1026S//1-1025Q//1-3247471
        inputs.setProperty("QuoteNum", "Cool Quote");
        eia.doInvokeMethod("QuoteExcelGenerator", inputs, outputs);
    }
}