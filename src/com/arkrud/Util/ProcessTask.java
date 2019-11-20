package com.arkrud.Util;

import java.util.concurrent.Callable;

public class ProcessTask implements Callable<Integer> {
	private String user;
	private String nodeIP;
	private String keyPair;

	public ProcessTask(String user, String nodeIP, String keyPair) {
		this.user = user;
		this.nodeIP = nodeIP;
		this.keyPair = keyPair;
	}

	@Override
	public Integer call() throws Exception {
		UtilMethodsFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("/", "\\");
		ProcessBuilder p = new ProcessBuilder(UtilMethodsFactory.getConfigPath() + "putty.exe", "-i", UtilMethodsFactory.getPuttyCertPath() + "certs\\" + keyPair + ".ppk", user + "@" + nodeIP);
		p.redirectErrorStream(true);
		Process pr = p.start();
		// p5.waitFor();
		return pr.exitValue();
	}
}