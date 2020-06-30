package com.kiuwan.plugins.kiuwanJenkinsPlugin;

import java.io.IOException;
import java.util.logging.Level;

import com.kiuwan.plugins.kiuwanJenkinsPlugin.util.KiuwanAnalyzerInstaller;
import com.kiuwan.plugins.kiuwanJenkinsPlugin.util.KiuwanUtils;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.remoting.Channel;
import hudson.slaves.ComputerListener;
import jenkins.model.Jenkins;

@Extension
public class KiuwanComputerListener extends ComputerListener {

	@Override
	public void onOnline(Computer c, TaskListener listener) throws IOException, InterruptedException {
		// work around the bug where master doesn't call preOnline method
		if (c.getNode() == Jenkins.getInstance()) {
			process(c, c.getNode().getRootPath(), listener);
		}
	}

	@Override
	public void preOnline(Computer c, Channel channel, FilePath root, TaskListener listener)
			throws IOException, InterruptedException {
		process(c, root, listener);
	}

	public void process(Computer computer, FilePath root, TaskListener listener) throws IOException, InterruptedException {
		KiuwanGlobalConfigDescriptor descriptor = KiuwanGlobalConfigDescriptor.get();
		if (descriptor.getConnectionProfiles() != null) {
			for (KiuwanConnectionProfile connectionProfile : descriptor.getConnectionProfiles()) {
				try {
					KiuwanAnalyzerInstaller.installKiuwanLocalAnalyzer(root, listener, connectionProfile);
					
				} catch (IOException e) {
					KiuwanUtils.logger().log(Level.SEVERE, e.getLocalizedMessage());
					listener.error("Failed to install KiuwanAnalyzer: " + e);
				}
			}
		}
	}

}
