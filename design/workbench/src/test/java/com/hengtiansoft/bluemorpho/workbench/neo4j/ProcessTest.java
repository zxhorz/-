package com.hengtiansoft.bluemorpho.workbench.neo4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;

public class ProcessTest {
	private static final Logger logger = LogManager.getLogger(ProcessTest.class);
	private static final String MAVEN_RESOLVE = "cmd /c mvn dependency:resolve";
	private static final String SETTING_OPTION = "--settings";
	
	@Test
	public void mavenTest() {
			List<String> cmd = new ArrayList<>();
			cmd.add("cmd");
			cmd.add("/c");
			cmd.add("mvn");
			cmd.add("dependency:resolve");
			cmd.add("--settings");
			cmd.add("C:\\Users\\xqtang\\.m2\\hcd-settings.xml");
			ProcessBuilder processBuilder = new ProcessBuilder(cmd);
			processBuilder.directory(new File("F:\\workspace_bwb\\workbench"));
			Process process = null;
			try {
				process = processBuilder.start();
			} catch (IOException e1) {
				logger.error(e1);
			}
			// 获取进程的标准输入流
			final InputStream is1 = process.getInputStream();
			// 获取进城的错误流
			final InputStream is2 = process.getErrorStream();
			// 启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
			final String[] inputObjs = new String[1];
			final String[] errorObjs = new String[1];
			final StringBuffer inputBuffer = new StringBuffer();
			final StringBuffer errorBuffer = new StringBuffer();
			new Thread() {

				public void run() {
					BufferedReader br1 = new BufferedReader(new InputStreamReader(
							is1));
					try {
						boolean flag = false;
						String line1 = null;
						while ((line1 = br1.readLine()) != null) {
							inputBuffer.append(line1);
//							logger.info(line1);
							if(!flag) {
								if(line1.indexOf("The following files have been resolved:")>-1) {
									flag =true;
								}
							}else {
								String[] attrs = line1.replace("[INFO]", "").trim().split(":");
								if(attrs.length==5) {
									System.out.print(attrs[0]);
									System.out.print(attrs[1]);
									System.out.print(attrs[2]);
									System.out.print(attrs[3]);
									System.out.println(attrs[4]);
								}
							}
						}
					} catch (IOException e) {
						logger.error(e);
					} finally {
						try {
							is1.close();
						} catch (IOException e) {
							logger.error(e);
						}
					}
					inputObjs[0] = inputBuffer.toString();
				}
			}.start();

			new Thread() {
				public void run() {
					BufferedReader br2 = new BufferedReader(new InputStreamReader(
							is2));
					try {
						String line2 = null;
						while ((line2 = br2.readLine()) != null) {
							errorBuffer.append(line2);
							logger.info(line2);
						}
					} catch (IOException e) {
						logger.error(e);
					} finally {
						try {
							is2.close();
						} catch (IOException e) {
							logger.error(e);
						}
					}
					errorObjs[0] = errorBuffer.toString();
				}
			}.start();
			try {
				process.waitFor();
				int code = process.exitValue();
				if (code == 0 && StringUtils.isBlank(errorObjs[0])
						&& !StringUtils.isBlank(inputObjs[0])) {
					// 返回tier，数字
//					System.out.println(inputBuffer.toString());
				} else {
					System.out.println("");
				}
			} catch (InterruptedException e) {
				System.out.println("");
		}
//		
//        MavenXpp3Reader reader = new MavenXpp3Reader();
//        Model model = reader.read(new FileReader("F:\\workspace_bwb\\workbench\\pom.xml"));
//        System.out.println(model.toString());
//        List<Dependency> dependencies = model.getDependencies();
//        for(Dependency d: dependencies) {
//        	System.out.print(d.getGroupId());
//        	System.out.print(d.getArtifactId());
//        	System.out.println(d.getVersion());
//        }
//        
//        ModelBuildingRequest req = new DefaultModelBuildingRequest();
//        req.setProcessPlugins( false );
//        req.setPomFile(new File("F:\\workspace_bwb\\workbench\\pom.xml"));
////        req.setModelResolver( new ModelResolver() );
//        req.setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL );
	}
	
	@Test
	public void checkCodeTest(){	
		for(int i = 5; i>0;i--){
			printVserion();
		}
	}
	
	public void printVserion(){
		String version = FileStatusUtil.checkCode("F:/workspace_bwb/workbench/project/Project_T");
		System.out.println(version);
	}
	@Test
	public void pathTest(){
//		String path = ClassName.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String path1= this.getClass().getClassLoader().getResource("sysconfig").getPath();	
//		System.out.println(path);
		System.out.println(path1);
	}
	@Test
	public void processTest(){
		List<String> cmd = new ArrayList<>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add("F:\\workspace_bwb\\workbench\\tools\\OntologyExportor-0.0.1-SNAPSHOT.jar");
		cmd.add("F:\\workspace_bwb\\workbench\\project\\Project-F\\config\\so\\arguments.properties");
		String sonarCheckDir = "F:\\workspace_bwb\\workbench\\tools";
		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.directory(new File(sonarCheckDir));
		Process process = null;
		try {
			process = processBuilder.start();
		} catch (IOException e1) {
			logger.error(e1);
		}
		// 获取进程的标准输入流
		final InputStream is1 = process.getInputStream();
		// 获取进城的错误流
		final InputStream is2 = process.getErrorStream();
		// 启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
//		final OutputStream is3 = process.getOutputStream();
		new Thread() {

			public void run() {
				BufferedReader br1 = new BufferedReader(new InputStreamReader(
						is1));
				try {
					String line1 = null;
					while ((line1 = br1.readLine()) != null) {
						if (line1.contains(".realMain")){
							logger.info("input log: " + line1);
						}
					}
				} catch (IOException e) {
					logger.error(e);
				} finally {
					try {
						is1.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}.start();

		new Thread() {

			public void run() {
				BufferedReader br2 = new BufferedReader(new InputStreamReader(
						is2));
				try {
					String line2 = null;
					while ((line2 = br2.readLine()) != null) {
						logger.info("error log: " + line2);
					}
				} catch (IOException e) {
					logger.error(e);
				} finally {
					try {
						is2.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}.start();
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		logger.info("test end : " + process.exitValue());
	}
	}
