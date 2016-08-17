package com.sommer.jenkins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobReconfig {

	public static final String DISABLE_BUILD = "(?<=<disabled>).*(?=</disabled>)";
	public static final String DESCRIPTION = "<description>。*</description>|<description.*/>";
	public static final String SCM_REPO_URL = "<remote>.*</remote>";
	public static final String SCM_REPO_DEPTH = "<depthOption>).*</depthOption>";
	public static final String TRIGGER = "<triggers>。*</triggers>|<triggers.*/>";
	public static final String BUILD_GOAL = "<goals>.*</goals>|<goals.*/>";
	public static final String EMAIL = "<recipientList>.*</recipientList>|<recipientList.*/>";

	public static String getConfigXML(String original, ConfigEntity configEntity) {

		String newConf = original;

		if (configEntity.getDescription() != null) {
			StringBuilder sb = new StringBuilder("<description>");
			sb.append(configEntity.getDescription());
			sb.append("</description>");
			newConf = replaceElement(DESCRIPTION, newConf, sb.toString());
		}
		if (configEntity.getRepositoryURL() != null) {
			StringBuilder sb = new StringBuilder("<remote>");
			sb.append(configEntity.getDescription());
			sb.append("</remote>");
			newConf = replaceElement(SCM_REPO_URL, newConf, sb.toString());
		}
		if (configEntity.getBulidGoals() != null) {
			StringBuilder sb = new StringBuilder("<goals>");
			sb.append(configEntity.getDescription());
			sb.append("</goals>");
			newConf = replaceElement(BUILD_GOAL, newConf, sb.toString());
		}
		
		if (configEntity.getEmail()!=null){
			StringBuilder sb = new StringBuilder("<recipientList>");
			sb.append(configEntity.getDescription());
			sb.append("</recipientList>");
			newConf = replaceElement(EMAIL, newConf, sb.toString());
		} 
		if (configEntity.isDisableBuild()) {
			newConf = replaceElement(DISABLE_BUILD, newConf, "true");
		} else {
			newConf = replaceElement(DISABLE_BUILD, newConf, "false");
		}
		if (configEntity.isBuildPeriodically() && configEntity.getCronTime() != null) {
			StringBuilder sb = new StringBuilder("<triggers>\n");
			sb.append("<hudson.triggers.TimerTrigger>\n");
			sb.append("<spec>");
			sb.append("configEntity.getCronTime()");
			sb.append("</spec>\n");
			sb.append(configEntity.getDescription());
			sb.append("</hudson.triggers.TimerTrigger>\n");
			sb.append("</triggers>");
			newConf = replaceElement(TRIGGER, newConf, sb.toString());
		} 
		if (configEntity.isPollSCM() && configEntity.getCronTime() != null) {
			StringBuilder sb = new StringBuilder("<triggers>\n");
			sb.append("<hudson.triggers.SCMTrigger>\n");
			sb.append("<spec>");
			sb.append("configEntity.getCronTime()");
			sb.append("</spec>\n");
			sb.append(configEntity.getDescription());
			sb.append("</hudson.triggers.SCMTrigger>\n");
			sb.append("</triggers>");
			newConf = replaceElement(TRIGGER, newConf, sb.toString());
		} 
		return newConf;
	}

	public static String replaceElement(String pattern, String fullText, String subContent) {
		Pattern p = Pattern.compile(pattern,Pattern.MULTILINE);
		Matcher m = p.matcher(fullText);
		return m.replaceFirst(subContent);
	}
}
