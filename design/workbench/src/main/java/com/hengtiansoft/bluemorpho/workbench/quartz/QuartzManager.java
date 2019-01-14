package com.hengtiansoft.bluemorpho.workbench.quartz;

import static org.quartz.impl.matchers.EverythingMatcher.allJobs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.domain.AnalysisDependency;
import com.hengtiansoft.bluemorpho.workbench.domain.JobStatus;
import com.hengtiansoft.bluemorpho.workbench.enums.JobProcessStatus;
import com.hengtiansoft.bluemorpho.workbench.enums.JobRequestHandleType;
import com.hengtiansoft.bluemorpho.workbench.neo4j.Neo4jServerPool;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.CloneCodeJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.ClusteringJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.ComplexityJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.CostEstimation;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.DataDependencyJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.DataMappingJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.DeadCodeJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.DependencyJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.SoJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.SystemDocumentationJob;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.JobInfo;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.JobTree;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.JobTree.Node;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.Pair;
import com.hengtiansoft.bluemorpho.workbench.quartz.listener.BwbJobListener;
import com.hengtiansoft.bluemorpho.workbench.repository.AnalysisDependencyRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.AnalysisTypeRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.JobStatusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.CodeVersionUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.JobNameUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;
import com.hengtiansoft.bluemorpho.workbench.util.UserUtil;
import com.hengtiansoft.bluemorpho.workbench.websocket.ProgressBarWebSocket;
/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 7:01:33 PM
 */
@Component
public class QuartzManager {

	private static final Logger LOGGER = Logger.getLogger(QuartzManager.class);
	@Autowired
	private AnalysisDependencyRepository analysisDependencyRepository;
	@Autowired
	private AnalysisTypeRepository analysisTypeRepository;
	@Autowired
	private JobStatusRepository jobStatusRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private CodeVersionUtil codeVersionUtil;
	@Autowired
	private UserUtil userUtil;
	@Autowired
	PortUtil portUtil;
	@Autowired
	private JobNameUtil jobNameUtil;
	@Autowired
	private Neo4jServerPool pool;
	@Autowired
	@Qualifier("bwbScheduler")
	private SchedulerFactoryBean schedulerFactoryBean;
	@Autowired
	private BwbJobListener bwbJobListener;
	@Autowired
	private ProgressBarWebSocket webSocket;	
	
	// a job tree represent a group of analysis from bwb UI
	private Map<String, JobTree<String>> jobTreeMap = new HashMap<String, JobTree<String>>();
	private List<JobInfo> waitingJobs = new ArrayList<JobInfo>();
	private List<JobInfo> executingJobs = new ArrayList<JobInfo>();
	private Scheduler scheduler = null;
	
	/**
	 * handle multiple analysis, remove the duplicate analysis,
	 * and construct analysis dependency trees. 
	 */
	public JobRequestHandleType handleAnalysisRequest(String projectId, List<String> analysisTypeIds) {
		setWebSocketInProcessBuilderUtil();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());

		String userId = userUtil.getCurrentUserId();
		String path = projectRepository.findOne(projectId).getPath();

		// current code version
		String codeVersion = FileStatusUtil.checkCode(path);
		
		String jobGroup = jobNameUtil.generatedJobGroup(projectId, codeVersion, userId, time);
		LOGGER.info("received one group of analysis job(" + jobGroup + ")......");
		prepareSchedulerAndTrigger(jobGroup);

		List<Pair<String>> pairs = getAllDependencyInDb();

		String rootValue = findTreeRoot(analysisTypeIds, pairs);
		if (rootValue == null) {
			LOGGER.error("can not find tree root.");
			return JobRequestHandleType.ERROR;
		} else {
			LOGGER.info("Constructing dependency tree......");
			LOGGER.info("tree root : " + rootValue);
			JobTree<String> jobTree = constructDependencyTree(rootValue, analysisTypeIds, pairs, projectId, codeVersion, time);
			// node in current tree don't has report and not contained in pre jobTree,
			// so current tree should add to jobTreeMap.
			boolean notAllNodeHasReport = notAllNodeHasReport(jobTree, projectId, codeVersion);
			boolean notAllNodeContainedInPreTreeMap = notAllNodeContainedInPreTreeMap(jobTree, projectId, codeVersion);
			if (notAllNodeHasReport && notAllNodeContainedInPreTreeMap) {
				this.jobTreeMap.put(jobGroup, jobTree);
				addToWaitting(jobGroup, analysisTypeIds, projectId, codeVersion, userId, time);
			} else {
				if (!notAllNodeHasReport) {
					LOGGER.info("current analysis has reports.");
					return JobRequestHandleType.HAS_REPORTS;
				}
				if (!notAllNodeContainedInPreTreeMap) {
					LOGGER.info("current analysis is contained in pre job.");
					return JobRequestHandleType.CONTAINED_IN_PRE_ANALYSIS;
				}
			}
			LOGGER.info("Tree was constructed..............");
			try {
				startJob(jobTree, projectId, codeVersion, userId, time);
			} catch (Exception e) {
				LOGGER.error(e);
				this.jobTreeMap.remove(jobGroup);
				clearCurrentGroupInQueue(jobGroup);
			}
		}
		return JobRequestHandleType.WAITING_TO_RUN;
	}

	public synchronized void addToWaitting(String jobGroup, List<String> analysisTypeIds,
			String projectId, String codeVersion, String userId, String time) {
		for (String analysisId : analysisTypeIds) {
			String jobName = jobNameUtil.generatedJobName(projectId, codeVersion, userId, analysisId, time);
			boolean hasReport = hasReport(projectId, analysisId, codeVersion);
			boolean containedInQueue = hasEquivalentJobInQueue(projectId, analysisId, codeVersion, jobName);
			if (hasReport || containedInQueue) {
				continue;
			} else {
				// if incremental analysis
				boolean isIncremental = false;
				String incrementBaseVersion = null;
				JobStatus lastStatus = getLastAnalysisStatusForProject(projectId, analysisId);
				if (lastStatus != null && !lastStatus.getCodeVersion().equals(codeVersion)) {
					isIncremental = true;
					incrementBaseVersion = lastStatus.getCodeVersion();
				}
				
				String analysisName = analysisTypeRepository.findOne(analysisId).getAnalysisName();
				JobInfo jobInfo = new JobInfo(projectId, analysisId, analysisName, 
						isIncremental, incrementBaseVersion, jobGroup, jobName, codeVersion);
				waitingJobs.add(jobInfo);
				// add to db
				String isIncre = isIncremental ? "y" : "n";
				JobStatus jobStatus = new JobStatus(projectId, jobName, analysisId, isIncre, incrementBaseVersion,
						null, null, codeVersion, JobProcessStatus.NS.toString(), null);
				jobStatusRepository.save(jobStatus);
			}
		}
	}

	private boolean notAllNodeHasReport(JobTree<String> jobTree, String projectId, String codeVersion) {
		List<String> leaves = jobTree.getLeaves();
		for (String leaf : leaves) {
			List<JobStatus> finds = jobStatusRepository.findByProjectIdAndAnalysisIdAndCodeVersion(projectId, leaf, codeVersion);
			if (finds == null) {
				return true;
			} else {
				if (finds.size() == 0) {
					return true;
				}
				if (finds.size() > 0) {
					for (JobStatus find : finds) {
						if (find == null
								|| (find != null && !find.getStatus().equals(JobProcessStatus.S.toString()))) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean notAllNodeContainedInPreTreeMap(JobTree<String> jobTree, String projectId, String codeVersion) {
		boolean mark = false;
		List<String> leaves = jobTree.getLeaves();
		for (String leaf : leaves) {
			boolean leafContainedInPreJobTree = false;
			for (String key : this.jobTreeMap.keySet()) {
				JobTree<String> treeInMap = this.jobTreeMap.get(key);
				if (projectId.equals(treeInMap.getProjectId()) 
						&& codeVersion.equals(treeInMap.getCodeVersion())
						&& treeInMap.getNodeValues().contains(leaf)) {
					leafContainedInPreJobTree = true;
					break;
				}
			}
			if (leafContainedInPreJobTree) {
				continue;
			} else {
				mark = true;
				break;
			}
		}
		return mark;
	}

	/**
	 * 从依赖树值为value的结点开始，为其每条子分支各找到首个需要分析的结点，加入toExecutes
	 * @param time2 
	 */
	private void startJob(JobTree<String> jobTree, 
			String projectId, String codeVersion, String userId, String time) {
		String rootValue = jobTree.root().getData();
		List<String> toExecutes = new ArrayList<String>();
		collectRunnableJobs(jobTree, rootValue, toExecutes, projectId, codeVersion, userId, time);
		LOGGER.info("/-/-/-/-/-/-/-/-/-/-/-/-/-/-");
		for (String jobName : toExecutes) {
			LOGGER.info("first runnable job of a jobTree: " + jobName);
		}
		LOGGER.info("/-/-/-/-/-/-/-/-/-/-/-/-/-/-");
		for (String jobName : toExecutes) {
			findAndRunJobInWaitingQueue(jobName, null);
		}
	}

	/**
	 * 单棵树value值的结点，其各个子分支找到首个需要分析的结点，加入toExecutes
	 */
	public void collectRunnableJobs(JobTree<String> jobTree, String value,
			List<String> toExecutes, String projectId, String codeVersion,
			String userId, String time) {
		int pos = findPosInTree(jobTree, value);
		if (pos == -1) {
			LOGGER.error("can not find tree node(" + value + ") in jobTree.");
		}
		String jobName = jobNameUtil.generatedJobName(projectId, codeVersion, userId, value, time);
		if (hasReport(projectId, value, codeVersion)) {
			// 已经分析过等价分析，且有report
			List<Node<String>> children = jobTree.oneLevelChildren(jobTree.getNodeByPosition(pos));
			for (Node<String> child : children) {
				collectRunnableJobs(jobTree, child.getData(), toExecutes, projectId, codeVersion, userId, time);
			}
		} else if (hasEquivalentJobInQueue(projectId, value, codeVersion, jobName)) {
			// 等价分析已存在waiting或executing队列,不做操作，等待等价分析完成后触发其child分析
			return;
		} else {
			Node<String> node = getTreeNodeByValue(jobTree, value);
			// root node, directly add to toExecutes
			if (jobTree.pos(node) == 0) {
				toExecutes.add(jobName);
				return;
			}
			List<Node<String>> parents = jobTree.parents(node);
			// one parent
			if (parents.size() == 1) {
				// 需要做分析（且addToWaiting方法已将其加入等待队列）
				toExecutes.add(jobName);
				return;
			}
			// several parents
			if (parents.size() > 1) {
				if (atLeastOneParentInQueue(projectId, parents, codeVersion)) {
					return;
				} else {
					// all parents done.
					// attention: each parent has one thread.
					// 			  maybe several threads will 
					//            duplicatly add current child
					//            to 'toExecutes' here.
					JobInfo findInWaiting = findJobInWaitingQueue(jobName);
					if (findInWaiting != null && !findInWaiting.isTriggeredByParent()) {
						toExecutes.add(jobName);
						findInWaiting.setTriggeredByParent(true);
					}
					return;
				}
			}
		}
	}
	
	private synchronized JobInfo findJobInWaitingQueue(String jobName) {
		for (JobInfo info : waitingJobs) {
			 if (info.getJobName().equals(jobName)) {
				 return info;
			 }
		}
		return null;
	}
	
	/**
	 * at least one parent has equivalent job or itself 
	 * in waiting queue/executing queue.
	 */
	private boolean atLeastOneParentInQueue(String projectId,
			List<Node<String>> parents, String codeVersion) {
		for (Node<String> parent : parents) {
			String parentValue = parent.getData();
			if (hasEquivalentJobOrSelfInQueue(projectId, parentValue, codeVersion)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get the next runnable jobs
	 */
	public void nextRunnableJobs(String jobName, List<String> toExecutes) {
		JobInfo info = jobNameUtil.parseJobInfoFromJobName(jobName);
		for (String key : this.jobTreeMap.keySet()) {
			JobTree<String> jobTree = this.jobTreeMap.get(key);
			collectRunnableJobs(jobTree, info.getAnalysisTypeId(), toExecutes, 
					info.getProjectId(), info.getCodeVersion(), info.getUserId(), jobTree.getRequestReceivedTime());
		}
	}
	
	// 已经做过该分析的等价分析，且有report
	public boolean hasReport(String projectId, String analysisTypeId, String codeVersion) {
		List<JobStatus> finds = jobStatusRepository.findByProjectIdAndAnalysisIdAndCodeVersion(projectId, analysisTypeId, codeVersion);
		if (finds != null && finds.size() > 0) {
			for (JobStatus find : finds) {
				if (find.getStatus().equals(JobProcessStatus.S.toString())) {
					return true;
				}
			}
		}
		return false;
	}
	
	// 该分析的等价分析（不包括其自己），已在waiting或executing队列中
	private synchronized boolean hasEquivalentJobInQueue(String projectId, 
			String analysisTypeId, String codeVersion, String jobName) {
		for (JobInfo info : waitingJobs) {
			if (projectId.equals(info.getProjectId())
					&& codeVersion.equals(info.getCodeVersion()) 
					&& analysisTypeId.equals(info.getAnalysisTypeId())
					// equivalent job not contains itself.
					&& !jobName.equals(info.getJobName())) {
				return true;
			}
		}
		for (JobInfo info : executingJobs) {
			if (projectId.equals(info.getProjectId())
					&& codeVersion.equals(info.getCodeVersion()) 
					&& analysisTypeId.equals(info.getAnalysisTypeId())
					// equivalent job not contains itself.
					&& !jobName.equals(info.getJobName())) {
				return true;
			}
		}
		return false;
	}
	
	// 该分析的等价分析（包括其自己），已在waiting或executing队列中
	private synchronized boolean hasEquivalentJobOrSelfInQueue(String projectId, 
			String analysisTypeId, String codeVersion) {
		for (JobInfo info : waitingJobs) {
			if (projectId.equals(info.getProjectId())
					&& codeVersion.equals(info.getCodeVersion()) 
					&& analysisTypeId.equals(info.getAnalysisTypeId())) {
				return true;
			}
		}
		for (JobInfo info : executingJobs) {
			if (projectId.equals(info.getProjectId())
					&& codeVersion.equals(info.getCodeVersion()) 
					&& analysisTypeId.equals(info.getAnalysisTypeId())) {
				return true;
			}
		}
		return false;
	}

	private int findPosInTree(JobTree<String> jobTree, String value) {
		for (Node<String> node : jobTree.getNodes()) {
			String nodeValue = node.getData();
			if (nodeValue.equals(value)) {
				return jobTree.pos(node);
			}
		}
		return -1;
	}

	/**
	 * get the next runnable jobs
	 */
	/*public Map<String, String> nextRunnableJobs(String lastFinishedJobName) {
		Map<String, String> runnableJobs = new HashMap<String, String>();
		String projectId = extractProjectId(lastFinishedJobName);
		// first time to run job, execute the root of the jobTree.
		if (lastFinishedJobName == null) {
			String rootTypeId = this.jobTree.root().getData();
			String rootTypeName = analysisTypeRepository.findOne(rootTypeId).getAnalysisName();
			String rootJobName = getKeyByValue(waitingJobs, rootTypeName);
			runnableJobs.put(rootJobName, rootTypeName);
			return runnableJobs;
		}

		String typeName = executingJobs.get(lastFinishedJobName);
		// analysised id in job tree
		String analysisId = analysisTypeRepository.findIdByName(typeName);
		for (Node<String> node : this.jobTree.getNodes()) {
			if (analysisId.equals(node.getData())) {
				// children id in job tree
				List<Node<String>> children = this.jobTree.children(node);
				for (Node<String> child : children) {
					List<Integer> parents = child.getParents();
					// analysised job is the only parent of this child
					if (parents.size() == 1 
							&& this.jobTree.getNodeByPosition(
									parents.get(0).intValue()).getData().equals(analysisId)) {
						String key = jobNameUitl.generatedJobName(projectId, child.getData());
						String value = analysisTypeRepository.findOne(child.getData()).getAnalysisName();
						runnableJobs.put(key, value);
					} else {
						boolean allParentsDone = true;
						// query if other parents all executed
						for (Integer parent : parents) {
							//							jobNameUitl.generatedJobName(projectId, );
							parent.intValue()
						}

					}
				}
			}
		}
	}*/

	public synchronized void convertToExecutingStatus(String jobName) {
		Iterator<JobInfo> iterator = waitingJobs.iterator();
		JobInfo temp = null;
		while (iterator.hasNext()) {
			JobInfo next = iterator.next();
			if (next.getJobName().equals(jobName)) {
				temp = next;
				iterator.remove();
				break;
			}
		}
		executingJobs.add(temp);
	}
	
	public synchronized void convertToExecutedStatus(String jobName) {
		Iterator<JobInfo> iterator = executingJobs.iterator();
		while (iterator.hasNext()) {
			JobInfo next = iterator.next();
			if (next.getJobName().equals(jobName)) {
				iterator.remove();
				break;
			}
		}
	}
	
	/**
	 * remove interrupted jobs in waiting queue. 
	 */
	public synchronized void removeFromWaiting(String jobName) {
		Iterator<JobInfo> iterator = waitingJobs.iterator();
		while (iterator.hasNext()) {
			JobInfo next = iterator.next();
			if (next.getJobName().equals(jobName)) {
				iterator.remove();
				break;
			}
		}
		allJobsInCurrentTreeProcessed(jobName);
	}

	private List<Pair<String>> getAllDependencyInDb() {
		List<Pair<String>> pairs = new ArrayList<Pair<String>>();
		Iterator<AnalysisDependency> iterator = analysisDependencyRepository.findAll().iterator();
		while (iterator.hasNext()) {
			AnalysisDependency next = iterator.next();
			pairs.add(new Pair<String>(next.getRelieredId(), next.getRelierId()));
		}
		return pairs;
	}

	/**
	 * construct analysis dependency tree
	 * @param time 
	 */
	public JobTree<String> constructDependencyTree(String rootValue, 
			List<String> dependencyIds, List<Pair<String>> pairs, String projectId, String codeVersion, String time) {
		JobTree<String> tree = new JobTree<String>(rootValue, dependencyIds.size(), projectId, codeVersion, time);
		fillTree(tree, rootValue, tree.root(), dependencyIds, pairs);
		return tree;
	}

	/**
	 * depth-first fill the dependency tree
	 */
	private void fillTree(JobTree<String> tree, String parentValue, 
			Node<String> parentNode, List<String> dependencyIds, List<Pair<String>> pairs) {
		List<String> children =  findChildren(parentValue, dependencyIds, pairs);
		for (String child : children) {
			Node<String> childNode = null;
			if (!tree.getNodeValues().contains(child)) {
				childNode = tree.addNode(child, parentNode);
				fillTree(tree, child, childNode, dependencyIds, pairs);
			} else {
				for (Node<String> c : tree.getNodes()) {
					if (c != null && c.getData().equals(child)) {
						c.addParent(tree.pos(parentNode));
					}
				}
			}
		}
	}

	/**
	 * find one level deep children of parent
	 */
	private List<String> findChildren(String parent, 
			List<String> dependencyIds, List<Pair<String>> pairs) {
		List<String> result = new ArrayList<String>();
		for (Pair<String> pair : pairs) {
			String right = pair.getRight();
			String left = pair.getLeft();
			if (left.equals(parent) 
					&& dependencyIds.contains(right) 
					&& !result.contains(right)) {
				result.add(right);
			}
		}
		return result;
	}

	public String findTreeRoot(List<String> dependencyIds, List<Pair<String>> pairs) {
		for (String id : dependencyIds) {
			for (Pair<String> pair : pairs) {
				if (pair.getRight().equals(id)) {
					continue;
				}
			}
			return id;
		}
		return null;
	}

//	private List<String> collectAllDependency(List<OperationAndDependency> ops) {
//		List<String> result = new ArrayList<String>();
//		for (OperationAndDependency op : ops) {
//			String analysisId = op.getAnalysisId();
//			if (!result.contains(analysisId)) {
//				result.add(analysisId);
//			}
//			for (String dependencyId : op.getAllDependencies()) {
//				if (!result.contains(dependencyId)) {
//					result.add(dependencyId);
//				}
//			}
//		}
//		return result;
//	}

	/**
	 * remove the duplicate analysis
	 */
//	private void removeDuplicateOp(List<OperationAndDependency> ops) {
//		for (int i = 0; i < (ops.size() - 1); i++) {
//			for (int j = (i + 1); j < ops.size(); j++) {
//				OperationAndDependency op1 = ops.get(i);
//				OperationAndDependency op2 = ops.get(j);
//				if (op1.isDuplicated() && op2.isDuplicated()) {
//					continue;
//				}
//				if (op2.getAllDependencies().contains(op1.getAnalysisId())) {
//					op1.setDuplicated(true);
//				}
//				if (op1.getAllDependencies().contains(op2.getAnalysisId())) {
//					op2.setDuplicated(true);
//				}
//			}
//		}
//		Iterator<OperationAndDependency> iterator = ops.iterator();
//		while (iterator.hasNext()) {
//			OperationAndDependency next = iterator.next();
//			if (next.isDuplicated()) {
//				iterator.remove();
//			}
//		}
//	}

	/**
	 * add current analysis to the job(operations) queue
	 * @return true
	 * 			current analysis is added.
	 * @return false
	 * 			all the dependencies of current analysis are 
	 * 			contained in previous operations(or the result is generated),
	 * 			so it is unnecessary to be added.
	 */
	//	private boolean needAddToQueue(String projectId, AnalysisType type) {
	//		OperationAndDependency operationInfo = getOperationInfo(type, projectId, userUtil.getCurrentUserId());
	//		List<String> allDependencyIds = operationInfo.getAllDependencies();
	//		for (String dependencyId : allDependencyIds) {
	//			if (!containsInPreQueueOrAnalyzed(projectId, dependencyId)) {
	//				this.operations.add(operationInfo);
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	/**
	 * if current dependency analysis contained in previous 
	 * position of the queue(or the result is already generated)
	 */
	//	private boolean containsInPreQueueOrAnalyzed(String projectId, String dependencyId) {
	//		// query in queue
	//		for (OperationAndDependency operation : operations) {
	//			List<String> allDependencyIds = operation.getAllDependencies();
	//			if (allDependencyIds.contains(dependencyId)) {
	//				return true;
	//			}
	//		}
	//		// query in mysql
	//		if (isAnalysedForCurrentCodeVersion(projectId, dependencyId)) {
	//			return true;
	//		}
	//		return false;
	//	}

	/**
	 * if the report(result) of current analysis 
	 * for current code version has been generated.
	 */
	//	private boolean isAnalysedForCurrentCodeVersion(String projectId, String analysisId) {
	//		List<JobStatus> finds = jobStatusRepository.findAllByProjectIdAndAnalysisTypeId(projectId, analysisId);
	//		for (JobStatus find : finds) {
	//			if (JobProcessStatus.S.equals(find.getStatus())) {
	//				String codeVersion = find.getCodeVersion().toString();
	//				String currentVersion = codeVersionUtil.getCodeVerionInFs(projectId);
	//				if (codeVersion.equals(currentVersion)) {
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}

	/**
	 * get the dependency info of current analysis
	 * @param finishedJobName 
	 */
//	private OperationAndDependency getOperationInfo(AnalysisType type, String projectId, String userId) {
//		String analysisId = analysisTypeRepository.findIdByName(type.toString());
//		OperationAndDependency jobDependency = new OperationAndDependency(analysisId, projectId, userId);
//		fillDependency(jobDependency, analysisId);
//		return jobDependency;
//	}

//	private void fillDependency(OperationAndDependency jobDependency, String analysisId) {
//		List<String> relieredIds = analysisDependencyRepository.findRelieredIdByRelierId(analysisId);
//		if (relieredIds != null && relieredIds.size() > 0) {
//			jobDependency.offerLevel();
//			jobDependency.addDependencyIds(relieredIds);
//			for (String relieredId : relieredIds) {
//				fillDependency(jobDependency, relieredId);
//			}
//		}
//	}

//	private String getKeyByValue(Map<String, String> map, String value) {
//		for (String key : map.keySet()) {
//			if (map.get(key).equals(value)) {
//				return key;
//			}
//		}
//		return null;
//	}

//	private String extractProjectId(String lastFinishedJobName) {
//		return lastFinishedJobName.substring(0, lastFinishedJobName.indexOf("_"));
//	}
	
	public synchronized boolean findAndRunJobInWaitingQueue(String jobName, String precursorJobName) {
		for (JobInfo info : waitingJobs) {
			if (info.getJobName().equals(jobName)) {
				JobDetail job = null;
				String jobGroup = info.getJobGroup();
				JobKey jobKey = new JobKey(jobName, jobGroup);
				String projectId = info.getProjectId();
				String projectPath = projectRepository.findOne(projectId).getPath();
				String codeVersion = info.getCodeVersion();
				switch (info.getAnalysisName()) {
				case "SO":					
					// so analysis need parameters : projectPath, pool, codeVersion
					job = JobBuilder.newJob(SoJob.class).withIdentity(jobKey)
							.usingJobData("precursorJobName", precursorJobName)
							.usingJobData("projectId", projectId)
							.usingJobData("projectPath", projectPath)
							.usingJobData("codeVersion", codeVersion)
							.usingJobData("isIncremental", info.isIncremental() ? "y" : "n")
							.usingJobData("incrementBaseVersion", info.getIncrementBaseVersion())
							.build();
					break;
				case "CLONE_CODE":
					int boltPort = portUtil.getBoltPort(projectPath);
					// clonecode analysis need parameters : projectPath, bortPort, codeVersion
					job = JobBuilder.newJob(CloneCodeJob.class)
							.withIdentity(jobKey)
							.usingJobData("precursorJobName", precursorJobName)
							.usingJobData("projectId", projectId)
							.usingJobData("projectPath", projectPath)
							.usingJobData("boltPort", String.valueOf(boltPort))
							.usingJobData("codeVersion", codeVersion)
							.build();
					CloneCodeJob.webSocket = this.webSocket;
					break;
				case "COMPLEXITY":
					int boltPort2 = portUtil.getBoltPort(projectPath);
					String uri = "bolt://localhost:" + boltPort2;
					job = JobBuilder.newJob(ComplexityJob.class)
							.withIdentity(jobKey)
							.usingJobData("projectId", projectId)
							.usingJobData("precursorJobName", precursorJobName)
							.usingJobData("boltUrl",uri)
							.build();
					ComplexityJob.webSocket = this.webSocket;
					break;
				case "DATA_MAPPING":
					job = JobBuilder.newJob(DataMappingJob.class)
							.withIdentity(jobName, jobGroup)
							.usingJobData("precursorJobName", precursorJobName)
							.build();
					break;
				case "DEAD_CODE":
					job = JobBuilder.newJob(DeadCodeJob.class)
							.withIdentity(jobKey)
							.usingJobData("precursorJobName", precursorJobName)
							.usingJobData("projectPath", projectPath)
							.usingJobData("codeVersion", codeVersion)
							.build();
					break;
				case "DEPENDENCY":
					job = JobBuilder.newJob(DependencyJob.class)
							.withIdentity(jobKey)
							.usingJobData("precursorJobName", precursorJobName)
							.build();
					break;
				case "COST_ESTIMATION":
					job = JobBuilder.newJob(CostEstimation.class)
							.withIdentity(jobKey)
							.usingJobData("precursorJobName", precursorJobName)
							.build();
					break;
				case "CLUSTERING":
					job = JobBuilder.newJob(ClusteringJob.class)
							.withIdentity(jobKey)
							.usingJobData("precursorJobName", precursorJobName)
							.build();
					break;
				case "DATA_DEPENDENCY":
					job = JobBuilder.newJob(DataDependencyJob.class)
							.withIdentity(jobKey)
							.usingJobData("precursorJobName", precursorJobName)
							.build();
				case "SYSTEM_DOCUMENTATION":
					job = JobBuilder.newJob(SystemDocumentationJob.class)
							.withIdentity(jobKey)
							.usingJobData("projectId", projectId)
							.usingJobData("precursorJobName", precursorJobName)
							.build();
					break;
				}
				if (!jobHasMultiParentsAndAdded(info)) {
					try {
						Trigger trigger = TriggerBuilder
								.newTrigger().withIdentity(jobGroup)
								.startAt(DateBuilder.futureDate(1,IntervalUnit.YEAR)).build();
						this.scheduler.scheduleJob(job, trigger);
						this.scheduler.triggerJob(jobKey);
						this.scheduler.unscheduleJob(trigger.getKey());
						return true;
					} catch (Exception e) {
						LOGGER.error("job(" + jobName + ") run error.", e);
						this.jobTreeMap.remove(jobGroup);
						clearCurrentGroupInQueue(jobGroup);
						// change status in db
						JobStatus find = jobStatusRepository.findbyName(jobName);
						if (find != null) {
							find.setStatus(JobProcessStatus.F.toString());
							jobStatusRepository.save(find);
						}
						return false;
					}
				}
			}
		}
		return false;
	}

	/**
	 * if current so is incremental analysis
	 */
	private JobStatus getLastAnalysisStatusForProject(String projectId, String analysisTypeId) {
//		boolean isIncremental = false;
		List<JobStatus> finds = jobStatusRepository.findAllByProjectIdAndAnalysisTypeId(projectId, analysisTypeId);
		JobStatus latest = null;
		for (JobStatus find : finds) {
			if (find.getStatus().equals(JobProcessStatus.S.toString())) {
				latest = find;
			}
		}
//		if (latestSuccessSo != null && !latestSuccessSo.getCodeVersion().equals(codeVersion)) {
//			isIncremental = true;
//		}
//		return isIncremental;
		return latest;
	}
	
	/**
	 * check if current job has multiple parents,
	 * avoid repeatedly add job to scheduler.
	 * @param info 
	 */
	private boolean jobHasMultiParentsAndAdded(JobInfo info) {
		boolean hasMultiParents = false;
		boolean added = false;
		String jobGroup = info.getJobGroup();
		String jobName = info.getJobName();
		String analysisTypeId = info.getAnalysisTypeId();
		JobTree<String> jobTree = this.jobTreeMap.get(jobGroup);
		for (Node<String> node : jobTree.getNodes()) {
			if (analysisTypeId.equals(node.getData())
					&& node.getParents().size() > 1) {
				hasMultiParents = true;
				break;
			}
		}
		
		JobKey jobKey = new JobKey(jobName, jobGroup);
		try {
			JobDetail jobDetail = this.scheduler.getJobDetail(jobKey);
			if (jobDetail != null) {
				added = true;
			}
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
		return hasMultiParents && added;
	}

	private void prepareSchedulerAndTrigger(String jobGroup) {
		if (this.scheduler == null) {
			try {
				this.scheduler = schedulerFactoryBean.getScheduler();
				this.scheduler.getListenerManager().addJobListener(bwbJobListener, allJobs());
				this.scheduler.start();
			} catch (SchedulerException e) {
	            LOGGER.error(e);
			}
		}
	}

	/**
	 * if all jobs in current tree has been processed.
	 * (except notStart/Processing status, all jobs regard as processed.)
	 */
	public synchronized void allJobsInCurrentTreeProcessed(String processedJobName) {
		JobInfo info = jobNameUtil.parseJobInfoFromJobName(processedJobName);
		String jobGroup = info.getJobGroup();
		boolean currentTreeAllDone = true;
		for (JobInfo ele : waitingJobs) {
			if (ele.getJobGroup().equals(jobGroup)) {
				currentTreeAllDone = false;
			}
		}
		if (currentTreeAllDone) {
			for (JobInfo ele : executingJobs) {
				if (ele.getJobGroup().equals(jobGroup)) {
					currentTreeAllDone = false;
				}
			}
		}
		if (currentTreeAllDone) {
			JobTree<String> jobTree = this.jobTreeMap.get(jobGroup);
			if (jobTree != null && !jobTree.isAllDone()) {
				jobTree.setAllDone(true);
				LOGGER.info("one group of analysis job(" + jobGroup + ") all porcessed!!!!!!");
				
				// send websocket message to change the "missing or update" status.
				String message = "ALL_DONE/" + info.getProjectId() + "/" + info.getUserId();
				try {
					webSocket.sendMessageTo(message, info.getProjectId());
				} catch (IOException e) {
		            LOGGER.error(e);
				}
				
				this.jobTreeMap.remove(jobGroup);
				clearCurrentGroupInQueue(jobGroup);
			}
		}
	}
	
	private synchronized void clearCurrentGroupInQueue(String jobGroup) {
		// clear waiting queue
		Iterator<JobInfo> waitingIterator = waitingJobs.iterator();
		while (waitingIterator.hasNext()) {
			JobInfo next = waitingIterator.next();
			if (next.getJobGroup().equals(jobGroup) || next == null) {
				waitingIterator.remove();
			}
		}
		// clear executing queue
		Iterator<JobInfo> executingIterator = executingJobs.iterator();
		while (executingIterator.hasNext()) {
			JobInfo next = executingIterator.next();
			if (next.getJobGroup().equals(jobGroup) || next == null) {
				executingIterator.remove();
			}
		}
	}
	
	private Node<String> getTreeNodeByValue(JobTree<String> jobTree, String value) {
		for (Node<String> node : jobTree.getNodes()) {
			if (node.getData().equals(value)) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * get interrupted jobs(interrupted by its dependent job)
	 * from queue, and change its status in db.
	 */
	public synchronized List<String> getInterruptedJobNames(JobInfo info) {
		List<String> handledJobNames = new ArrayList<String>();
		List<String> interruptedAnalysisIds = getInterruptedAnalysisIds(info);
		for (JobInfo waiting : this.waitingJobs) {
			if (waiting.getProjectId().equals(info.getProjectId())
					&& waiting.getCodeVersion().equals(info.getCodeVersion())
					&& interruptedAnalysisIds.contains(waiting.getAnalysisTypeId())) {
				String interruptedJobName = waiting.getJobName();
//				// remove from waiting queue
//				removeFromWaiting(interruptedJobName);
//				// change job status in db
//				JobStatus find = jobStatusRepository.findbyName(interruptedJobName);
//				if (find != null) {
//					find.setStopTime(new Date());
//					find.setStatus(JobProcessStatus.I.toString());
//					find.setDescription("Interrupted by job : (" + info.getJobName() + ")");
//					jobStatusRepository.save(find);
//				}
//				handledJobNames.add(interruptedJobName);
				
				handledJobNames.add(interruptedJobName);
			}
		}
		return handledJobNames;
	}
	
	public synchronized boolean checkProjectHasJob(String projectId) {
	    for (JobInfo jobInfo : waitingJobs) {
            if(jobInfo.getProjectId().equals(projectId))
                return true;
        }
        for (JobInfo jobInfo : executingJobs) {
            if(jobInfo.getProjectId().equals(projectId))
                return true;
        }
        return false;
    }

	private List<String> getInterruptedAnalysisIds(JobInfo info) {
		List<String> interruptedAnalysisIds = new ArrayList<String>();
		String analysisId = info.getAnalysisTypeId();
		for (String key : this.jobTreeMap.keySet()) {
			JobTree<String> treeInMap = this.jobTreeMap.get(key);
			if (info.getProjectId().equals(treeInMap.getProjectId()) 
					&& info.getCodeVersion().equals(treeInMap.getCodeVersion())
					&& treeInMap.getNodeValues().contains(analysisId)) {
				for (Node<String> node : treeInMap.getNodes()) {
					if (node.getData().equals(analysisId)) {
						List<Node<String>> children = treeInMap.anyLevelChildren(node);
						for (Node<String> child : children) {
							interruptedAnalysisIds.add(child.getData());
						}
					}
				}
			}
		}
		return interruptedAnalysisIds;
	}
	
	/**
	 * due to the jobs can not get the instance from
	 * IOC container(quartz create the job instance
	 * by Java reflection), so the ProcessBuilderUtil
	 * used in job definition also can not be registered
	 * in IOC container, and the websocket instance
	 * used in ProcessBuilderUtil class should defined
	 * as static and get the instance here.
	 */
	private void setWebSocketInProcessBuilderUtil() {
		if (ProcessBuilderUtil.progressBarWebSocket == null) {
			ProcessBuilderUtil.progressBarWebSocket = this.webSocket;
		}
	}
	
}
