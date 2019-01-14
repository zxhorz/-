package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.workbench.domain.AnalysisDependency;
import com.hengtiansoft.bluemorpho.workbench.domain.AnalysisType;
import com.hengtiansoft.bluemorpho.workbench.domain.JobStatus;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.AnalysisTypeResult;
import com.hengtiansoft.bluemorpho.workbench.dto.DependencyInfo;
import com.hengtiansoft.bluemorpho.workbench.dto.JobDependency;
import com.hengtiansoft.bluemorpho.workbench.dto.JobStatusResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ProgressBarResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ProjectStatusResponse;
import com.hengtiansoft.bluemorpho.workbench.enums.JobProcessStatus;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.JobInfo;
import com.hengtiansoft.bluemorpho.workbench.repository.AnalysisDependencyRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.AnalysisTypeRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.JobStatusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.JobNameUtil;

@Service
public class JobService {
	
	private static final Logger logger = Logger
			.getLogger(JobService.class);
	
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	AnalysisTypeRepository analysisTypeRepository;
	@Autowired
	AnalysisDependencyRepository analysisDependencyRepository;
	@Autowired
	JobStatusRepository jobStatusRepository;
	@Autowired
	private JobNameUtil jobNameUtil;
	@Autowired
	private UserRepository userRepository;

	public String checkStatus(String projectId) {
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return "NPE";
			}

		try{
		String version = FileStatusUtil.checkCode(project.getPath());
		List<String> jobIds = getSelectedAnalysisType(projectId);
		if(null==jobIds || jobIds.size()==0){
			return "OUTOFDATE";
		}
		
		for(String id : jobIds){
			List<JobStatus> find = jobStatusRepository.findByProjectIdAndAnalysisIdAndCodeVersion(projectId,id, version);
			
			if(null==find||find.size()==0){
				return "OUTOFDATE";
			}
			JobStatus flag = new JobStatus();
			for(JobStatus js: find){
				flag= js;
			}
			if(flag.getStatus().equals(JobProcessStatus.F.toString())||flag.getStatus().equals(JobProcessStatus.I.toString())){
				return "OUTOFDATE";
			}else{
				if(flag.getStatus().equals(JobProcessStatus.NS.toString())||flag.getStatus().equals(JobProcessStatus.P.toString())){
					return "ONDOING";
				}
			}
		}
			return "ONDATE";
		}catch(Exception e){
			logger.error(e);
			return "OUTOFDATE";
		}

	}

	public AnalysisTypeResult getAllAnalysisDependency() {
		List<AnalysisType> analysisTypes = analysisTypeRepository
				.findAllAnalysisTypes();
		List<AnalysisDependency> analysisDependencies = analysisDependencyRepository
				.getAllAnalysisDependencies();
		List<JobDependency> jobDependencys = convert2JobDependency(analysisDependencies);
		AnalysisTypeResult result = new AnalysisTypeResult(analysisTypes,
				jobDependencys);
		
		return result;
	}

	private List<JobDependency> convert2JobDependency(
			List<AnalysisDependency> analysisDependencies) {
		List<JobDependency> jobDependencyList=  new ArrayList<>();
		List<String> deps = new ArrayList<>();
		JobDependency jobDependency = null;
		for(AnalysisDependency dep: analysisDependencies){
			jobDependency = new JobDependency(dep.getRelierId(),dep.getRelieredId());
			jobDependencyList.add(jobDependency);
			if(!deps.contains(dep.getRelieredId())){
				deps.add(dep.getRelieredId());
			}
		}
		for(int i = 0; i<deps.size();i++){
			List<JobDependency> dependencyList = getExtDeps(deps.get(i),jobDependencyList);
			if(null!=dependencyList&&dependencyList.size()>0){
				for(JobDependency jobd : dependencyList){
					if(!containsDep(jobDependencyList,jobd)){
						jobDependencyList.add(jobd);
					}
				}
			}
		}
		return jobDependencyList;
	}

	private boolean containsDep(List<JobDependency> jobDependencyList,
			JobDependency jobd) {
		for(JobDependency jdb:jobDependencyList){
			if(jdb.getDependId().equals(jobd.getDependId())&&jdb.getId().equals(jobd.getId())){
				return true;
			}
		}
		return false;
	}

	private List<JobDependency> getExtDeps(String id,
			List<JobDependency> jobDependencyList) {
		List<JobDependency> jobs = new ArrayList<>();
		List<String> ids = new ArrayList<>();
		List<String> deps = new ArrayList<>();
		for(JobDependency job : jobDependencyList){
			if(job.getDependId().equals(id)&&!ids.contains(job.getId())){
				ids.add(job.getId());
			}
			if(job.getId().equals(id)&&!deps.contains(job.getDependId())){
				deps.add(job.getDependId());
			}
		}
		for(int j = 0;j<ids.size();j++){
			for(int k = 0;k<deps.size();k++){
				JobDependency jb = new JobDependency(ids.get(j),deps.get(k));
				jobs.add(jb);
			}
		}
		return jobs;
	}

	public String saveSelectedAnalysisType(DependencyInfo dependencyInfo) {
		String message;
		String projectId = dependencyInfo.getProjectId();
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return "";
		}
		//check old selected type
		List<String> oldSelected = new ArrayList<>();
		String filePath = FilePathUtil.getAnalysisTypePath(project.getPath());
		File file = new File(filePath);
		if(file.exists()){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				oldSelected.add(tempString.trim());
			}
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		}else{
			message = "A";
		}
		//
		message = compareSelectedName(oldSelected,dependencyInfo.getSelectedName());
		String content = StringUtils.join(dependencyInfo.getSelectedName(), "\n");
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			writer.write(content.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return message;
	}

	private String compareSelectedName(List<String> oldSelected,
			List<String> selectedName) {
		boolean hasAdd = false;
		boolean hasDelete = false;
		for(String oldName: oldSelected){
			if(!selectedName.contains(oldName)){
				hasDelete = true;
			}
		}
		for(String newName: selectedName){
			if(!oldSelected.contains(newName)){
				hasAdd = true;
			}
		}		
		if(hasAdd){
			return "A";
		}else if(hasDelete){
			return "D";
		}else{
			return "S";
		}
	}

	public List<String> getSelectedAnalysisType(String projectId) {
		List<String> analysisIds = new ArrayList<String>();
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return analysisIds;
		}
		String filePath = FilePathUtil.getAnalysisTypePath(project.getPath());
		File file = new File(filePath);
		if(!file.exists()){
			return analysisIds;
		}
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				analysisIds.add(analysisTypeRepository.findIdByName(tempString
						.trim()));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return analysisIds;
	}

	public List<ProgressBarResponse> getJobProgressBarStatus(String projectId) {
		List<JobStatus> jobstatus = jobStatusRepository.findWaitingOrRunningJobForProject(projectId);
		List<ProgressBarResponse> pbRes = new ArrayList<ProgressBarResponse>();
		if(null==jobstatus || jobstatus.size()==0){
			return pbRes;
		}
//		List<String> analysisIds = new ArrayList<String>();
		for(JobStatus js: jobstatus){
			ProgressBarResponse pb = buildProcessBarResponse(js);
//			analysisIds.add(pb.getAnalysisId());
			pbRes.add(pb);
		}
//		return sortProgressBar(projectId, analysisIds, pbRes);
		return pbRes;
	}

	// 对队列中job对应进度条进行排序
	/*private List<ProgressBarResponse> sortProgressBar(String projectId, List<String> analysisIds, List<ProgressBarResponse> pbRes) {
		ProgressBarResponse[] array = new ProgressBarResponse[pbRes.size()];
		pbRes.toArray(array);
		List<Pair<String>> pairs = getAllDependencyInDb();
		String rootValue = findTreeRoot(analysisIds, pairs);
		JobTree<String> jobTree = constructDependencyTree(rootValue, analysisIds, pairs, projectId, "1", "1");
		setLevels(jobTree, rootValue, array);
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = i + 1; j < array.length; j ++) {
				if (array[i].getLevel() > array[j].getLevel()) {
					ProgressBarResponse temp = array[i];
					array[i] = array[j];
					array[j] = temp;
				}
			}
		}
		return Arrays.asList(array);
	}

	private void setLevels(JobTree<String> jobTree, String rootValue, ProgressBarResponse[] array) {
		for (ProgressBarResponse ele : array) {
			String analysisId = ele.getAnalysisId();
			int level = setLevel(jobTree, rootValue, analysisId);
			ele.setLevel(level);
		}
	}

	private int setLevel(JobTree<String> jobTree, String rootValue, String analysisId) {
		int deepestLevel = 1;
		Node<String> currentNode = null;
		Node<String>[] nodes = jobTree.getNodes();
		for (Node<String> node : nodes) {
			if (node.getData().equals(analysisId)) {
				currentNode = node;
				break;
			}
		}
		if (currentNode != null) {
			return getDeepestLevel(nodes, rootValue, currentNode, deepestLevel);
		} else {
			return -1;
		}
	}

	private int getDeepestLevel(Node<String>[] nodes, String rootValue, Node<String> currentNode, int level) {
		if (currentNode.getData().equals(rootValue)) {
			return level;
		}
		List<Integer> parents = currentNode.getParents();
		for (Integer parentPos : parents) {
			int pos = parentPos.intValue();
			level++;
			if (nodes[pos].getData().equals(rootValue)) {
				return level;
			} else {
				level = getDeepestLevel(nodes, rootValue, nodes[pos], level);
			}
		}
		return level;
	}*/

	private ProgressBarResponse buildProcessBarResponse(JobStatus js) {
		ProgressBarResponse pb = new ProgressBarResponse();
		String jobName = js.getJobName();
		String name = analysisTypeRepository.findAnalysisNameById(js.getAnalysisTypeId());
		JobInfo info = jobNameUtil.parseJobInfoFromJobName(jobName);
		String userName = userRepository.findOne(info.getUserId()).getUsername();
		
		pb.setJobName(jobName);
		String isIncremental = js.getIsIncremental();
		if ("y".equals(isIncremental)) {
			pb.setIncremental(true);
		}
		pb.setAnalysisName(name);
		pb.setUserName(userName);
		pb.setCodeVersion(js.getCodeVersion());
		pb.setStartTime(null == js.getStartTime() ? "" : js.getStartTime().toString());
		if (JobProcessStatus.P.toString().equals(js.getStatus())) {
			pb.setStatus("running");
		} else if (JobProcessStatus.NS.toString().equals(js.getStatus())) {
			pb.setStatus("waiting");
		}
		return pb;
	}
	
	private JobStatusResponse buildJobStatusResponse(JobStatus js) {
		String name;
		JobStatusResponse jsr = new JobStatusResponse();
		jsr.setCodeVersion(js.getCodeVersion());
		jsr.setJobStatus(js.getStatus());
		jsr.setStartTime(null==js.getStartTime()?"":js.getStartTime().toString());
		jsr.setStopTime(null==js.getStopTime()?"":js.getStopTime().toString());
		name = analysisTypeRepository.findAnalysisNameById(js.getAnalysisTypeId());
		jsr.setAnalysisName(name);
		return jsr;
	}

	private List<JobStatusResponse> buildOriJobStatusResponse(
			String codeVersion, List<String> analysisTypeIds) {
		List<JobStatusResponse> responses = new ArrayList<JobStatusResponse>();
		for (String analysisTypeId : analysisTypeIds) {
			JobStatusResponse jsr = new JobStatusResponse();
			jsr.setCodeVersion(codeVersion);
			jsr.setJobStatus("NS");
			jsr.setNeedUpdate(true);
			jsr.setAnalysisName(analysisTypeRepository
					.findAnalysisNameById(analysisTypeId));
			responses.add(jsr);
		}
		return responses;
	}

	public String getSpecJobStatus(String projectId, String jobName) {
		Project project = projectRepository.findOne(projectId);
		if(null==project){
			return "E";
		}
		String version = FileStatusUtil.checkCode(project.getPath());
		String jobId = analysisTypeRepository.findIdByName(jobName);
		if(null==jobId){
			return JobProcessStatus.S.toString();
		}
		List<JobStatus> jobstatus = jobStatusRepository.findByProjectIdAndAnalysisIdAndCodeVersion(projectId, jobId, version);
		if(null==jobstatus||jobstatus.size()==0){
			return "N";
		}
		String status = "N";
		
		for(JobStatus js: jobstatus){
			if(!js.getStatus().equals(JobProcessStatus.F.toString())
					&& !js.getStatus().equals(JobProcessStatus.I.toString())){
				status = js.getStatus();	
					}
		}
		return status;
	}

	public List<JobStatusResponse> getJobDependency(String projectId, String jobName) {
		String jobId = analysisTypeRepository.findIdByName(jobName);
		Project project = projectRepository.findOne(projectId);
		String version = FileStatusUtil.checkCode(project.getPath());
		List<JobStatusResponse> dependency = getAllDependById(jobId,version,projectId);
		return dependency;
	}

	private List<JobStatusResponse> getAllDependById(String jobId,String version, String projectId) {
		List<JobStatusResponse> dependency = new ArrayList<>();
		List<AnalysisDependency> analysisDependencies = analysisDependencyRepository
				.getAllAnalysisDependencies();
		List<JobDependency> jobDependencys = convert2JobDependency(analysisDependencies);
		for(JobDependency job: jobDependencys){
			if(jobId.equals(job.getId())){
				JobStatusResponse jr = generateJobDependency(version, projectId,job.getDependId());
				if(null!=jr&&!dependency.contains(jr)){
					dependency.add(jr);						
				}
			}
		}
		JobStatusResponse jr = generateJobDependency(version, projectId,jobId);
		if(null!=jr&&!dependency.contains(jr)){
			dependency.add(jr);						
		}
		
		//默认数据库中jobtype按照依赖关系存储，靠前的jobtype会可能会被后面的job依赖；
		//如果数据库存储形式发生变化，以下的排序方法需要修改：
		Collections.sort(dependency,  new Comparator<JobStatusResponse>(){
			public int compare(JobStatusResponse jsr1, JobStatusResponse jsr2){
				int id1 = Integer.valueOf(jsr1.getId()).intValue();
				int id2 = Integer.valueOf(jsr2.getId()).intValue();
				if(id1>id2){
					return 1;					
				}else if(id1<id2){
					return -1;
				}else {
					return 0;
				}
			}
		});
		return dependency;
	}

	private JobStatusResponse generateJobDependency(String version,
			String projectId,String dependId) {
		AnalysisType analysisType = analysisTypeRepository.findOne(dependId);
		if(null!=analysisType){
			List<JobStatus> jobStatus = jobStatusRepository.findByProjectIdAndAnalysisIdAndCodeVersion(projectId, analysisType.getId(), version);
			String status="";
			if(null==jobStatus||jobStatus.size()==0){
				status = "missing";
			}else{
				String flag = "N";
				for(JobStatus js: jobStatus){
					if(!js.getStatus().equals(JobProcessStatus.F.toString())
							&& !js.getStatus().equals(JobProcessStatus.I.toString())){
						flag = js.getStatus();	
							}
				}
				if(JobProcessStatus.S.toString().equals(flag)){
					status = "ready, up to date";
				}else{
					status = "datat set is not ready";
				}					
			}

			JobStatusResponse jr = new JobStatusResponse(analysisType.getId(),analysisType.getAnalysisName(),status);
			return jr;
		}
		return null;
	}

	public void updateSelectedAnalysisType(DependencyInfo dependencyInfo) {
		Project project = projectRepository.findOne(dependencyInfo.getProjectId());
		if (project == null) {
			return;
		}
		String filePath = FilePathUtil.getAnalysisTypePath(project.getPath());
		File file = new File(filePath);
		if(!file.exists()){
			saveSelectedAnalysisType(dependencyInfo);
		}else{
			DependencyInfo info = removeDuplication(dependencyInfo,file);
			saveSelectedAnalysisType(info);
		}
	}

	private DependencyInfo removeDuplication(DependencyInfo dependencyInfo,
			File file) {
		List<String> analysisNames = dependencyInfo.getSelectedName();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				if(!analysisNames.contains(tempString.trim())){
					analysisNames.add(tempString.trim());
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		DependencyInfo info = new DependencyInfo();
		info.setProjectId(dependencyInfo.getProjectId());
		info.setSelectedName(analysisNames);
		return info;
	}

	public ProjectStatusResponse getProjectStatus(String projectId) {
		ProjectStatusResponse psr = new ProjectStatusResponse();
		List<JobStatusResponse> jsrs = new ArrayList<>();
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return psr;
		}
		String version = FileStatusUtil.checkCode(project.getPath());
		boolean outOfdate = false;//out of date
		boolean upTodate = true;// up to date
		boolean updating = false;// updating 
		try{
		List<String> jobIds = getSelectedAnalysisType(projectId);
		if(null==jobIds || jobIds.size()==0){
			psr.setProjectStatus("OUTOFDATE");
			psr.setJobStatus(jsrs);
			completionSoAndCloneStatus(projectId, version, jsrs);
			return psr;
		}
		for(String id : jobIds){
			List<JobStatus> find = jobStatusRepository.findByProjectIdAndAnalysisIdAndCodeVersion(projectId,id, version);
			if(null==find|| find.size()==0){
				outOfdate = true;
				upTodate = false;
				continue;
			}else{
				JobStatus flag = new JobStatus();
				//默认相同的时间内，相同项目，相同代码版本的同一类型JOB只会执行一个。
				//按开始时间排序的结果中，最后一条为最新的jobstatus。
				flag = find.get(find.size()-1);
//				for(JobStatus js: find){
//						flag = js;
//				}
				if(flag.getStatus().equals(JobProcessStatus.F.toString())||flag.getStatus().equals(JobProcessStatus.I.toString())){
					outOfdate = true;
					upTodate = false;
				}else if(flag.getStatus().equals(JobProcessStatus.NS.toString())||flag.getStatus().equals(JobProcessStatus.P.toString())){
					upTodate = false;
					updating = true;
				}
				JobStatusResponse jsr = buildJobStatusResponse(flag);
				jsrs.add(jsr);
			}
		}
		}catch(Exception e){
			logger.error(e);
			psr.setProjectStatus("OUTOFDATE");
			psr.setJobStatus(jsrs);
			completionSoAndCloneStatus(projectId, version, jsrs);
			return psr;
		}
		if(updating){
			psr.setProjectStatus("ONDOING");
		}else if(outOfdate){
			psr.setProjectStatus("OUTOFDATE");
		}else if(upTodate){
			psr.setProjectStatus("ONDATE");
		}
		psr.setJobStatus(jsrs);
		completionSoAndCloneStatus(projectId, version, jsrs);
		return psr;
	}

	/**
	 * so和clone分析结果界面是否展示只取决于这两个分析是否完成，
	 * 而不取决于required data set复选框中是否选择这两项内容，
	 * 因此返回结果中补全so和clone的job状态
	 */
	private void completionSoAndCloneStatus(String projectId, String version,
			List<JobStatusResponse> jsrs) {
		boolean containsSo = false;
		boolean containsClone = false;
		for (JobStatusResponse jsr : jsrs) {
			if (jsr.getAnalysisName().equals(
					com.hengtiansoft.bluemorpho.workbench.enums.AnalysisType.SO.toString())) {
				containsSo = true;
			}
			if (jsr.getAnalysisName().equals(
					com.hengtiansoft.bluemorpho.workbench.enums.AnalysisType.CLONE_CODE.toString())) {
				containsClone = true;
			}
			if (containsSo && containsClone) {
				return;
			}
		}
		if (!containsSo) {
			String soName = com.hengtiansoft.bluemorpho.workbench.enums.AnalysisType.SO.toString();
			String soId = analysisTypeRepository.findByName(soName).getId();
			List<JobStatus> soFinds = jobStatusRepository.findByProjectIdAndAnalysisIdAndCodeVersion(projectId, soId, version);
			if (soFinds != null && soFinds.size() > 0) {
				JobStatus js = soFinds.get(soFinds.size() - 1);
				JobStatusResponse jsr = buildJobStatusResponse(js);
				jsrs.add(jsr);
			}
		}
		if (!containsClone) {
			String cloneName = com.hengtiansoft.bluemorpho.workbench.enums.AnalysisType.CLONE_CODE.toString();
			String cloneId = analysisTypeRepository.findByName(cloneName).getId();
			List<JobStatus> cloneFinds = jobStatusRepository.findByProjectIdAndAnalysisIdAndCodeVersion(projectId, cloneId, version);	
			if (cloneFinds != null && cloneFinds.size() > 0) {
				JobStatus js = cloneFinds.get(cloneFinds.size() - 1);
				JobStatusResponse jsr = buildJobStatusResponse(js);
				jsrs.add(jsr);
			}
		}
	}

	public String getJobStartTime(String jobName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = jobStatusRepository.findStartTimeByJobName(jobName);
		String time = sdf.format(date);
		return time;
	}

	public int getFileCount(int projectId) {
		
		int count = 0 ;
		
		if((count = getFileCount(projectId,"COBOL"))>0) {
			return count;
		}

		if((count = getFileCount(projectId,"COPYBOOK"))>0) {
			return count;
		}
		
		if((count = getFileCount(projectId,"JOB"))>0) {
			return count;
		}
		
		if((count = getFileCount(projectId,"PROC"))>0) {
			return count;
		}

		return 0;
	}

	public int getFileCount(int projectId,String folderName) {
		Project project = projectRepository.findOne(String.valueOf(projectId));
		if (project != null) {
			String filePath = FilePathUtil.getPath(
					FilePathUtil.getPath(project.getPath(), "SOURCE"), folderName);
			filePath = StringUtils.replace(filePath, "\\", "/");
			return fileCount(filePath);
		}
		return 0;
	}
	
	public int fileCount(String filePath) {
		int count = 0;
		File file = new File(filePath);
		if (!file.isDirectory()) {
			count++;
		} else if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(filePath + "/" + filelist[i]);
				if (!readfile.isDirectory()) {
					count++;
				} else if (readfile.isDirectory()) {
					count += fileCount(filePath + "/" + filelist[i]);
				}
			}
		}
		return count;
	}

	public List<JobStatusResponse> getJobHistory(String projectId) {
		List<JobStatus> jobstatus = jobStatusRepository.findAllExceptWaitingOrRunningJobs(projectId);
		List<JobStatusResponse> jobResponse = new ArrayList<JobStatusResponse>();
		if(null==jobstatus || jobstatus.size()==0){
			return jobResponse;
		}
		for(JobStatus js: jobstatus){
			JobStatusResponse jsr = buildJobStatusResponse(js);
			jobResponse.add(jsr);
		}
		return jobResponse;
	}

	/**
	 * 得到该project的最新codeVersion中的job status，并标记Job是否需要update
	 *
	 * @param projectId
	 * @return
	 */
	public List<JobStatusResponse> getJobUpdateStatus(String projectId) {
		List<JobStatusResponse> responses = new ArrayList<JobStatusResponse>();
		List<String> analysisTypeIds = getSelectedAnalysisType(String
				.valueOf(projectId));
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return responses;
		}
		int currCodeVersion = Integer.valueOf(FileStatusUtil.checkCode(project
				.getPath()));
		// project没有进行任何分析时，根据analysisType，所有Job为NS状态
		List<JobStatus> oriJobStatus = jobStatusRepository
				.findByProjectId(projectId);
		if (oriJobStatus.size() == 0) {
			return buildOriJobStatusResponse(String.valueOf(currCodeVersion),
					analysisTypeIds);
		}
		List<JobStatus> jobstatus = jobStatusRepository.findLatestJobs(
				projectId, analysisTypeIds);
		int codeVersion = 0;
		if (jobstatus.size() < 1) {
			return responses;
		}

		List<String> typeIds = new ArrayList<String>();
		codeVersion = Integer.valueOf(jobstatus.get(0).getCodeVersion());
		for (JobStatus js : jobstatus) {
			typeIds.add(js.getAnalysisTypeId());
			JobStatusResponse jsr = buildJobStatusResponse(js);
			if (codeVersion < currCodeVersion) {
				jsr.setNeedUpdate(true);
			} else {
				// 失败的Job才需要重新运行
				if (JobProcessStatus.F.toString().equals(jsr.getJobStatus())) {
					jsr.setNeedUpdate(true);
				}
			}
			responses.add(jsr);
		}
		// 未在jobStatus中的job
		analysisTypeIds.removeAll(typeIds);
		responses.addAll(buildOriJobStatusResponse(
				String.valueOf(currCodeVersion), analysisTypeIds));
		return responses;
	}

//	/**
//	 * construct analysis dependency tree
//	 * @param time 
//	 */
//	public JobTree<String> constructDependencyTree(String rootValue, 
//			List<String> dependencyIds, List<Pair<String>> pairs, String projectId, String codeVersion, String time) {
//		JobTree<String> tree = new JobTree<String>(rootValue, dependencyIds.size(), projectId, codeVersion, time);
//		fillTree(tree, rootValue, tree.root(), dependencyIds, pairs);
//		return tree;
//	}
//
//	/**
//	 * depth-first fill the dependency tree
//	 */
//	private void fillTree(JobTree<String> tree, String parentValue, 
//			Node<String> parentNode, List<String> dependencyIds, List<Pair<String>> pairs) {
//		List<String> children =  findChildren(parentValue, dependencyIds, pairs);
//		for (String child : children) {
//			Node<String> childNode = null;
//			if (!tree.getNodeValues().contains(child)) {
//				childNode = tree.addNode(child, parentNode);
//				fillTree(tree, child, childNode, dependencyIds, pairs);
//			} else {
//				for (Node<String> c : tree.getNodes()) {
//					if (c != null && c.getData().equals(child)) {
//						c.addParent(tree.pos(parentNode));
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * find one level deep children of parent
//	 */
//	private List<String> findChildren(String parent, 
//			List<String> dependencyIds, List<Pair<String>> pairs) {
//		List<String> result = new ArrayList<String>();
//		for (Pair<String> pair : pairs) {
//			String right = pair.getRight();
//			String left = pair.getLeft();
//			if (left.equals(parent) 
//					&& dependencyIds.contains(right) 
//					&& !result.contains(right)) {
//				result.add(right);
//			}
//		}
//		return result;
//	}
//	
//	public String findTreeRoot(List<String> dependencyIds, List<Pair<String>> pairs) {
//		for (String id : dependencyIds) {
//			for (Pair<String> pair : pairs) {
//				if (pair.getRight().equals(id)) {
//					continue;
//				}
//			}
//			return id;
//		}
//		return null;
//	}
//	
//	private List<Pair<String>> getAllDependencyInDb() {
//		List<Pair<String>> pairs = new ArrayList<Pair<String>>();
//		Iterator<AnalysisDependency> iterator = analysisDependencyRepository.findAll().iterator();
//		while (iterator.hasNext()) {
//			AnalysisDependency next = iterator.next();
//			pairs.add(new Pair<String>(next.getRelieredId(), next.getRelierId()));
//		}
//		return pairs;
//	}
	
}
