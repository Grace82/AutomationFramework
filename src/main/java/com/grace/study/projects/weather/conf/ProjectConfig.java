package com.grace.study.projects.weather.conf;

import java.io.FileNotFoundException;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

import com.grace.study.configuration.Config;


public class ProjectConfig extends Config {
	private volatile static ProjectConfig projectConfig;
	private volatile static Config config;
	
	private ProjectConfig(String[] args) throws FileNotFoundException, ArgumentParserException{
		super(args);
	}
	
	public static ProjectConfig getProjectConfig(String[] args) throws ArgumentParserException, FileNotFoundException{
		if(projectConfig == null){
			synchronized (ProjectConfig.class){
				if(projectConfig == null){
					projectConfig = new ProjectConfig(args);
				}
			}
		}
		return projectConfig;
	}
	
	public static void main(String[] args) throws FileNotFoundException, ArgumentParserException{
		ProjectConfig pc = ProjectConfig.getProjectConfig(args);
		System.out.println(pc.getCurrentProject());
	
}
}
