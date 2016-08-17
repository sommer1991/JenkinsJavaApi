package com.sommer.jenkins;

public class ConfigEntity {
	private String projectName;
	private String description;
	private String cronTime;
	private String repositoryURL; //svn project
	private String bulidGoals;
	private String postBuildCommand;
	private String email;
	
	private boolean isBuildPeriodically;
	private boolean isPollSCM;
	private boolean isDisableBuild;
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isPollSCM() {
		return isPollSCM;
	}
	public void setPollSCM(boolean isPollSCM) {
		this.isPollSCM = isPollSCM;
	}
	public String getCronTime() {
		return cronTime;
	}
	public void setCronTime(String cronTime) {
		this.cronTime = cronTime;
	}
	public String getRepositoryURL() {
		return repositoryURL;
	}
	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}
	public String getBulidGoals() {
		return bulidGoals;
	}
	public void setBulidGoals(String bulidGoals) {
		this.bulidGoals = bulidGoals;
	}
	public String getPostBuildCommand() {
		return postBuildCommand;
	}
	public void setPostBuildCommand(String postBuildCommand) {
		this.postBuildCommand = postBuildCommand;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isBuildPeriodically() {
		return isBuildPeriodically;
	}
	public void setBuildPeriodically(boolean isBuildPeriodically) {
		this.isBuildPeriodically = isBuildPeriodically;
	}

	public boolean isDisableBuild() {
		return isDisableBuild;
	}
	public void setDisableBuild(boolean isDisableBuild) {
		this.isDisableBuild = isDisableBuild;
	}
	

}
