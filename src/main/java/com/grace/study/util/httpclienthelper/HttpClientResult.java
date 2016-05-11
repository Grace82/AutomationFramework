package com.grace.study.util.httpclienthelper;

import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;

public class HttpClientResult  {
	private CloseableHttpClient httpClient;
	private CloseableHttpResponse response;
	private List<Cookie> cookies;
	private String httpBody;
	private Header[] headerAll;
	private int statusCode;
	private HttpClientContext context;
	private String redirectURL;
	
	public String getRedirectURL(){
		return this.redirectURL;
	}
	
	public void setRedirectURL(String redirectURL){
		this.redirectURL = redirectURL;	
	}
	
	public HttpClientContext getHttpClientContext(){
		return this.context;
	}
	
	public void setHttpClientContext(HttpClientContext context){
		this.context = context;
	}
	
	public List<Cookie> getCookies(){
		return this.cookies;
	}
	
	public void setCookies(List<Cookie> cookies){
		this.cookies = cookies;
	}
	
	public CloseableHttpClient getHttpClient(){
		return this.httpClient;
	}
	
	public void setCloseableHttpClient(CloseableHttpClient httpClient){
		this.httpClient = httpClient;
	}
	
	
	public CloseableHttpResponse getCloseableHttpResponse(){
		return this.response;
	}
	
	public void setCloseableHttpResponse(CloseableHttpResponse response){
		this.response = response;
	}
	
	public String getHttpBody(){
		return this.httpBody;
	}
	
	public void setHttpBody(String httpBody){
		this.httpBody = httpBody;
	}
	
	public Header[] getResponseAllHeader(){
		return this.headerAll;
	}
	
	public void setResponseAllHeader(Header[] headerAll){
		this.headerAll = headerAll;
	}
	
	public int getStatusCode(){
		return this.statusCode;
	}
	
	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}
 
}
