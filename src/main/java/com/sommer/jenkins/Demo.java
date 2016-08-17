package com.sommer.jenkins;

import com.sommer.util.Common;

public class Demo {
	public static void main(String[] args) {
		JenkinsServer server = new JenkinsServer("http://localhost:8080/", "sommer", "sommer");
//		String res = server.buildJob("mavenproject");
//		String res = server.getJobInfo("mavenproject", 0);
//		String res = server.renameJob("mavenproject", "mavenproject1");
//		Boolean b = server.jobExits("mavenproject1");
//		String res = server.disableJob("mavenproject");
		String res = server.consoleOutput("mavenproject", 48);
		System.out.println(res);
//		String configXML = Common.readConfigFile("config/config_xml");
//		server.createJob("new817", configXML);
		
//		String configXML = server.getJobConf("mavenproject");
//		ConfigEntity configEntity = new ConfigEntity();
//		configEntity.setDisableBuild(true);
//		configEntity.setBulidGoals("clean install");
//		configEntity.setCronTime("H 04 * * 1-5");
//		configEntity.setPollSCM(true);
//		configEntity.setEmail("xxx@example.com");
//		String newConfigXML = JobReconfig.getConfigXML(configXML, configEntity);
//		server.reconfigJob("webProject", newConfigXML);
		
	}
}
