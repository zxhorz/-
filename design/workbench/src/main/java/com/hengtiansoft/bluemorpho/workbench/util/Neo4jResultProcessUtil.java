package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import com.hengtiansoft.bluemorpho.model.D3JsonObject;
import com.hengtiansoft.bluemorpho.model.D3Link;
import com.hengtiansoft.bluemorpho.model.D3Mark;
import com.hengtiansoft.bluemorpho.model.D3Node;
import net.sf.json.JSONObject;

public class Neo4jResultProcessUtil {
    private static final Logger LOGGER = Logger.getLogger(Neo4jResultProcessUtil.class);
    public static String generateDependencyJsonFile(String jsonFilePath, String programName, StatementResult caller, 
            StatementResult callee, List<Record> useTableRecord,StatementResult useTable2, StatementResult useFile, StatementResult useCpy) {
        D3JsonObject d3JsonObject = new D3JsonObject();
        Set<D3Node> nodesSet = new HashSet<D3Node>();
        List<D3Node> nodes = new ArrayList<D3Node>();
        List<D3Link> links = new ArrayList<D3Link>();
        List<D3Mark> mark = new ArrayList<D3Mark>();
        d3JsonObject.setNodes(nodes);
        d3JsonObject.setLinks(links);
        d3JsonObject.setMark(mark);
        mark.add(new D3Mark("Program", "Program"));
        mark.add(new D3Mark("Copybook", "Copybook"));
        mark.add(new D3Mark("Table", "Table"));
        mark.add(new D3Mark("File", "File"));
//      mark.add(new D3Mark("call", "call"));
//      mark.add(new D3Mark("copy", "copy"));
//      mark.add(new D3Mark("use_table", "use_table"));
//      mark.add(new D3Mark("use_file", "use_file"));
        
        nodesSet.add(new D3Node(programName, "Program"));
        
        List<Record> callerRecords = caller.list();
        for (Record record : callerRecords) {
            String gpm = record.get("pgm").asString();
            nodesSet.add(new D3Node(gpm, "Program"));
            links.add(new D3Link(gpm, programName, "call"));
        }
        
        List<Record> calleeRecords = callee.list();
        for (Record record : calleeRecords) {
            String gpm = record.get("pgm").asString();
            nodesSet.add(new D3Node(gpm, "Program"));
            links.add(new D3Link(programName, gpm, "call"));
        }
        
        for (Record record : useTableRecord) {
            String table = record.get("table").asString();
            String operation = record.get("operation").asString();
            nodesSet.add(new D3Node(table,"Table"));
            if (operation.equals("R"))
                links.add(new D3Link(table, programName, "use_table"));
            else
                links.add(new D3Link(programName, table, "use_table"));
        }
        
        useTableRecord = useTable2.list();
        for (Record record : useTableRecord) {
            String table = record.get("table").asString();
            nodesSet.add(new D3Node(table,"Table"));
            links.add(new D3Link(programName, table, "use_table"));
        }

        List<Record> useFileRecords = useFile.list();
        for (Record record : useFileRecords) {
            String file = record.get("file").asString();
            String type = record.get("type").asString();
            nodesSet.add(new D3Node(file, "File"));
            if(type.equals("INPUT"))            
                links.add(new D3Link(file, programName, "use_file"));
            else if(type.equals("OUTPUT") || type.equals("EXTEND"))
                links.add(new D3Link(programName, file, "use_file"));
        }
        
        List<Record> useCpyRecords = useCpy.list();
        for (Record record : useCpyRecords) {
            String cpyName = record.get("cpyName").asString();
            nodesSet.add(new D3Node(cpyName, "Copybook"));
            links.add(new D3Link(programName, cpyName, "copy"));
        }
        nodes = new ArrayList<D3Node>(nodesSet);
        d3JsonObject.setNodes(nodes);
        JSONObject json = JSONObject.fromObject(d3JsonObject);
        String jsonString = json.toString();
        try {
            FileUtils.writeStringToFile(new File(jsonFilePath), jsonString);
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return jsonString;
    }
    
}
