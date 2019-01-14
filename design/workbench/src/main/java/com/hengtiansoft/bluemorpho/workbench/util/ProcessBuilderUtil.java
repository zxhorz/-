package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hengtiansoft.bluemorpho.workbench.websocket.CustomScriptWebSocket;
import com.hengtiansoft.bluemorpho.workbench.websocket.ProgressBarWebSocket;

public class ProcessBuilderUtil {
	
	private static final Logger logger = Logger.getLogger(ProcessBuilderUtil.class);
	public static ProgressBarWebSocket progressBarWebSocket = null;
	public static CustomScriptWebSocket customScriptWebSocket = null;
	public static final String LINE_SEP = System.getProperty("line.separator");
	public static final String CONSOLELOG = "/console.log";
	
	public static int processBuilder(List<String> cmd, String workPath, final String jobName, final String projectId){

		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.directory(new File(workPath));
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
						// 约定各工具log输出进度信息格式：
						// progress-bar-info : 70
						if (line1.contains("progress-bar-info")) {
							String percentValue = line1.substring(line1.lastIndexOf(":") + 1).trim();
							if (progressBarWebSocket == null) {
								logger.error("webSocket instance null");
							} else {
								progressBarWebSocket.sendMessageTo(jobName + "/" + percentValue, projectId);
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
			int code = process.exitValue();
			return  code;
		} catch (InterruptedException e) {
			logger.error(e);
			return process.exitValue();
		}
	}
	
	public static int processBuilder(List<String> cmd, String workPath, final String logPath, final boolean isJar){

		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.directory(new File(workPath));
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

		new Thread() {
			public void run() {
				BufferedReader br1 = new BufferedReader(new InputStreamReader(
						is1));
				Date start1 = new Date();
				long last1 = 0L;
				StringBuffer buffer1 = new StringBuffer();
				try {
					String line1 = null;
					while ((line1 = br1.readLine()) != null) {
						if (isJar) {
							// jar
							if (line1.contains(".realMain")) {
								buffer1.append(line1).append("\n");
								logger.info("input log: " + line1);
							}
						} else {
							// python
							buffer1.append(line1).append("\n");
							logger.info("input log: " + line1);
						}
						
						Date now1 = new Date();
						long minutes = Math
								.abs(start1.getTime() - now1.getTime()) / (1000);
						if ((minutes - last1) > 5) {
							// 每隔5秒钟，将buffer追加至log
							FilePathUtil.writeFile(logPath, buffer1.toString(), true);
							buffer1 = new StringBuffer();
							last1 = minutes;
						}
					}
					FilePathUtil.writeFile(logPath, buffer1.toString(), true);
					buffer1 = new StringBuffer();
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
				Date start2 = new Date();
				long last2 = 0L;
				StringBuffer buffer2 = new StringBuffer();
				try {
					String line2 = null;
					while ((line2 = br2.readLine()) != null) {
						buffer2.append(line2).append("\n");
						logger.info("error log: " + line2);
						Date now2 = new Date();
						long minutes = Math.abs(start2.getTime()
								- now2.getTime()) / (1000);
						if ((minutes - last2) > 10) {
							// 每隔10秒钟，将buffer追加至log
							FilePathUtil.writeFile(logPath, buffer2.toString(), true);
							buffer2 = new StringBuffer();
							last2 = minutes;
						}
					}
					FilePathUtil.writeFile(logPath, buffer2.toString(), true);
					buffer2 = new StringBuffer();
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
			int code = process.exitValue();
			return  code;
		} catch (InterruptedException e) {
			logger.error(e);
			return process.exitValue();
		}
	}

	public static int processBuilder(List<String> cmd, String workPath, 
			final String logPath, final boolean isJar, final String jobName, final String projectId) {

		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.directory(new File(workPath));
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

		new Thread() {
			public void run() {
				BufferedReader br1 = new BufferedReader(new InputStreamReader(
						is1));
				Date start1 = new Date();
				long last1 = 0L;
				StringBuffer buffer1 = new StringBuffer();
				try {
					String line1 = null;
					while ((line1 = br1.readLine()) != null) {
						if (isJar) {
							// jar
//							if (line1.contains(".realMain")) {
								buffer1.append(line1).append("\n");
//								logger.info("input log: " + line1);
//							}
							logger.info("input log: " + line1);
						} else {
							// python
							buffer1.append(line1).append("\n");
							logger.info("input log: " + line1);
						}
						
						// 约定各工具log输出进度信息格式：
						// progress-bar-info : 70.00
						if (line1.contains("progress-bar-info")) {
							String percentValue = line1.substring(line1.lastIndexOf(":") + 1).trim();
							if (progressBarWebSocket == null) {
								logger.error("webSocket instance null");
							} else {
								progressBarWebSocket.sendMessageTo(jobName + "/" + percentValue, projectId);
							}
						}
						
						Date now1 = new Date();
						long minutes = Math
								.abs(start1.getTime() - now1.getTime()) / (1000);
						if ((minutes - last1) > 5) {
							// 每隔5秒钟，将buffer追加至log
							FilePathUtil.writeFile(logPath, buffer1.toString(), true);
							buffer1 = new StringBuffer();
							last1 = minutes;
						}
					}
					FilePathUtil.writeFile(logPath, buffer1.toString(), true);
					buffer1 = new StringBuffer();
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
				Date start2 = new Date();
				long last2 = 0L;
				StringBuffer buffer2 = new StringBuffer();
				try {
					String line2 = null;
					while ((line2 = br2.readLine()) != null) {
						buffer2.append(line2).append("\n");
						logger.info("error log: " + line2);
						Date now2 = new Date();
						long minutes = Math.abs(start2.getTime()
								- now2.getTime()) / (1000);
						if ((minutes - last2) > 10) {
							// 每隔10秒钟，将buffer追加至log
							FilePathUtil.writeFile(logPath, buffer2.toString(), true);
							buffer2 = new StringBuffer();
							last2 = minutes;
						}
					}
					FilePathUtil.writeFile(logPath, buffer2.toString(), true);
					buffer2 = new StringBuffer();
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
			int code = process.exitValue();
			return  code;
		} catch (InterruptedException e) {
			logger.error(e);
			return process.exitValue();
		}
	}

	public static int processBuilderForCloneDiff(List<String> cmd, String workPath){

		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.directory(new File(workPath));
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

		new Thread() {

			public void run() {
				final StringBuffer inputBuffer = new StringBuffer();
				BufferedReader br1 = new BufferedReader(new InputStreamReader(
						is1));
				try {
					String line1 = null;
					while ((line1 = br1.readLine()) != null) {
						inputBuffer.append(line1);
						logger.info(line1);
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
				final StringBuffer errorBuffer = new StringBuffer();
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
				return Integer.valueOf(inputObjs[0]);
			} else {
				return 1000;
			}
		} catch (InterruptedException e) {
			logger.error(e);
			return process.exitValue();
		}
	}

	public static void startPyServer(List<String> cmd) {
		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
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

		new Thread() {
			public void run() {
				final StringBuffer inputBuffer = new StringBuffer();
				BufferedReader br1 = new BufferedReader(new InputStreamReader(
						is1));
				try {
					String line1 = null;
					while ((line1 = br1.readLine()) != null) {
						inputBuffer.append(line1);
						logger.info(line1);
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
				final StringBuffer errorBuffer = new StringBuffer();
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
//		try {
//			process.waitFor();
//			int code = process.exitValue();
////			return code;
//		} catch (InterruptedException e) {
//			logger.error(e);
////			return process.exitValue();
//		}
	}

	public static String processBuilderForParserTool(List<String> cmd, String workPath){

		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.directory(new File(workPath));
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
					String line1 = null;
					while ((line1 = br1.readLine()) != null) {
						inputBuffer.append(line1);
						logger.info(line1);
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
				return inputBuffer.toString();
			} else {
				return "";
			}
		} catch (InterruptedException e) {
			logger.error(e);
			return "";
		}
	}
	
	public static int processBuilderForCustomScript(List<String> cmd,  Map<String, String> envMap, String scriptName, String scriptDir, String logDir, final String runId,String projectPath){
		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		
		File relDir = new File(scriptDir + "/" + scriptName);
		if (!relDir.exists()) {
			relDir.mkdirs();
		}
		String absDir = StringUtils.replace(relDir.getAbsolutePath(), "\\", "/");
		processBuilder.directory(new File(absDir));
		
		Map<String, String> env = processBuilder.environment();  
		for (String key : envMap.keySet()) {
			String value = envMap.get(key);
			env.put(key, value);
			logger.info("Set env var : " + key + " - " + value);
		}

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
				int logLineId = 0;
				BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
				try {
					String line1 = null;
					while ((line1 = br1.readLine()) != null) {
						inputBuffer.append(line1);
						inputBuffer.append(LINE_SEP);
						logger.info(line1);
						if (customScriptWebSocket == null) {
							logger.error("webSocket instance null");
						} else {
							customScriptWebSocket.sendMessageTo(line1+"\n", runId);
							logLineId++;
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
				int logLineId = 0;
				BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
				try {
					String line2 = null;
					while ((line2 = br2.readLine()) != null) {
						errorBuffer.append(line2);
						errorBuffer.append(LINE_SEP);
						logger.info(line2);
						if (customScriptWebSocket == null) {
							logger.error("webSocket instance null");
						} else {
							customScriptWebSocket.sendMessageTo(line2+"\n", runId);
							logLineId++;
						}
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
			// write log file
			String logContent = inputObjs[0] + errorObjs[0];
			File dir = new File(logDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileWriter fw = null;
			BufferedWriter bw = null;
			try {
				fw = new FileWriter(new File(logDir + CONSOLELOG));
				bw = new BufferedWriter(fw);
				bw.write(logContent);
				bw.flush();
//				if(errorObjs[0] == null || errorObjs[0].equals("")){
//				    compressThread(runId, projectPath);    
//				}
			} catch (IOException e) {
				logger.error(e);
			} finally {
				try {
					bw.close();
					fw.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			return code;
		} catch (InterruptedException e) {
			logger.error(e);
			return process.exitValue();
		}
	}
	
	public static int processBuilderForSetEnviVars(String cmd) {
		String[] cmdStr = cmd.split("\\s+");
		ProcessBuilder processBuilder = new ProcessBuilder(cmdStr);
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
				BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
				try {
					String line1 = null;
					while ((line1 = br1.readLine()) != null) {
						inputBuffer.append(line1);
						logger.info(line1);
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
				BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
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
			return code;
		} catch (InterruptedException e) {
			logger.error(e);
			return process.exitValue();
		}
	}
	
//	public static void compressThread(String runId,String projectPath){
//	    new Thread() {
//            public void run() {
//                String scriptWorkDir = projectPath.replace("\\", "/") + "/script_workdirs" + "/" + runId;
//                String outputDir = scriptWorkDir + "/output";
//                File output = new File(outputDir);
//                if(!output.exists()){
//                    return;
//                }
//                
//                //压缩
//                File file = new File(scriptWorkDir,"output.zip");
//                try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
//                    CompressUtils.toZip(zos,"",output);
//                    //删除
//                    FileUtils.deleteDirectory(output);
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
////                FilePathUtil.deleteDir(output);
//            }
//        }.start();
//	}
}
