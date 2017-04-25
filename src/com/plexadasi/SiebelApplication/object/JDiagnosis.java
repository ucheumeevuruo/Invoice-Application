/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plexadasi.SiebelApplication.object;

import com.plexadasi.SiebelApplication.MyLogging;
import com.plexadasi.SiebelApplication.SiebelService;
import com.plexadasi.SiebelApplication.object.Impl.Impl;
import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import com.siebel.data.SiebelPropertySet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Adeyemi
 */
public class JDiagnosis extends SiebelService implements Impl
{
    private static SiebelPropertySet set;
    private String job_id;
    private final List<Map<String, String>> quoteItem = new ArrayList<Map<String, String>>();
    private final Map<String, String> map = new HashMap();
    private final String searchKey = "Parent SH Id";
    private static final String BO = "Auto Vehicle";
    private static final String BC = "PLX Auto Job Card";
    
    /**
     * 
     * @param conn 
     */
    public JDiagnosis(SiebelDataBean conn)
    {
        super(conn);
    }
    
    /**
     *
     * @param id
     * @return
     * @throws SiebelException
     */
    @Override
    public List<Map<String, String>> getItems(String id) throws SiebelException
    {
        this.job_id = id;
        set = new SiebelPropertySet();
        map.put("1", "DIAGNOSTIC FINDINGS");
        quoteItem.add(map);
        set.setProperty("Operation Line No", "1");
        set.setProperty("Code", "3");
        set.setProperty("Type Of Work", "4");
        set.setProperty("Amount", "11");
        this.setSField(set);
        quoteItem.addAll(this.getSField(BO, BC, this));
        MyLogging.log(Level.INFO, "Creating siebel objects JDiagnosis: " + quoteItem);
        return quoteItem;
    }

    /**
     * 
     * @param sbBC
     * @throws SiebelException 
     */
    @Override
    public void searchSpec(SiebelBusComp sbBC) throws SiebelException
    {
        sbBC.setSearchSpec(searchKey, job_id);
        sbBC.setSearchSpec("Type", "JOB_CARD"); 
        sbBC.setSearchSpec("Operation Line No", "Diagnostic Finding");
    }
    
    @Override
    public void searchSpec(SiebelBusComp sbBC, String type) throws SiebelException{}
    
    /**
     *
     * @param sbBC
     */
    @Override
    public void getExtraParam(SiebelBusComp sbBC){};
}
