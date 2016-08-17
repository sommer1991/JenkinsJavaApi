# JenkinsJavaApi

Username/password based authentication.<br />
Creating jobs by sending xml file or by specifying ConfigEntity.<br />
Building jobs (with params) querying details of recent builds, obtaining build params, etc.<br />
Listing jobs available in Jenkins with job name filter.<br />
Adding/removing jobs.<br />
Chaining jobs i.e given a list of projects each project is added as a downstream project to the previous one.<br />
Obtaining progressive console output.<br />

More potential functions seeing in com.sommer.jenkins.RequestFormat.java. Developers can custom their own requirements by following functions in com.sommer.jenkins.JenkinsServer.java
