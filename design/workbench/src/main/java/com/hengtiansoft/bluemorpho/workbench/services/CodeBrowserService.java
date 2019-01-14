package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.workbench.domain.CtrlFlowSvgSize;
import com.hengtiansoft.bluemorpho.workbench.domain.PicIndexAndBase64;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.CheckMissingResult;
import com.hengtiansoft.bluemorpho.workbench.dto.CheckMissingItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ControlFlowDto;
import com.hengtiansoft.bluemorpho.workbench.dto.DependencyDto;
import com.hengtiansoft.bluemorpho.workbench.dto.DocDownloadInfo;
import com.hengtiansoft.bluemorpho.workbench.dto.FileFolderTreeResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.FileStructureNode;
import com.hengtiansoft.bluemorpho.workbench.dto.ParagraphSourceCodeDetail;
import com.hengtiansoft.bluemorpho.workbench.dto.ProgramInfo;
import com.hengtiansoft.bluemorpho.workbench.enums.MissingDocType;
import com.hengtiansoft.bluemorpho.workbench.neo4j.dao.Neo4jDao;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.Neo4jResultProcessUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PictureUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;
import com.hengtiansoft.bluemorpho.workbench.util.SvgPropertyAdderUtil;
import com.hengtiansoft.bluemorpho.workbench.util.SvgToPngUtil;
import com.hengtiansoft.bluemorpho.workbench.util.TemplateUtil;
import com.hengtiansoft.bluemorpho.workbench.util.WordUtil;

@Service
public class CodeBrowserService {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    SummaryService summaryService;
    @Autowired
    PortUtil portUtil;
    private static final Logger LOGGER = Logger.getLogger(CodeBrowserService.class);
    private static final String CONTROL_FLOW_JAR = "/ControlFlow-0.0.1-SNAPSHOT.jar";
    private static final String FILE_STRUCTURE_JAR = "/FileStructure-0.0.1-SNAPSHOT.jar";
    private static final String CUR_CTRL_FLOW_DOC_FTL = "curCtrlFlowDoc.ftl";
    private static final String CUR_FILE_STRUCTURE_DOC_FTL = "curFileStructureDoc.ftl";
    private static final String CUR_DEPENDENCY_DOC_FTL = "curDependecyDoc.ftl";
//    private static final String PGM_DOC_FTL = "onePgmDoc.ftl";
    private static final String PGM_DOC_FTL = "one_program.ftl";
    private static final String CONFIG = "config/config.properties";
    private static final String SYSTEM_DOCUMENTATION = "system_documentation.ftl";
    private static final int CTRLFLOW_PIC_DEFAULT_HEIGHT = 400;

    public FileFolderTreeResponse getFolderTree(String projectId) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return null;
        }
        FileFolderTreeResponse fft = new FileFolderTreeResponse();

        if ("DEFAULT".equals(project.getFileMapping())) {
            fft = getDefaultFolderTreeList(project.getPath());
        } else {

        }

        return fft;
    }

    public String getSourceCode(String projectId, String filePath) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return null;
        }
        filePath = FilePathUtil.getPath(project.getPath(), "SOURCE") + "/" + filePath;
        return FilePathUtil.readFile(filePath);
    }
    
    
    public List<String> getSourceCodeList(String projectId, String filePath) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return null;
        }
        filePath = FilePathUtil.getPath(project.getPath(), "SOURCE") + "/" + filePath;
        List<String> result = null;
        try {
            result = FileUtils.readLines(new File(filePath));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error("SourceCodeList IO Exception");
        }
        return result;
    }

    /**
     * Summary的paragraph列表中，选中一个paragraph，则跳转至code browser，打开该段所在的程序，并且根据startLine,
     * endLine，定位至该段所在的位置
     *
     * @param projectId
     * @param filePath
     * @param startLine
     * @param endLine
     * @return
     */
    public ParagraphSourceCodeDetail getParaSourceCode(String projectId, String filePath, int startLine, int endLine) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return null;
        }
        filePath = FilePathUtil.getPath(project.getPath(), "SOURCE") + "/" + filePath;
        return new ParagraphSourceCodeDetail(FilePathUtil.readFile(filePath), startLine, endLine);
    }

    /**
     * 选中一个程序，生成controlFlow，返回graphName&graphPath的map
     *
     * @param projectId
     * @param fileName
     * @return
     */
    public List<ControlFlowDto> getControlFlow(String projectId, String fileName) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return null;
        }
        String projectPath = project.getPath();
        String sourcePath = FilePathUtil.getPath(projectPath, "SOURCE");
        String cobolPath = FilePathUtil.getPath(sourcePath, "COBOL");
        String copybookPath = FilePathUtil.getPath(sourcePath, "COPYBOOK");
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String outputPath = FilePathUtil.getControlFlowPath(projectPath, codeVersion);
        // 若已经生成controlflow,则直接返回graphNamesMap；否则，调用jar包生成
        if (updateControlFlow(projectPath, fileName, codeVersion)) {
            int code = generateControlFlow(projectPath, cobolPath + "/" + fileName, copybookPath, outputPath,
                    codeVersion);
            if (code == 0) {
                // control flow生成成功 读取graphNames.txt
                return FilePathUtil.getGraphNames(projectPath, fileName, codeVersion);
            } else {
                return new ArrayList<ControlFlowDto>();
            }
        } else {
            return FilePathUtil.getGraphNames(projectPath, fileName, codeVersion);
        }
    }

    public List<FileStructureNode> getFileStructure(String projectId, String fileName) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return null;
        }
        String projectPath = project.getPath();
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String sourcePath = FilePathUtil.getPath(projectPath, "SOURCE");
        String cobolPath = FilePathUtil.getPath(sourcePath, "COBOL");
        String copybookPath = FilePathUtil.getPath(sourcePath, "COPYBOOK");
        String outputFilePath = FilePathUtil.getFileStructureTempPath(projectPath, fileName, codeVersion);
        // 若已经生成filestructure,则直接返回fileStrucureNodes;否则，调用jar包生成
        if (updateFileStructure(outputFilePath)) {
            int code = fileStructureVisitor(projectPath, cobolPath + "/" + fileName, copybookPath, outputFilePath,
                    codeVersion);
            if (code == 0) {
                // file structure生成成功 读取fileStrucureNodes.txt
                return readResultFromFile(outputFilePath);
            } else {
                return new ArrayList<FileStructureNode>();
            }
        } else {
            return readResultFromFile(outputFilePath);
        }
    }

    public boolean updateControlFlow(String projectPath, String fileName, String codeVersion) {
        String outputPath = FilePathUtil.getControlFlowPath(projectPath, codeVersion) + "/" + fileName;
        File outputFile = new File(outputPath);
        if (!outputFile.exists()) {
            return true;
        }
        File mapFile = new File(FilePathUtil.getControlFlowNamesPath(projectPath, fileName, codeVersion));
        if (!mapFile.exists()) {
            return true;
        }
        return false;
    }

    public boolean updateFileStructure(String outputFilePath) {
        File outputFile = new File(outputFilePath);
        if (!outputFile.exists()) {
            return true;
        }
        return false;
    }

    public int fileStructureVisitor(String projectPath, String cobolFilePath, String copybookPath, String outputFile,
            String codeVersion) {
        String toolPath = FilePathUtil.getToolPath();
        List<String> cmd = buildFileStructureCommand(toolPath, cobolFilePath, copybookPath, outputFile);
        int code = ProcessBuilderUtil.processBuilder(cmd, toolPath,
                FilePathUtil.getFileStructureLogPath(projectPath, codeVersion), true);
        return code;
    }

    public int generateControlFlow(String projectPath, String cobolFilePath, String copybookPath, String outputPath,
            String codeVersion) {
        String toolPath = FilePathUtil.getToolPath();
        List<String> cmd = builderControlFlowCommand(toolPath, cobolFilePath, copybookPath, outputPath);
        int code = ProcessBuilderUtil.processBuilder(cmd, toolPath,
                FilePathUtil.getControlFlowLogPath(projectPath, codeVersion), true);
        return code;
    }

    private List<String> buildFileStructureCommand(String toolPath, String cobolFilePath, String copybookPath,
            String outputFile) {
        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-jar");
        cmd.add(toolPath + FILE_STRUCTURE_JAR);
        cmd.add(cobolFilePath);
        cmd.add(copybookPath);
        cmd.add(outputFile);
        return cmd;
    }

    private List<String> builderControlFlowCommand(String toolPath, String cobolFilePath, String copybookPath,
            String outputPath) {
        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-jar");
        cmd.add(toolPath + CONTROL_FLOW_JAR);
        cmd.add(cobolFilePath);
        cmd.add(copybookPath);
        cmd.add(outputPath);
        cmd.add("false");
        return cmd;
    }

    private FileFolderTreeResponse getDefaultFolderTreeList(String path) {
        String sourcePath = FilePathUtil.getPath(path, "SOURCE");
        String cobolPath = FilePathUtil.getPath(sourcePath, "COBOL");
        String copybookPath = FilePathUtil.getPath(sourcePath, "COPYBOOK");
        String jobPath = FilePathUtil.getPath(sourcePath, "JOB");
        String procPath = FilePathUtil.getPath(sourcePath, "PROC");
        FileFolderTreeResponse fft = new FileFolderTreeResponse();
        File file = new File(sourcePath);
        fft.setName(file.getName());
        fft.setType("directory");
        List<FileFolderTreeResponse> ffts = new ArrayList<>();
        FileFolderTreeResponse cobolFolder = getFileFolderTree(cobolPath, "COBOL");
        FileFolderTreeResponse copyBookFolder = getFileFolderTree(copybookPath, "COPYBOOK");
        FileFolderTreeResponse jobFolder = getFileFolderTree(jobPath, "JOB");
        FileFolderTreeResponse procFolder = getFileFolderTree(procPath, "PROC");
        if(cobolFolder!=null) {
            ffts.add(cobolFolder);
        }
        if(copyBookFolder!=null) {
            ffts.add(copyBookFolder);
        }
        if(jobFolder!=null) {
            ffts.add(getFileFolderTree(jobPath, "JOB"));    
        }
        if(procFolder!=null) {
            ffts.add(procFolder);
        }
        fft.setChildren(ffts);
        return fft;
    }

    private FileFolderTreeResponse getFileFolderTree(String cobolPath, String type) {
        File file = new File(cobolPath);
        if (!file.exists()) {
            return null;
        }
        FileFolderTreeResponse fft = new FileFolderTreeResponse();
        fft.setName(file.getName());
        fft.setFileType(type);
        if (file.isDirectory()) {
            fft.setType("directory");
        } else {
            fft.setType("file");
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            List<FileFolderTreeResponse> ffts = new ArrayList<>();
            for (File f : files) {
                ffts.add(getFileFolderTree(f.getAbsolutePath(), type));
            }
            fft.setChildren(ffts);
        } else {
            return fft;
        }
        return fft;
    }

    private List<FileStructureNode> readResultFromFile(String outputPath) {
        List<FileStructureNode> result = new ArrayList<FileStructureNode>();
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(outputPath));
            result = (List<FileStructureNode>) is.readObject();// 从流中读取List的数据
            is.close();
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
        } catch (IOException e) {
            LOGGER.error(e);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e);
        }
        return result;
    }

    public DependencyDto getDependency(String projectId, String programName) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return null;
        }
        String projectPath = project.getPath();
        int boltPort = portUtil.getBoltPort(projectPath);
        String uri = "bolt://localhost:" + boltPort;
        Neo4jDao neo4jDao = new Neo4jDao(uri);

        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String dir = FilePathUtil.getDependencyPath(projectPath, codeVersion);
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        String jsonFilePath = dir + "/" + programName + ".json";
        String nodeId ="COBOL/" + programName;
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("programName", programName);
        properties.put("nodeId", nodeId);
//        String queryProgramId = "match(p:Program) where p.name={programName} return p.nodeId as pgmId";
//        String queryCaller = "match(p1:Program)-[:call]->(p2:Program) where p2.name={programName} return p1.name as pgm";
//        String queryCallee = "match(p1:Program)-[:call]->(p2:Program) where p1.name={programName} return p2.name as pgm";
//        String queryTable = "match(p:Program)-[:use_table]->(t:Table) where p.name={programName} return t.name as table";
//        String queryFile = "match(p:Program)-[:use_file]->(f:File) where p.name={programName} return f.name as file,f.openType as type";
//        String queryCopybook = "match(p:Program)-[:use_copy_command]->(cc:CopyCommand)-[:copy]->(c:Copybook) where p.name={programName} return distinct c.name as cpyName";
        String queryProgramId = "match(p:Program) where p.nodeId contains {nodeId} return p.nodeId as pgmId";
        String queryCaller = "match(p1:Program)-[:call]->(p2:Program) where p2.nodeId contains {nodeId} return p1.name as pgm";
        String queryCallee = "match(p1:Program)-[:call]->(p2:Program) where p1.nodeId contains {nodeId} return p2.name as pgm";
//        String queryTable = "match(p:Program)-[:use_table]->(t:Table) where p.nodeId contains {nodeId} return t.name as table";
        String queryTable = "match(n:Program)-[:has_paragraph]->(p:Paragraph)<-[:blockToParagraph]-(b:BlockPosition),(b1:BlockPosition)-[r:sqlUseTable]->(t:Table) where n.nodeId contains {nodeId} and p.isExit = 'N' and b1.paraName = b.paraName return distinct b1.nodeId as blockId,t.name as table,r.operation as operation";
        String queryTable2 = "match(n:BlockPosition)-[r:sqlUseTable]->(t:Table) where not n.nodeId in {blockIds} and n.nodeId contains {nodeId} and n.nodeId contains('#SQL_STATEMENT#') return distinct n.nodeId as programId,t.name as table,r.operation as operation";
        String queryFile = "match(p:Program)-[:use_file]->(f:File) where p.nodeId contains {nodeId} return f.name as file,f.openType as type";
        String queryCopybook = "match(p:Program)-[:use_copy_command]->(cc:CopyCommand)-[:copy]->(c:Copybook) where p.nodeId contains {nodeId} return distinct c.name as cpyName";
        StatementResult pgmId = neo4jDao.executeReadCypher(queryProgramId, properties);
        StatementResult caller = neo4jDao.executeReadCypher(queryCaller, properties);
        StatementResult callee = neo4jDao.executeReadCypher(queryCallee, properties);
        StatementResult useTable = neo4jDao.executeReadCypher(queryTable, properties);
        StatementResult useFile = neo4jDao.executeReadCypher(queryFile, properties);
        StatementResult useCpy = neo4jDao.executeReadCypher(queryCopybook, properties);
        Set<String> blockIdSet = new HashSet<String>();
        List<Record> useTableRecord = useTable.list();
        for (Record record : useTableRecord) {
            blockIdSet.add(record.get("blockId").asString());
        }
        properties.put("blockIds", blockIdSet);
        StatementResult useTable2 = neo4jDao.executeReadCypher(queryTable2, properties);
        neo4jDao.close();
        
        String jsonString = Neo4jResultProcessUtil.generateDependencyJsonFile(jsonFilePath, programName, caller,
                callee, useTableRecord, useTable2, useFile, useCpy);
        String programId = pgmId.single().get("pgmId").asString();
        DependencyDto dto = new DependencyDto(programId, jsonString, jsonFilePath);
        return dto;
    }

    public CheckMissingResult checkMissing(String projectId) {
        // TODO Auto-generated method stub
        CheckMissingResult checkMissingResult = new CheckMissingResult();
        List<CheckMissingItem> results = new ArrayList<CheckMissingItem>();
        Project project = projectRepository.findOne(String.valueOf(projectId));
        String projectPath = project.getPath();
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        List<String> copyBookList = new ArrayList<String>();
        List<String> programList = new ArrayList<String>();
        List<String> tableList = new ArrayList<String>();
        String path = projectPath + "/output/" + codeVersion + "/so/missing/";
        try {
            File copybook = new File(path + MissingDocType.COPYBOOK.toString());
            File program = new File(path + MissingDocType.PROGRAM.toString());
            File table = new File(path + MissingDocType.TABLE.toString());
            if (copybook.exists()) {
                copyBookList = FileUtils.readLines(copybook);
                checkMissingResult.setCopybookLength(copyBookList.size());
                for (String string : copyBookList) {
                    results.add(new CheckMissingItem(string, "copybook"));
                }
            }
            if (program.exists()) {
                programList = FileUtils.readLines(program);
                checkMissingResult.setProgramLength(programList.size());
                for (String string : programList) {
                    results.add(new CheckMissingItem(string, "program"));
                }
            }
            if (table.exists()) {
                tableList = FileUtils.readLines(table);
                checkMissingResult.setTableLength(tableList.size());
                for (String string : tableList) {
                    results.add(new CheckMissingItem(string, "table"));
                }
            }
            checkMissingResult.setCheckMissiongItems(results);
        } catch (IOException e) {
            // TODO: handle exception
            return null;
        }
        return checkMissingResult;
    }

    public void downloadCurCtrlFlowDoc(DocDownloadInfo docInfo,
            HttpServletResponse response) {
        String projectId = docInfo.getProjectId();
        String programName = docInfo.getProgramName();
        String controlFlowName = docInfo.getControlFlowName();
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return;
        }
        String docFilePath = generateCurCtrlFlowDoc(projectId, project.getPath(), project.getName(), programName,
                controlFlowName);
        if (docFilePath == null) {
            return;
        }
        File docFile = new File(docFilePath);
        responseFileOutputStream(response, docFile);
    }

    public void downloadCurFileStructureDoc(DocDownloadInfo docInfo, HttpServletResponse response) {
        String projectId = docInfo.getProjectId();
        String programName = docInfo.getProgramName();
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return;
        }
        List<FileStructureNode> fileStructureNodes = getFileStructure(projectId, programName);
        FileStructureNode root = getRootNode(fileStructureNodes);
        if (root == null) {
            return;
        }
        List<FileStructureNode> filtered = filterFileStructureNodes(fileStructureNodes);
        fillDepth(filtered, root, 0);
        String rootName = filtered.get(0).getName();
        List<FileStructureNode> sorted = new ArrayList<FileStructureNode>();
        sortStructureNodes(root, filtered, sorted);
        String docFilePath = generateCurFileStructureDoc(projectId, project.getPath(), project.getName(), programName,
                rootName, sorted);
        if (docFilePath == null) {
            return;
        }
        File docFile = new File(docFilePath);
        responseFileOutputStream(response, docFile);
    }

    public void downloadCurDependencyDoc(DocDownloadInfo docInfo,
            HttpServletResponse response) {
        String programName = docInfo.getProgramName();
        String projectId = docInfo.getProjectId();
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return;
        }
        Map<String, Object> datas = new HashMap<>();
        datas.put("projectName", project.getName());
        datas.put("programName", programName);
        datas.put("externalDependencies", getExternalDependencies(projectId, programName));
        
        String projectPath = project.getPath();
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String docDir = FilePathUtil.getSingleGraphDocPath(projectPath, codeVersion);
        File f = new File(docDir + "/dependency/" + programName);
        if (!f.exists()) {
            f.mkdirs();
        }
        String docFilePath = docDir + "/dependency/" + programName + "/dependency_documentation_" + programName + ".doc";
        TemplateUtil.generateFile(CUR_DEPENDENCY_DOC_FTL, docFilePath, datas);
        File docFile = new File(docFilePath);
        responseFileOutputStream(response, docFile);
    }

//    public void downloadOneProgramDoc(DocDownloadInfo docInfo, HttpServletResponse response) {
//        String projectId = docInfo.getProjectId();
//        Project project = projectRepository.findOne(projectId);
//        if (null == project) {
//            return;
//        }
//        String projectName = project.getName();
//        String programName = docInfo.getProgramName();
//        String projectPath = project.getPath();
//        String codeVersion = FileStatusUtil.checkCode(projectPath);
//        
//        Map<String, PicIndexAndBase64> ctrlFlowImgMap = getControlFlowImgMap(projectPath, programName, codeVersion);
//        
//        List<FileStructureNode> fileStructureNodes = getFileStructure(projectId, programName);
//        FileStructureNode root = getRootNode(fileStructureNodes);
//        if (root == null) {
//            return;
//        }
//        List<FileStructureNode> filtered = filterFileStructureNodes(fileStructureNodes);
//        fillDepth(filtered, root, 0);
//        String rootName = filtered.get(0).getName();
//        List<FileStructureNode> sorted = new ArrayList<FileStructureNode>();
//        sortStructureNodes(root, filtered, sorted);
//        String dependencyImg = docInfo.getBase64Str();
//                
//        Map<String, Object> datas = new HashMap<>();
//        datas.put("projectName", projectName);
//        datas.put("programName", programName);
//        datas.put("controlflowImgMap", ctrlFlowImgMap);
//        datas.put("rootName", rootName);
//        datas.put("rows", sorted);
//        datas.put("dependencyImg", dependencyImg);
//        
//        String docDir = FilePathUtil.getWholePgmDocPath(project.getPath(), codeVersion) + "/" + programName;
//        File dir = new File(docDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        String docFilePath = docDir + "/program_" + programName + ".doc";
//        TemplateUtil.generateFile(PGM_DOC_FTL, docFilePath, datas);
//        File docFile = new File(docFilePath);
//        responseFileOutputStream(response, docFile);
//    }
//    
    
    public String downloadOneProgramDoc(DocDownloadInfo docInfo, HttpServletResponse response) {
        String projectId = docInfo.getProjectId();
        String programName = docInfo.getProgramName();
//        String filePath = docInfo.getFilePath()
        Project project = projectRepository.findOne(projectId);
        String projectPath = project.getPath();
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String docDir = FilePathUtil.getWholePgmDocPath(project.getPath(), codeVersion) + "/" + programName;
        
        File dir = new File(docDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String docFilePath = docDir + "/program_" + programName + ".doc";
        String pdfPath = docDir + "/program_" + programName + ".pdf";
        File docFile = new File(docFilePath);
        File pdfFile = new File(pdfPath);
        if (!docFile.exists()) {
            Map<String, Object> datas = new HashMap<>();
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM d,yyyy");

            Properties properties = FilePathUtil.getProperties();
            String companyName = "";
            String logoBase64 = "";
            companyName = properties.getProperty("companyName");
            String logoPath = properties.getProperty("logoPath");
            logoBase64 = PictureUtil.imageToBase64Str(logoPath);
            datas.put("companyName", companyName);
            datas.put("logoBase64", logoBase64);

            ProgramInfo program = new ProgramInfo();

            String time = formatter.format(currentTime);
            String projectName = project.getName();

            program = getProgramInfo(projectId, projectName, projectPath, codeVersion, programName);

            datas.put("time", time);
            datas.put("program", program);

            TemplateUtil.generateFile(PGM_DOC_FTL, docFilePath, datas);
            docFile = new File(docFilePath);
            WordUtil.updateIndex(docFile);
        }
        if(!pdfFile.exists()){
            WordUtil.wordToPdf(docFile, pdfPath);
        }
        return docDir + "/program_" + programName;
    }

    public Map<String, PicIndexAndBase64> getControlFlowImgMap(String projectPath, String programName, String codeVersion) {
        Map<String, PicIndexAndBase64> result = new TreeMap<String, PicIndexAndBase64>();
        
        String controlFlowDir = FilePathUtil.getControlFlowPath(projectPath, codeVersion);
        File ctrlflowDir = new File(controlFlowDir + "/" + programName);
        
        int picIndex = 1;
        List<File> files = new ArrayList<File>();
        for (File f : ctrlflowDir.listFiles()) {
            files.add(f);
        }
        Collections.sort(files);
        
        String programSvg = controlFlowDir + "/" + programName + "/" + programName + ".svg";
        File pgmSvgFile = new File(programSvg);
        if (pgmSvgFile.exists()) {
            files.remove(pgmSvgFile);
            files.add(0, pgmSvgFile);
        }
        
        for (File f : files) {
            String absPath = f.getAbsolutePath();
            
            if (absPath.endsWith(".svg") || absPath.endsWith(".SVG")) {
                String fName = f.getName();
                String fileNameWithoutExt = fName.substring(0, fName.lastIndexOf("."));
                String docDir = FilePathUtil.getSingleGraphDocPath(projectPath, codeVersion);
                String svgPath = controlFlowDir + programName + "/" + fileNameWithoutExt + ".svg";
                String svgWithCssPath = controlFlowDir + programName + "/" + fileNameWithoutExt + "_with_style.svg";
                File tempFile = new File(svgWithCssPath);
                // add css to svg
                CtrlFlowSvgSize size = SvgPropertyAdderUtil.addCssToControlFlowSvg(svgPath, svgWithCssPath);
                String pngPath = docDir + "/controlFlow/" + programName + "/" + fName + ".png";
                File dir = new File(docDir + "/controlFlow/" + programName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                try {
                    String svgCode = FileUtils.readFileToString(tempFile);
                    SvgToPngUtil.convertToPng(svgCode, pngPath);
                    tempFile.delete();
                } catch (Exception e) {
                    LOGGER.info(e);
                }
                double ptWidth = calcPicWidth(size);
                PicIndexAndBase64 piab = new PicIndexAndBase64(String.valueOf(picIndex), getImageBase(pngPath), String.valueOf(ptWidth),fName);
                picIndex++;
                result.put(fName, piab);
            }
        }
        return result;
    }

    public void sortStructureNodes(FileStructureNode node, List<FileStructureNode> nodes,
            List<FileStructureNode> sorted) {
        ArrayList<FileStructureNode> children = node.getChildren();
        for (FileStructureNode child : children) {
            sorted.add(child);
            sortStructureNodes(child, nodes, sorted);
        }
    }

    public List<FileStructureNode> filterFileStructureNodes(List<FileStructureNode> fileStructureNodes) {
        List<FileStructureNode> filtered = new ArrayList<FileStructureNode>();
        for (FileStructureNode fs : fileStructureNodes) {
            String name = fs.getName();
            if (!name.contains("-EXIT")) {
                filtered.add(fs);
            }
        }
        return filtered;
    }

    public void responseFileOutputStream(HttpServletResponse response, File docFile) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + docFile.getName());
            response.setHeader("Content-Length", String.valueOf(docFile.length()));
            if (!docFile.exists()) {
                out.write("nothing".getBytes());
                return;
            }
            in = new BufferedInputStream(new FileInputStream(docFile));
            byte[] data = new byte[1024];
            int len = 0;
            while (-1 != (len = in.read(data, 0, data.length))) {
                out.write(data, 0, len);
            }
        } catch (Exception e) {
            LOGGER.error("Download failed.", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("IO Exception", e);
            }
        }
    }

    private String generateCurCtrlFlowDoc(String projectId, String projectPath, String projectName, String programName,
            String controlFlowName) {
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String controlFlowDir = FilePathUtil.getControlFlowPath(projectPath, codeVersion);
        String controlFlowFilePath = controlFlowDir + programName + "/" + controlFlowName + ".svg";
        String controlFlowWithCssFilePath = controlFlowDir + programName + "/" + controlFlowName + "_with_style.svg";
        // add css to svg
        CtrlFlowSvgSize size = SvgPropertyAdderUtil.addCssToControlFlowSvg(controlFlowFilePath, controlFlowWithCssFilePath);
        String docDir = FilePathUtil.getSingleGraphDocPath(projectPath, codeVersion);
        File f = new File(docDir + "/controlFlow/" + programName);
        if (!f.exists()) {
            f.mkdirs();
        }
        String pngFilePath = docDir + "/controlFlow/" + programName + "/" + controlFlowName + ".png";
        String docFilePath = docDir + "/controlFlow/" + programName + "/controlFlow_documentation_" + controlFlowName
                + ".doc";
        try {
            File temp = new File(controlFlowWithCssFilePath);
            String svgCode = FileUtils.readFileToString(temp);
//            svgCode = svgCode.replaceAll("<g class=\"grpahc", "<style>.node{fill: #ffffff;stroke: #000000;fill-opacity: 1;}     .node0{fill: #ffffff;stroke: #000000;fill-opacity: 0;}</style><g class=\"grpahc");
            temp.delete();
            SvgToPngUtil.convertToPng(svgCode, pngFilePath);
        } catch (Exception e) {
            LOGGER.info(e);
        }
        Map<String, Object> datas = new HashMap<>();
        datas.put("projectName", projectName);
        datas.put("programName", programName);
        datas.put("controlFlowName", controlFlowName);
        datas.put("image", getImageBase(pngFilePath));
        double ptWidth = calcPicWidth(size);
        datas.put("picWidth", String.valueOf(ptWidth));
        TemplateUtil.generateFile(CUR_CTRL_FLOW_DOC_FTL, docFilePath, datas);
        return docFilePath;
    }

    private double calcPicWidth(CtrlFlowSvgSize size) {
        double pxWidthStr = size.getWidth();
        double pxHeightStr = size.getHeight();
        double rate = pxWidthStr/pxHeightStr;
        double ptWidth =  CTRLFLOW_PIC_DEFAULT_HEIGHT * rate;
        return ptWidth;
    }

    private String generateCurFileStructureDoc(String projectId, String projectPath, String projectName,
            String programName, String rootName, List<FileStructureNode> fileStructureNodes) {
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String docDir = FilePathUtil.getSingleGraphDocPath(projectPath, codeVersion);
        File f = new File(docDir + "/fileStructure/" + programName);
        if (!f.exists()) {
            f.mkdirs();
        }
        String docFilePath = docDir + "/fileStructure/" + programName + "/fileStructure_documentation_" + programName
                + ".doc";
        Map<String, Object> datas = new HashMap<>();
        datas.put("projectName", projectName);
        datas.put("programName", programName);
        datas.put("rootName", rootName);
        datas.put("rows", fileStructureNodes);
        TemplateUtil.generateFile(CUR_FILE_STRUCTURE_DOC_FTL, docFilePath, datas);
        return docFilePath;
    }

    private String getImageBase(String pngFilePath) {
        File file = new File(pngFilePath);
        if (!file.exists()) {
            return "";
        }
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            LOGGER.error("getImageBase IO Exception");
        }
        String encodeBase64 = Base64.encodeBase64String(data);
        return encodeBase64;
    }

    public void fillDepth(List<FileStructureNode> nodes, FileStructureNode node, int depth) {
        List<FileStructureNode> children = findChildren(node, nodes);
        if (children == null || children.size() == 0) {
            return;
        }
        for (FileStructureNode child : children) {
            child.setDepth(depth);
            if (depth == 0) {
                child.setPicSize("10.5");
            } else {
                child.setPicSize("7.5");
            }
            fillDepth(nodes, child, depth + 1);
        }
    }

    private List<FileStructureNode> findChildren(FileStructureNode parent, List<FileStructureNode> nodes) {
        ArrayList<FileStructureNode> children = new ArrayList<FileStructureNode>();
        for (FileStructureNode node : nodes) {
            String pid = node.getPid();
            if (pid != null && pid.equals(parent.getId())) {
                children.add(node);
            }
        }
        parent.setChildren(children);
        return children;
    }

    public FileStructureNode getRootNode(List<FileStructureNode> fileStructureNodes) {
        for (FileStructureNode fs : fileStructureNodes) {
            String parentId = fs.getPid();
            if (parentId == null || (parentId != null && parentId.isEmpty())) {
                return fs;
            }
        }
        return null;
    }
    
    public List<String> filterStringToXML(List<String> list){
        for (int i=0;i<list.size();i++) {
            String string =list.get(i);
            string = string.replaceAll("<", "&lt;");
            string = string.replaceAll("&", "&amp;");
            string = string.replaceAll("","");
            list.set(i, string);
        }
        list.get(list.size()-1);
        return list;
    }
    
    public ProgramInfo getProgramInfo(String projectId, String projectName, String projectPath,
            String codeVersion, String programName) {
        ProgramInfo program = new ProgramInfo();

        String sourcePath = FilePathUtil.getPath(projectPath, "SOURCE");
        String cobolPath = FilePathUtil.getPath(sourcePath, "COBOL");
        String copybookPath = FilePathUtil.getPath(sourcePath, "COPYBOOK");
        String outputPath = FilePathUtil.getControlFlowPath(projectPath, codeVersion);
        
        String fileName = programName;
        String outputFilePath = FilePathUtil.getFileStructureTempPath(projectPath, fileName, codeVersion);
        if (updateFileStructure(outputFilePath)) {
            fileStructureVisitor(projectPath, cobolPath + "/" + fileName, copybookPath, outputFilePath, codeVersion);
        }

        if (updateControlFlow(projectPath, fileName, codeVersion)) {
            generateControlFlow(projectPath, cobolPath + "/" + fileName, copybookPath, outputPath, codeVersion);
        }

        // ctrlFlowImgMap
        Map<String, PicIndexAndBase64> ctrlFlowImgMap = getControlFlowImgMap(projectPath,
                programName, codeVersion);

        List<PicIndexAndBase64> ctrlFlowImgList = new ArrayList<PicIndexAndBase64>(ctrlFlowImgMap.values());
        PicIndexAndBase64 controlFlowRoot = ctrlFlowImgList.get(ctrlFlowImgList.size()-1);
        ctrlFlowImgList.remove(controlFlowRoot);
        ctrlFlowImgList.add(0,controlFlowRoot);
        // fileStructureNodes
        List<FileStructureNode> fileStructureNodes = getFileStructure(projectId, programName);
        FileStructureNode root = getRootNode(fileStructureNodes);
        String rootName = "";
        List<FileStructureNode> sorted = new ArrayList<FileStructureNode>();
        if (root == null) {
            rootName = programName;
            sorted = fileStructureNodes;
        }else{
            List<FileStructureNode> filtered = filterFileStructureNodes(fileStructureNodes);
            fillDepth(filtered, root, 0);
            rootName = filtered.get(0).getName();
            sortStructureNodes(root, filtered, sorted);
        }

        //dependencyImg
        // String dependencyImg = docInfo.getBase64Str();
        program.setExternalDependencies(getExternalDependencies(projectId, programName));

        // sourceCode
        List<String> sourceCode = getSourceCodeList(projectId, "COBOL/" + programName);
        sourceCode = filterStringToXML(sourceCode);

        program.setProjectName(projectName);
        program.setProgramName(programName);
        program.setCtrlFlowImgMap(ctrlFlowImgMap);
        program.setCtrlFlowImgList(ctrlFlowImgList);
        program.setRootName(rootName);
        program.setRows(sorted);
        program.setSourceCode(sourceCode);
        return program;
    }
    
    
    public String[][] getExternalDependencies(String projectId, String programName) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            return null;
        }
        String projectPath = project.getPath();
        int boltPort = portUtil.getBoltPort(projectPath);
        String uri = "bolt://localhost:" + boltPort;
        Neo4jDao neo4jDao;
        try {
            neo4jDao = new Neo4jDao(uri);
        } catch (IllegalArgumentException e) {
            // TODO: handle exception
            LOGGER.info("neo4j is closed");
            return null;
        }

        String[][] sheet = new String[8][4];
        sheet[0] = new String[] { "Dependency Type", "Relation", "Name(s)", "count" };
        sheet[1] = new String[] { "Copybook", "use", "", "0" };
        sheet[2] = new String[] { "Input File", "read", "", "0" };
        sheet[3] = new String[] { "Output File", "write", "", "0" };
        sheet[4] = new String[] { "Input Table", "read", "", "0" };
        sheet[5] = new String[] { "Output Table", "write", "", "0" };
        sheet[6] = new String[] { "Caller", "called by", "", "0" };
        sheet[7] = new String[] { "Callee", "call", "", "0" };
        // DependencyDto dependencyDto = codeBrowserService.getDependency(projectId, programName);
        // JSONObject jsonObject = JSONObject.parseObject(dependencyDto.getJsonString());
        // JSONArray nodes = JSONObject.parseArray(jsonObject.getString("nodes"));
        // JSONArray links = JSONObject.parseArray(jsonObject.getString("links"));
        //
        Map<String, Object> properties = new HashMap<String, Object>();
        String nodeId ="COBOL/" + programName;
        properties.put("programName", programName);
        properties.put("nodeId", nodeId);
        String queryCaller = "match(p1:Program)-[:call]->(p2:Program) where p2.nodeId contains {nodeId} return p1.name as pgm";
        String queryCallee = "match(p1:Program)-[:call]->(p2:Program) where p1.nodeId contains {nodeId} return p2.name as pgm";
//        String queryTable = "match(p:Program)-[:use_table]->(t:Table) where p.nodeId contains {nodeId} return t.name as table";
        String queryTable1 = "match(n:Program)-[:has_paragraph]->(p:Paragraph)<-[:blockToParagraph]-(b:BlockPosition),(b1:BlockPosition)-[r:sqlUseTable]->(t:Table) where n.nodeId contains {nodeId} and p.isExit = 'N' and b1.paraName = b.paraName return distinct b1.nodeId as blockId,t.name as table,r.operation as operation";
        String queryTable2 = "match(n:BlockPosition)-[r:sqlUseTable]->(t:Table) where not n.nodeId in {blockIds} and n.nodeId contains {nodeId} and n.nodeId contains('#SQL_STATEMENT#') return distinct n.nodeId as programId,t.name as table,r.operation as operation";
        String queryFile = "match(p:Program)-[:use_file]->(f:File) where p.nodeId contains {nodeId} return f.name as file,f.openType as type";
        String queryCopybook = "match(p:Program)-[:use_copy_command]->(cc:CopyCommand)-[:copy]->(c:Copybook) where p.nodeId contains {nodeId} return distinct c.name as cpyName";
        StatementResult caller = neo4jDao.executeReadCypher(queryCaller, properties);
        StatementResult callee = neo4jDao.executeReadCypher(queryCallee, properties);
        StatementResult useTable1 = neo4jDao.executeReadCypher(queryTable1, properties);
        StatementResult useFile = neo4jDao.executeReadCypher(queryFile, properties);
        StatementResult useCpy = neo4jDao.executeReadCypher(queryCopybook, properties);

        Set<String> records = new HashSet<String>();
        Set<String> records1 = new HashSet<String>();
        
        List<Record> cpyRecord = useCpy.list();
        for (Record record : cpyRecord) {
            records.add(record.get("cpyName").asString());
        }
        sheet[1][3] = records.size() + "";
        sheet[1][2] = records.toString().substring(1, records.toString().length() - 1);
        records.clear();

        List<Record> useFileRecord = useFile.list();
        for (Record record : useFileRecord) {
            String file = record.get("file").asString();
            String type = record.get("type").asString();
            if(type.equals("INPUT"))            
                records.add(file);
            else if(type.equals("OUTPUT"))
                records1.add(file);
        }
        sheet[2][3] = records.size() + "";
        sheet[2][2] = records.toString().substring(1, records.toString().length() - 1);
        sheet[3][3] = records1.size() + "";
        sheet[3][2] = records1.toString().substring(1, records1.toString().length() - 1);
        records.clear();
        records1.clear();
        
        Set<String> blockIdSet = new HashSet<String>();
        List<Record> useTableRecord = useTable1.list();
        for (Record record : useTableRecord) {
            String table = record.get("table").asString();
            String operation = record.get("operation").asString();
            blockIdSet.add(record.get("blockId").asString());
            if(operation.equals("R"))            
                records.add(table);
            else
                records1.add(table);
        }
        properties.put("blockIds", blockIdSet);
        StatementResult useTable2 = neo4jDao.executeReadCypher(queryTable2, properties);
        useTableRecord = useTable2.list();
        for (Record record : useTableRecord) {
            String table = record.get("table").asString();
            records1.add(table);
        }
        sheet[4][3] = records.size() + "";
        sheet[4][2] = records.toString().substring(1, records.toString().length() - 1);
        sheet[5][3] = records1.size() + "";
        sheet[5][2] = records1.toString().substring(1, records1.toString().length() - 1);
        records.clear();
        records1.clear();
        
        List<Record> callerRecord = caller.list();
        for (Record record : callerRecord) {
            records.add(record.get("pgm").asString());
        }
        sheet[6][3] = records.size() + "";
        sheet[6][2] = records.toString().substring(1, records.toString().length() - 1);
        records.clear();
        
        List<Record> calleeRecord = callee.list();
        for (Record record : calleeRecord) {
            records.add(record.get("pgm").asString());
        }
        sheet[7][3] = records.size() + "";
        sheet[7][2] = records.toString().substring(1, records.toString().length() - 1);
        records.clear();

        neo4jDao.close();
        return sheet;
    }
    
}
