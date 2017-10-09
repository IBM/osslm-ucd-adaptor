package com.accantosystems.stratoss.driver.ucd.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to the UrbanCode Deploy Resource Manager Driver.
 *
 * <p>
 *     Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "urbancodedeploy")
public class UCDProperties {

	private String resourceManagerName = "ucd";
	private boolean allowSelfSignedCertificates = false;
	private final Map<String, StaticResourceType> types = new HashMap<>();
	private final Map<String, UrbanCodeDeployEnvironment> environments = new HashMap<>();

    public String getResourceManagerName() {
		return resourceManagerName;
	}
	public void setResourceManagerName(String resourceManagerName) {
		this.resourceManagerName = resourceManagerName;
	}
	public boolean isAllowSelfSignedCertificates() {
		return allowSelfSignedCertificates;
	}
	public void setAllowSelfSignedCertificates(boolean allowSelfSignedCertificates) {
		this.allowSelfSignedCertificates = allowSelfSignedCertificates;
	}
	public Map<String, StaticResourceType> getTypes() {
		return types;
	}
	public Map<String, UrbanCodeDeployEnvironment> getEnvironments() {
		return environments;
	}
    
	public static class StaticResourceType {
    	
    	private String filename;

    	public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
    	
    }

	public static class UrbanCodeDeployEnvironment {
		
		private final UrbanCodeDeployServer ucdServer = new UrbanCodeDeployServer();
		private final UrbanCodeDeployServer ucdPatterns = new UrbanCodeDeployServer();
		private final NotificationBroker notifications = new NotificationBroker();
		
		public UrbanCodeDeployServer getUcdServer() {
			return ucdServer;
		}
		public UrbanCodeDeployServer getUcdPatterns() {
			return ucdPatterns;
		}
		public NotificationBroker getNotifications() {
			return notifications;
		}

		public static class UrbanCodeDeployServer {
			
        	private String url;
        	private String username;
        	private String password;

    		public String getUrl() {
    			return url;
    		}
    		public void setUrl(String url) {
    			this.url = url;
    		}
    		public String getUsername() {
    			return username;
    		}
    		public void setUsername(String username) {
    			this.username = username;
    		}
    		public String getPassword() {
    			return password;
    		}
    		public void setPassword(String password) {
    			this.password = password;
    		}
    		
		}
		
		public static class NotificationBroker {
			
			private String hostname;
			private String username;
			private String password;
			
			public String getHostname() {
				return hostname;
			}
			public void setHostname(String hostname) {
				this.hostname = hostname;
			}
			public String getUsername() {
				return username;
			}
			public void setUsername(String username) {
				this.username = username;
			}
			public String getPassword() {
				return password;
			}
			public void setPassword(String password) {
				this.password = password;
			}

		}
    	        	
    }
    
}
