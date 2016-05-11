package com.grace.study.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.grace.study.util.loghelper.LoggedTestCase;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSHUtil extends LoggedTestCase{
	public static Connection getOpenedConnection(String host,int port, String username,  String password) throws IOException {  
		if (logger.isInfoEnabled()) {
			logger.info("connecting to " + host + " with user " + username
					+ " and pwd " + password);
		}
		Connection conn = new Connection(host, port);
		conn.connect(); // make sure the connection is opened
		boolean isAuthenticated = conn.authenticateWithPassword(username,password);
		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");
		return conn;
	}
	
	public static String execShellScript(String host, int port, String username,  String password,  String cmd) throws IOException {  
        if (logger.isInfoEnabled()) {  
            logger.info("running SSH cmd [" + cmd + "]");  
        }  
        Connection conn = null;  
        Session sess = null;  
        InputStream stdout = null;  
        BufferedReader br = null;  
        StringBuffer buffer = new StringBuffer("exec result:");  
        buffer.append(System.getProperty("line.separator"));// 换行  
        try {  
            conn = getOpenedConnection(host, port, username, password);  
            sess = conn.openSession();  
            sess.execCommand(cmd);  
            stdout = new StreamGobbler(sess.getStdout());  
            br = new BufferedReader(new InputStreamReader(stdout));  
            while (true) {  
                // attention: do not comment this block, or you will hit  
                // NullPointerException  
                // when you are trying to read exit status  
                String line = br.readLine();  
                if (line == null)  
                    break;  
                buffer.append(line);  
                buffer.append(System.getProperty("line.separator"));// 换行   
                if (logger.isInfoEnabled()) {  
                    logger.info(line);  
                }  
            }  
        } finally {  
            sess.close();  
            conn.close();  
        }  
        return buffer.toString();  
    }  
}
