package com.grace.study.util.filehelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

public class YamlUtil {
	public static Object loadYaml(String filePath) throws FileNotFoundException{
		InputStream input = new FileInputStream(new File(filePath));
		Yaml yaml = new Yaml();
		Object data = yaml.load(input);
		return data;
	}
}
