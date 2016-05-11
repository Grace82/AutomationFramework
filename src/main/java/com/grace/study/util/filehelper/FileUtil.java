package com.grace.study.util.filehelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.grace.study.util.loghelper.LoggedTestCase;

public class FileUtil extends LoggedTestCase{
	
	public static void deleteFile(String fileName){
		File file = new File(fileName);
		if(file.exists()){
			if(file.isFile()){
				file.delete();
			}
		}
	}
	
	public static void createNewFile(String fileName) throws IOException{
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
	}
	
	public static List<String> getPathPatternMatchFile(String glob, String location) throws IOException{
	 	List<String> fileList = new ArrayList<String>();
        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(
                glob);
        Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path,BasicFileAttributes attrs) throws IOException {
                if (pathMatcher.matches(path)) {
                	System.out.println(path.toString());
                	fileList.add(path.toString());
                }
                return FileVisitResult.CONTINUE;
            }
            public FileVisitResult visitFileFailed(Path file , IOException exc)
                    throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }
	 
	 public static void main(String[] args) throws IOException{
		 List<String> list = new ArrayList<String>();
		 list = FileUtil.getPathPatternMatchFile("glob:**/test/Test*","D:/Study/Java/StudyAutoFrameWorker/study.auto/target/classes/com/grace/study/projects/weather");
		 System.out.println(list);
	 }
}
