package com.kiuwan.plugins.kiuwanJenkinsPlugin;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.Proxy.Type;
import java.net.URL;

import org.apache.commons.lang.RandomStringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.kiuwan.plugins.kiuwanJenkinsPlugin.model.KiuwanModelObject;
import com.kiuwan.plugins.kiuwanJenkinsPlugin.model.ProxyProtocol;

import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.Secret;
import jenkins.model.Jenkins;

public class KiuwanConnectionProfile implements Describable<KiuwanConnectionProfile>, KiuwanModelObject, Serializable {

	private static final long serialVersionUID = 1768850962607295267L;

	public static final String CONFIGURE_PROXY_NONE = "false";
	public static final String CONFIGURE_PROXY_JENKINS = "jenkins";
	public static final String CONFIGURE_PROXY_CUSTOM = "true";
	
	public static final int DEFAULT_PROXY_PORT = 3128;
	
	private String uuid;
	private String name;
	
	private String username;
	private String password;
	private String domain;
	
	private boolean configureKiuwanURL;
	private String kiuwanURL;
	
	private String configureProxy;
	private String proxyHost;
	private int proxyPort;
	private String proxyProtocol;
	private String proxyAuthentication;
	private String proxyUsername;
	private String proxyPassword;
	
	@DataBoundConstructor
	public KiuwanConnectionProfile() {
		super();
	}
	
	@Override
	public Descriptor<KiuwanConnectionProfile> getDescriptor() {
		return Jenkins.getInstance().getDescriptorByType(KiuwanConnectionProfileDescriptor.class);
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
	
	public Type getJavaProxyType() {
		Type proxyType = null;
		
		// Type: HTTP
		if (ProxyProtocol.HTTP.getValue().equals(proxyProtocol)) {
			proxyType = Type.HTTP;
		
		// Type: socks
		} else if (ProxyProtocol.SOCKS.getValue().equals(proxyProtocol)) {
			proxyType = Type.SOCKS;
		}
		
		return proxyType;
	}
	
	@Override
	public String getDisplayName() {
		String profileName = name != null && !name.isEmpty() ? name : "?";
		String profileUsername = username != null && !username.isEmpty() ? username : "?";
		String profileHost = null;
		
		if (configureKiuwanURL) {
			try {
				URL url = new URL(kiuwanURL);
				profileHost = url.getHost() + (url.getPort() >= 0 ? ":" + url.getPort() : "");
			} catch (MalformedURLException e) {
				profileHost = "?";
			}
		} else {
			profileHost = "www.kiuwan.com";
		}
		
		String profileProxy = null;
		if (configureProxy == null) {
			profileProxy = "no proxy";
			
		} else if (Boolean.FALSE.equals(configureProxy)) {
			profileProxy = "Jenkins proxy";

		} else if (Boolean.TRUE.equals(configureProxy)) {
			profileProxy = "custom proxy";
		}
		
		return profileName + " - " + profileUsername + "@" + profileHost + " - using " + profileProxy + " (" + this.uuid + ")";
	}
	
	@Override
	public String getValue() {
		return uuid;
	}
	
	public String generateUuid() {
		int length = 8;
		String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(length);
		String head = randomAlphanumeric.substring(0, length / 2);
		String tail = randomAlphanumeric.substring(length / 2);
		this.uuid = head + "-" + tail;
		return this.uuid;
	}

	public String getUuid() { return uuid; }
	public String getName() { return name; }
	public String getUsername() { return username; }
	public String getPassword() { return decrypt(password); }
	public String getDomain() { return domain; }
	public boolean isConfigureKiuwanURL() { return configureKiuwanURL; }
	public String getKiuwanURL() { return kiuwanURL; }
	public String getConfigureProxy() { return configureProxy; }
	public String getProxyHost() { return proxyHost; }
	public int getProxyPort() { return proxyPort; }
	public String getProxyProtocol() { return proxyProtocol; }
	public String getProxyAuthentication() { return proxyAuthentication; }
	public String getProxyUsername() { return proxyUsername; }
	public String getProxyPassword() { return decrypt(proxyPassword); }

	@DataBoundSetter public void setUuid(String uuid) { this.uuid = uuid; }
	@DataBoundSetter public void setName(String name) { this.name = name; }
	@DataBoundSetter public void setUsername(String username) { this.username = username; }
	@DataBoundSetter public void setPassword(String password) { this.password = encrypt(password); }
	@DataBoundSetter public void setDomain(String domain) { this.domain = domain; }
	@DataBoundSetter public void setConfigureKiuwanURL(boolean configureKiuwanURL) { this.configureKiuwanURL = configureKiuwanURL; }
	@DataBoundSetter public void setKiuwanURL(String kiuwanURL) { this.kiuwanURL = kiuwanURL; }
	@DataBoundSetter public void setConfigureProxy(String configureProxy) { this.configureProxy = configureProxy; }
	@DataBoundSetter public void setProxyHost(String proxyHost) { this.proxyHost = proxyHost; }
	@DataBoundSetter public void setProxyPort(int proxyPort) { this.proxyPort = proxyPort; }
	@DataBoundSetter public void setProxyProtocol(String proxyProtocol) { this.proxyProtocol = proxyProtocol; }
	@DataBoundSetter public void setProxyAuthentication(String proxyAuthentication) { this.proxyAuthentication = proxyAuthentication; }
	@DataBoundSetter public void setProxyUsername(String proxyUsername) { this.proxyUsername = proxyUsername; }
	@DataBoundSetter public void setProxyPassword(String proxyPassword) { this.proxyPassword = encrypt(proxyPassword); }
	
	private static String encrypt(String value) {
		return Secret.fromString(value).getEncryptedValue();
	}
	
	private static String decrypt(String value) {
		return Secret.toString(Secret.decrypt(value));
	}
	
}
