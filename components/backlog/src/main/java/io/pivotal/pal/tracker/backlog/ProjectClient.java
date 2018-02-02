package io.pivotal.pal.tracker.backlog;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private final Map<String, ProjectInfo> projectCache;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
        this.projectCache = new ConcurrentHashMap<>();
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        String url = getUrl(projectId);
        ProjectInfo fetchedProjectInfo = restOperations.getForObject(url, ProjectInfo.class);

        projectCache.put(url, fetchedProjectInfo);

        return fetchedProjectInfo;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        return projectCache.get(getUrl(projectId));
    }

    private String getUrl(long projectId) {
        return registrationServerEndpoint + "/projects/" + projectId;
    }
}