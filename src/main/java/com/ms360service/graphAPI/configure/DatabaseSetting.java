package com.ms360service.graphAPI.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Database setup values
 *
 */

@ConfigurationProperties("spring.datasource")
public class DatabaseSetting {
    private String driverClassName;
    private String username;
    private String password;
    private String url;
    
    public String printout() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("username: %s\n", username));
        buffer.append(String.format("url     : %s\n", url));
        buffer.append(String.format("driverClassName  : %s\n", driverClassName));
        return buffer.toString();
    }

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
