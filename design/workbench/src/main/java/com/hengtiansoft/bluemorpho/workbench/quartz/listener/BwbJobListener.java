package com.hengtiansoft.bluemorpho.workbench.quartz.listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.domain.JobStatus;
import com.hengtiansoft.bluemorpho.workbench.enums.AnalysisType;
import com.hengtiansoft.bluemorpho.workbench.enums.JobProcessStatus;
import com.hengtiansoft.bluemorpho.workbench.neo4j.Neo4jServerPool;
import com.hengtiansoft.bluemorpho.workbench.quartz.QuartzManager;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.JobInfo;
import com.hengtiansoft.bluemorpho.workbench.repository.AnalysisTypeRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.JobStatusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.JobNameUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.websocket.ProgressBarWebSocket;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 10:29:55 AM
 */
@Component
public class BwbJobListener extends JobListenerSupport {

	private static final Logger LOGGER = Logger.getLogger(BwbJobListener.class);
	@Autowired
	private QuartzManager quartzManager;
	@Autowired
	private JobStatusRepository jobStatusRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private AnalysisTypeRepository analysisTypeRepository;
	@Autowired
	private Neo4jServerPool pool;
	@Autowired
	private JobNameUtil jobNameUtil;
	@Autowired
	private ProgressBarWebSocket webSocket;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PortUtil portUtil;
	
	private static final String IGNORE_JOB_NAME = "autoTagFeedbackJobDetail";
	@Override
	public String getName() {
		return BwbJobListener.class.toString();
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		JobDetail jobDetail = context.getJobDetail();
		JobKey key = jobDetail.getKey();
		String jobName = key.getName();
		if (IGNORE_JOB_NAME.equals(jobName)) {
			return;
		}
		JobInfo info = jobNameUtil.parseJobInfoFromJobName(jobName);
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		String precursorJobName = jobDataMap.getString("precursorJobName");
		LOGGER.info("job to be executed(" + jobName + "). precursorJobName : " + precursorJobName);
		
		// change status in queue(from waitting queue to executing queue)
		quartzManager.convertToExecutingStatus(jobName);
		
		// change status in db
		JobStatus find = jobStatusRepository.findbyName(jobName);
		if (find != null) {
			find.setStartTime(new Date());
			find.setStatus(JobProcessStatus.P.toString());
			jobStatusRepository.save(find);
		}

		// change progressbar status:running
		changeProgressBarInfo(jobName + "/status:running", info.getProjectId());

		String analysisname = analysisTypeRepository.findAnalysisNameById(info.getAnalysisTypeId());
		// so incremental analysis
		if (analysisname.equals(AnalysisType.SO.toString())) {
			prepareSoIncremental(info, jobDataMap);
		}
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		JobDetail jobDetail = context.getJobDetail();
		JobKey key = jobDetail.getKey();
		String jobName = key.getName();
		LOGGER.info(jobName + " execution vetoed.");
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		String finishedJobName = context.getJobDetail().getKey().getName();
		if (IGNORE_JOB_NAME.equals(finishedJobName)) {
			return;
		}
		JobInfo info = jobNameUtil.parseJobInfoFromJobName(finishedJobName);
		String analysisname = analysisTypeRepository.findAnalysisNameById(info.getAnalysisTypeId());
		String username = userRepository.findOne(info.getUserId()).getUsername();
		
		// change status in queue(remove from executing queue)
		quartzManager.convertToExecutedStatus(finishedJobName);
		
		String code = context.getJobDetail().getJobDataMap().getString("code");
		if (!"0".equals(code)) {
			// change status in db
			JobStatus find = jobStatusRepository.findbyName(finishedJobName);
			if (find != null) {
				find.setStopTime(new Date());
				find.setStatus(JobProcessStatus.F.toString());
				jobStatusRepository.save(find);
			}
			
			// change related jobs to interrupted status and remove from waiting queue.
			List<String> interruptedJobNames = quartzManager.getInterruptedJobNames(info);
			LOGGER.info("execution exception in job(" + finishedJobName + "), " + interruptedJobNames.size() + " related job interrupted.");
			if (interruptedJobNames.size() > 0) {
				LOGGER.info("###################################");
				for (String interruptedJob : interruptedJobNames) {
					LOGGER.info(interruptedJob);
				}
				LOGGER.info("###################################");
			}
			
			for (String interruptedJobName : interruptedJobNames) {
				// remove from waiting queue
				quartzManager.removeFromWaiting(interruptedJobName);
				// change job status in db
				JobStatus interrupted = jobStatusRepository.findbyName(interruptedJobName);
				if (interrupted != null) {
					interrupted.setStopTime(new Date());
					interrupted.setStatus(JobProcessStatus.I.toString());
					interrupted.setDescription("Interrupted by job : (" + finishedJobName + ")");
					jobStatusRepository.save(interrupted);
				}
				// change progressbar status
				changeProgressBarInfo(interruptedJobName + "/status:exception&Interrupted by previous "
						+ analysisname + " job(user:" + username + " codeVersion:" + info.getCodeVersion() + ")!", info.getProjectId());
			}
			
			quartzManager.allJobsInCurrentTreeProcessed(finishedJobName);
			
			// change progressbar status
			changeProgressBarInfo(finishedJobName + "/status:exception&Exception occured!", info.getProjectId());
			return;
		}
		
		LOGGER.info("job was executed(" + finishedJobName + ").");
		
		// if finished job is so analysis, start the neo4j server
		if (analysisname.equals(AnalysisType.SO.toString())) {
			String projectPath = projectRepository.findOne(info.getProjectId()).getPath();
			pool.getAndCreateDB(projectPath, info.getCodeVersion());
			LOGGER.info("Neo4j DB is opened");
		}
		
		// change status in db
		JobStatus find = jobStatusRepository.findbyName(finishedJobName);
		if (find != null) {
			find.setStopTime(new Date());
			find.setStatus(JobProcessStatus.S.toString());
			jobStatusRepository.save(find);
		}
		
		// change progressbar status
		changeProgressBarInfo(finishedJobName + "/100", info.getProjectId());
		
		quartzManager.allJobsInCurrentTreeProcessed(finishedJobName);
		
		List<String> toExecutes = new ArrayList<String>();
		quartzManager.nextRunnableJobs(finishedJobName, toExecutes);
		
		if (toExecutes.size() > 0) {
			// print all the next runable jobs.
			LOGGER.info("/-/-/-/-/-/-/-/-/-/-/-/-/-/-");
			for (String jobName : toExecutes) {
				LOGGER.info("next runnable jobs after " + finishedJobName + ": " + jobName);
			}
			LOGGER.info("/-/-/-/-/-/-/-/-/-/-/-/-/-/-");
			for (String jobName : toExecutes) {
				quartzManager.findAndRunJobInWaitingQueue(jobName, finishedJobName);
			}
		}
	}
	
	public void changeProgressBarInfo(String msg, String projectId) {
		try {
			webSocket.sendMessageTo(msg, projectId);
		} catch (IOException e) {
			LOGGER.info("websocket exception");
		}
	}
	
	private void prepareSoIncremental(JobInfo info, JobDataMap jobDataMap) {
		String isIncremental = jobDataMap.getString("isIncremental");
		if ("y".equals(isIncremental)) {
			LOGGER.info("Start so incrementally analysis.");
			String incrementBaseVersion = jobDataMap.getString("incrementBaseVersion");
			String codeVersion = jobDataMap.getString("codeVersion");
			String projectPath = projectRepository.findOne(info.getProjectId()).getPath();
			try {
				pool.destroy(projectPath +"/" + incrementBaseVersion);
				LOGGER.info("old neo4j destroied");
				// change progressbar status
				changeProgressBarInfo(info.getJobName() + "/3", info.getProjectId());
			} catch (Exception e) {
				LOGGER.error("old neo4j destroy exception.", e);
			}
			try {
				String oldNeo4jPath = projectPath + FilePathUtil.OUTPUT + "/" + incrementBaseVersion + FilePathUtil.OUTPUT_NEO4J_DB;
				String newNeo4jPath = projectPath + FilePathUtil.OUTPUT + "/" + codeVersion + FilePathUtil.OUTPUT_NEO4J_DB;
				newNeo4jPath = newNeo4jPath.substring(0, newNeo4jPath.lastIndexOf("/"));
				FileUtils.copyDirectoryToDirectory(new File(oldNeo4jPath), new File(newNeo4jPath));
				LOGGER.info("new neo4j copied.");
				// 插件jar包放至neo4j的plugin目录下
				FilePathUtil.placeNeo4jPlugin(projectPath, codeVersion);
				// change progressbar status
				changeProgressBarInfo(info.getJobName() + "/11", info.getProjectId());
			} catch (Exception e) {
				LOGGER.error("Copy neo4j to new db dir exception.");
			}
			try {
				pool.getAndCreateDB(projectPath, codeVersion);
				LOGGER.info("new neo4j started.");
				// change progressbar status
				changeProgressBarInfo(info.getJobName() + "/15", info.getProjectId());
			} catch (Exception e) {
				LOGGER.error("new neo4j start exception.");
			}
			String neo4jUri = "bolt://localhost:" + portUtil.getBoltPort(projectPath);
			jobDataMap.put("neo4jUri", neo4jUri);
		}
	}
	
}
