package com.sommer.jenkins;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JenkinsServer {

	URI uri;
	HttpHost host;
	CloseableHttpClient httpClient;
	HttpClientContext localContext;

	static String headerContentType = "text/xml; charset=utf-8";

	/**
	 * Create handle to Jenkins instance.
	 * 
	 * @param url:
	 *            URL of Jenkins server
	 * @param username:
	 *            Server username
	 * @param password:
	 *            Server password
	 */
	public JenkinsServer(String url, String username, String password) {
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		this.uri = URI.create(url);

		this.host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
				new UsernamePasswordCredentials(username, password));
		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);
		this.httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		// Add AuthCache to the execution context
		this.localContext = HttpClientContext.create();
		localContext.setAuthCache(authCache);

	}

	/**
	 * Utility routine for opening an HTTP GET request to a Jenkins server.
	 * 
	 * @param uri
	 * @return Response body
	 * @throws ParseException
	 * @throws IOException
	 * @throws JenkinsException
	 */
	private String jenkinsOpen(URI uri) throws ParseException, IOException, JenkinsException {

		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = null;
		try {
			response = httpClient.execute(host, httpGet, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 304) {
			throw new JenkinsException("Cannot connect to Jenkins Server.");
		}
		return EntityUtils.toString(response.getEntity());
	}

	/**
	 * Utility routine for opening an HTTP POST request to a Jenkins server.
	 * 
	 * @param uri
	 * @param variables[0]:
	 *            request body
	 * @param variables[1]:
	 *            request Content-Type header
	 * @return Response body
	 * @throws ParseException
	 * @throws IOException
	 * @throws JenkinsException
	 */
	private String jenkinsPost(URI uri, String... variables) throws ParseException, IOException, JenkinsException {
		String flag = "success";
		HttpPost httpPost = new HttpPost(uri);
		if (variables.length == 2) {
			String body = variables[0];
			String header = variables[1];
			httpPost.setHeader("Content-Type", header);
			StringEntity entity = new StringEntity(body);
			httpPost.setEntity(entity);
		}
		HttpResponse response = null;
		try {
			response = httpClient.execute(host, httpPost, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		if (response.getStatusLine().getStatusCode() >= 400 && response.getStatusLine().getStatusCode() < 500) {
			flag = "failure";
			throw new JenkinsException("Client Error: bad Request.");
		}
		if (response.getStatusLine().getStatusCode() >= 500) {
			flag = "failure";
			throw new JenkinsException("Jenkins server Error: failed to fulfill the request.");
		}
		return flag;
	}

	/**
	 * build URI according to RequestFormats
	 * 
	 * @param jobName
	 * @return
	 */
	private URI buildUrl(String formatSpec, Object... variables) {
		String urlPath;
		if (variables.length == 0) {
			urlPath = formatSpec;
		} else {
			urlPath = String.format(formatSpec, variables);
		}
		return urlJoin(this.uri, urlPath);
	}

	/**
	 * Join a base URL and a possibly relative URL to form an absolute
	 * interpretation of the latter
	 * 
	 * @param uri
	 * @param urlPath
	 * @return
	 */
	private URI urlJoin(URI uri, String urlPath) {
		String uriStr = uri.toString() + urlPath;
		return URI.create(uriStr);
	}

	/**
	 * Get job information dictionary.
	 * 
	 * @param jobName:
	 *            Job name
	 * @param depth:
	 *            JSON depth
	 * @return: job information (JSON format)
	 */
	public String getJobInfo(String jobName, int depth) {
		Map<String, String> map = getJobFolder(jobName);
		String folderUrl = map.get("folderUrl");
		String shortName = map.get("shortName");
		String res = "";
		try {
			res = jenkinsOpen(buildUrl(RequestFormat.JOB_INFO, folderUrl, shortName, depth));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		// return new JSONObject(res);
		return res;
	}

	/**
	 * Get configuration of existing Jenkins job.
	 * 
	 * @param jobName:
	 *            Name of Jenkins job
	 * @return: job configuration (XML format)
	 */
	public String getJobConf(String jobName) {
		Map<String, String> map = getJobFolder(jobName);
		String folderUrl = map.get("folderUrl");
		String shortName = map.get("shortName");
		String res = "";
		try {
			res = jenkinsOpen(buildUrl(RequestFormat.CONFIG_JOB, folderUrl, shortName));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Get build information.
	 * 
	 * @param jobName:
	 *            Job name
	 * @param num:
	 *            Build number
	 * @param depth:
	 *            JSON depth
	 * @return build information (JSON format)
	 */
	public String getBuildInfo(String jobName, int num, int depth) {
		Map<String, String> map = getJobFolder(jobName);
		String folderUrl = map.get("folderUrl");
		String shortName = map.get("shortName");
		String res = "";
		try {
			res = jenkinsOpen(buildUrl(RequestFormat.BUILD_INFO, folderUrl, shortName, num, depth));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		// return new JSONObject(res);
		return res;
	}

	/**
	 * Get information on this Master or item on Master. This information
	 * includes job list and view information and can be used to retreive
	 * information on items such as job folders.
	 * 
	 * @param item:
	 *            item to get information about on this Master
	 * @param query:
	 *            xpath to extract information about on this Master
	 * @return information about Master or item (JSON format)
	 */
	public String getInfo(String item, String query) {
		String url = RequestFormat.INFO;
		url = item + "/" + RequestFormat.INFO;
		if (!query.equals("")) {
			url += query;
		}
		String res = "";
		try {
			res = jenkinsOpen(buildUrl(url));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		return res;

	}

	/**
	 * Get list of all jobs recursively to the given folder depth. Each job is a
	 * dictionary with 'name', 'url', 'color' and 'fullname' keys.
	 * 
	 * @param folderDepth:
	 *            Number of levels to search
	 * @return: array of jobs (JSON format)
	 */
	public String getAllJobs(int folderDepth) {
		// List<String> jobList = new ArrayList<String>();
		JSONObject jobj = new JSONObject(getInfo("", RequestFormat.JOBS_QUERY));
		JSONArray jobs = jobj.getJSONArray("jobs");
		return jobs.toString();
	}

	/**
	 * Get the name and folder (see cloudbees plugin). This is a method to
	 * support cloudbees folder plugin. Url request should take into account
	 * folder path when the job name specify it (ex.: 'folder/job')
	 * 
	 * @param name:
	 *            Job name
	 * @return Map [ 'folder path for Request', 'Name of job without folder
	 *         path' ]
	 */
	public Map<String, String> getJobFolder(String name) {
		String[] aPath = name.split("/");
		String folderUrl = "";
		String shortName = aPath[aPath.length - 1];
		if (aPath.length > 1) {
			String tmp = aPath[0];
			for (int i = 1; i < aPath.length - 1; i++) {
				tmp += "/job/" + aPath[i];
			}
			folderUrl = "job/" + tmp + "/";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("folderUrl", folderUrl);
		map.put("shortName", shortName);
		return map;
	}

	/**
	 * Rename an existing Jenkins job Will raise an exception whenever the
	 * source and destination folder for this jobs won't be the same.
	 * 
	 * @param fromName:
	 *            Name of Jenkins job to rename
	 * @param toName:
	 *            New Jenkins job name
	 * @return
	 */
	public String renameJob(String fromName, String toName) {
		String res = "";
		Map<String, String> fromMap = getJobFolder(fromName);
		String fromFolderUrl = fromMap.get("folderUrl");
		String shortFromName = fromMap.get("shortName");

		Map<String, String> toMap = getJobFolder(toName);
		String toFolderUrl = toMap.get("folderUrl");
		String shortToName = toMap.get("shortName");
		if (fromFolderUrl.equals(toFolderUrl)) {
			try {
				res = jenkinsPost(buildUrl(RequestFormat.RENAME_JOB, fromFolderUrl, shortFromName, shortToName));
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JenkinsException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * Delete Jenkins job permanently.
	 * 
	 * @param name:
	 *            Name of Jenkins job
	 * @return
	 */
	public String deleteJob(String name) {
		String res = "";
		Map<String, String> map = getJobFolder(name);
		try {
			res = jenkinsPost(buildUrl(RequestFormat.DELETE_JOB, map.get("folderUrl"), map.get("shortName")));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Enable Jenkins job.
	 * 
	 * @param name:
	 *            Name of Jenkins job
	 * @return
	 */
	public String enableJob(String name) {
		String res = "";
		Map<String, String> map = getJobFolder(name);
		try {
			res = jenkinsPost(buildUrl(RequestFormat.ENABLE_JOB, map.get("folderUrl"), map.get("shortName")));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Disable Jenkins job.
	 * 
	 * @param name:
	 *            Name of Jenkins job
	 * @return
	 */
	public String disableJob(String name) {
		String res = "";
		Map<String, String> map = getJobFolder(name);
		try {
			res = jenkinsPost(buildUrl(RequestFormat.DISABLE_JOB, map.get("folderUrl"), map.get("shortName")));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Trigger build job.
	 * 
	 * @param name:
	 *            name of job
	 * @return
	 */
	public String buildJob(String name) {
		String res = "";
		Map<String, String> map = getJobFolder(name);
		try {
			res = jenkinsPost(buildUrl(RequestFormat.BUILD_JOB, map.get("folderUrl"), map.get("shortName")));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Check whether a job exists
	 * 
	 * @param name:
	 *            Name of Jenkins job
	 * @return True if Jenkins job exists
	 */
	public Boolean jobExits(String name) {
		Map<String, String> map = getJobFolder(name);
		try {
			String res = jenkinsOpen(buildUrl(RequestFormat.JOB_NAME, map.get("folderUrl"), map.get("shortName")));
			if (new JSONObject(res).getString("name").equals(map.get("shortName"))) {
				return true;
			}
		} catch (JSONException je) {
			return false;
		} catch (ParseException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return false;
	}

	/**
	 * Create a new Jenkins job param name: Name of Jenkins job param
	 * config_xml: config file text
	 */
	public void createJob(String name, String configXML) {
		Map<String, String> map = getJobFolder(name);
		try {
			if (jobExits(name)) {
				throw new JenkinsException("job " + name + " already exists.");
			}
			String res = jenkinsPost(buildUrl(RequestFormat.CREATE_JOB, map.get("folderUrl"), map.get("shortName")),
					configXML, headerContentType);
		} catch (JenkinsException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Change configuration of existing Jenkins job. To create a new job, see
	 * :meth:`Jenkins.createJob`.
	 * 
	 * @param name:
	 *            Name of Jenkins job
	 * @param configXML:
	 *            New XML configuration
	 */
	public void reconfigJob(String name, String configXML) {
		Map<String, String> map = getJobFolder(name);
		// try {
		// if(!jobExits(name)){
		// throw new JenkinsException("job " + name + " do not exist. A new one
		// will be created.");
		// }
		// createJob(name, configXML);
		// return;
		// } catch (JenkinsException e) {
		// e.printStackTrace();
		// }
		try {
			if (!jobExits(name)) {
				throw new JenkinsException("job " + name + " do not exist.");
			}
			return;
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
		try {
			String res = jenkinsPost(buildUrl(RequestFormat.CONFIG_JOB, map.get("folderUrl"), map.get("shortName")),
					configXML, headerContentType);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JenkinsException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JenkinsServer server = new JenkinsServer("http://localhost:8080/", "sommer", "sommer");
		boolean b = server.jobExits("mavenprojectaa");
		System.out.println(b);
		System.exit(0);
	}

}
