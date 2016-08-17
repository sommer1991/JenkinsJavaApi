package com.sommer.jenkins;

public interface RequestFormat {
	final static String INFO = "api/json";
	final static String PLUGIN_INFO = "pluginManager/api/json?depth=%d";
	final static String CRUMB_URL = "crumbIssuer/api/json";
	final static String JOBS_QUERY = "?tree=jobs[url,color,name,jobs]";
	final static String JOB_INFO = "%sjob/%s/api/json?depth=%d";
	final static String JOB_NAME = "%sjob/%s/api/json?tree=name";
	final static String Q_INFO = "queue/api/json?depth=0";
	final static String CANCEL_QUEUE = "queue/cancelItem?id=%s";
	final static String CREATE_JOB = "%screateItem?name=%s";
	final static String CONFIG_JOB = "%sjob/%s/config.xml";
	final static String DELETE_JOB = "%sjob/%s/doDelete";
	final static String ENABLE_JOB = "%sjob/%s/enable";
	final static String DISABLE_JOB = "%sjob/%s/disable";
	final static String SET_JOB_BUILD_NUMBER = "%sjob/%s/nextbuildnumber/submit";
	final static String COPY_JOB = "%screateItem?name=%s&mode=copy&from=%s";
	final static String RENAME_JOB = "%sjob/%s/doRename?newName=%s";
	final static String BUILD_JOB = "%sjob/%s/build";
	final static String STOP_BUILD = "%sjob/%s/%s/stop";
	final static String BUILD_WITH_PARAMS_JOB = "%sjob/%s/buildWithParameters";
	final static String BUILD_INFO = "%sjob/%s/%d/api/json?depth=%d";
	final static String BUILD_CONSOLE_OUTPUT = "%sjob/%s/%d/consoleText";
	final static String NODE_LIST = "computer/api/json";
	final static String CREATE_NODE = "computer/doCreateItem?%s";
	final static String DELETE_NODE = "computer/%s/doDelete";
	final static String NODE_INFO = "computer/%s/api/json?depth=%d";
	final static String NODE_TYPE = "hudson.slaves.DumbSlave$DescriptorImpl";
	final static String TOGGLE_OFFLINE = "computer/%s/toggleOffline?offlineMessage=%s";
	final static String CONFIG_NODE = "computer/%s/config.xml";
	final static String VIEW_NAME = "view/%s/api/json?tree=name";
	final static String CREATE_VIEW = "createView?name=%s";
	final static String CONFIG_VIEW = "view/%s/config.xml";
	final static String DELETE_VIEW = "view/%s/doDelete";
	final static String SCRIPT_TEXT = "scriptText";
	final static String QUIET_DOWN = "quietDown";
}
