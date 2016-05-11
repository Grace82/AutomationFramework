package com.grace.study.util.httpclienthelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.grace.study.util.loghelper.LoggedTestCase;

public class HttpClientUtil extends LoggedTestCase {
	private static CloseableHttpClient httpClient = null;
	private static CookieStore cookieStore = null;
	private static Map postParams = null;
	private static Map headersParams = null;
	private static HttpClientContext context = null;

	private static final String CHARSET_GBK = "GBK";
	private static final String SSL_DEFAULT_SCHEME = "https";
	private static final int SSL_DEFAULT_PORT = 443;
	private static final String CHARSET_UTF8 = "UTF-8";
	
	private static final int MAX_TIMEOUT = 5000; 

	private HttpClientUtil() {
	}

	private static synchronized CloseableHttpClient getHttpClient()
			throws NoSuchAlgorithmException, Exception {
		HttpRequestRetryHandler myRetryHandler = getHttpRequestRetryHandler();
		RequestConfig defaultRequestConfig = getRequestConfig();
		// cookieStore = getCookieStore();
		HttpClientConnectionManager cm = getTrustAllHttpsCertificates();
		if (null == httpClient) {
			httpClient = HttpClients.custom()
					.setDefaultRequestConfig(defaultRequestConfig)
					.disableRedirectHandling()
					// .setDefaultCookieStore(cookieStore)
					.setConnectionManager(cm).setRetryHandler(myRetryHandler)
					.setConnectionManagerShared(true)
					.build();
		}
		return httpClient;
	}

//	private static synchronized CloseableHttpClient getHttpClient(Map<Object,Object> paramsMap) throws NoSuchAlgorithmException, Exception {
//		HttpRequestRetryHandler myRetryHandler = getHttpRequestRetryHandler();
//		RequestConfig defaultRequestConfig = getRequestConfig();
//		// cookieStore = getCookieStore();
//		List<Header> defaultHeaders = getDefaultHeader(paramsMap);
//		HttpClientConnectionManager cm = getTrustAllHttpsCertificates();
//		if (null == httpClient) {
//			httpClient = HttpClients.custom()
//					.setDefaultRequestConfig(defaultRequestConfig)
//					.disableRedirectHandling()
//					// .setDefaultCookieStore(cookieStore)
//					.setConnectionManager(cm).setDefaultHeaders(defaultHeaders)
//					.setConnectionManagerShared(true)
//					.setRetryHandler(myRetryHandler).build();
//		}
//		return httpClient;
//	}

//	private static List<Header> getDefaultHeader(Map<Object,Object> paramsMap) {
//		List<Header> headers = new ArrayList<Header>();
//		if (paramsMap == null || paramsMap.size() == 0) {
//			return null;
//		}
//		Iterator<Map.Entry<Object, Object>> entries = paramsMap.entrySet().iterator();
//		while (entries.hasNext()) {
//			Map.Entry<Object, Object> entry = entries.next();
//			headers.add(new BasicHeader(entry.getKey().toString(), entry
//					.getValue().toString()));
//		}
//		return headers;
//	}

	private static RequestConfig getRequestConfig() {
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.STANDARD_STRICT)
				.setSocketTimeout(MAX_TIMEOUT)
				.setConnectTimeout(MAX_TIMEOUT)
				.setConnectionRequestTimeout(MAX_TIMEOUT)
				.build();
		return defaultRequestConfig;
	}

//	private static BasicCookieStore getCookieStore() {
//		return new BasicCookieStore();
//	}

	private static HttpClientConnectionManager getTrustAllHttpsCertificates()
			throws NoSuchAlgorithmException, Exception {
		TrustManager[] trustAllCerts = new TrustManager[1];
		TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sc);
		
		SSLContext sctls = SSLContext.getInstance("TLS");
		sctls.init(null, trustAllCerts, null);
		SSLConnectionSocketFactory ssltls = new SSLConnectionSocketFactory(sctls);
		
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();

		Registry<ConnectionSocketFactory> r = RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("https", sslsf)
				.register("https", ssltls)
				.register("http", plainSF).build();

		HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
		return cm;

	}

	static class miTM implements TrustManager, X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType)
				throws CertificateException {
			return;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType)
				throws CertificateException {
			return;
		}
	}

	private static HttpRequestRetryHandler getHttpRequestRetryHandler() {
		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= 5) {
					return false;
				}
				if (exception instanceof InterruptedIOException) {
					// Timeout
					return false;
				}
				if (exception instanceof UnknownHostException) {
					// Unknown host
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {
					// Connection refused
					return false;
				}
				if (exception instanceof SSLException) {
					// SSL handshake exception
					return false;
				}
				HttpClientContext clientContext = HttpClientContext
						.adapt(context);
				HttpRequest request = clientContext.getRequest();
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					// Retry if the request is considered idempotent
					return true;
				}
				return false;
			}
		};
		return myRetryHandler;
	}
	
	
	public static HttpClientResult post(String url,Map<String,String> stringBodyparams,String stringBodyCharset,Map<String,String> fileBodyParams,String fileBodyCharset)
			throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpClientContext context = HttpClientContext.adapt(localContext);
		
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		if(stringBodyparams != null){
			Iterator<Map.Entry<String, String>> strIterator = stringBodyparams.entrySet().iterator();
			while(strIterator.hasNext()){
				Map.Entry<String, String> entry = strIterator.next();
				multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName(stringBodyCharset)));
			}
						
		}

		if(fileBodyParams != null){
			Iterator<Entry<String, String>> fileIteator = fileBodyParams.entrySet().iterator();
			while(fileIteator.hasNext()){
				Map.Entry<String, String> entry = fileIteator.next();
				if (StringUtils.isBlank(entry.getValue()) || entry.getValue() == null) continue;
				File file = new File(entry.getValue());
				multipartEntityBuilder.addBinaryBody(entry.getKey(), file);
			}
		}
		
		HttpEntity httpEntity = multipartEntityBuilder.build();
		
		HttpPost hp = new HttpPost(url);
		hp.setEntity(httpEntity);
		
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hp, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str);
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	public static HttpClientResult post(String url,Map<String,String> stringBodyparams,String stringBodyCharset,Map<String,String> fileBodyParams,String fileBodyCharset,HttpClientContext context)
			throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		if(stringBodyparams != null){
			Iterator<Map.Entry<String, String>> strIterator = stringBodyparams.entrySet().iterator();
			while(strIterator.hasNext()){
				Map.Entry<String, String> entry = strIterator.next();
				multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName(stringBodyCharset)));
			}
						
		}

		if(fileBodyParams != null){
			Iterator<Entry<String, String>> fileIteator = fileBodyParams.entrySet().iterator();
			while(fileIteator.hasNext()){
				Map.Entry<String, String> entry = fileIteator.next();
				if (StringUtils.isBlank(entry.getValue()) || entry.getValue() == null) continue;
				File file = new File(entry.getValue());
				multipartEntityBuilder.addBinaryBody(entry.getKey(), file);
			}
		}
		
		HttpEntity httpEntity = multipartEntityBuilder.build();
		
		HttpPost hp = new HttpPost(url);
		hp.setEntity(httpEntity);
		
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hp, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str);
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	
	public static HttpClientResult post(String url,Map<String, String> headerParams,Map<String,String> stringBodyparams,String stringBodyCharset,Map<String,String> fileBodyParams,String fileBodyCharset)
			throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpClientContext context = HttpClientContext.adapt(localContext);
		
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		if(stringBodyparams != null){
			Iterator<Map.Entry<String, String>> strIterator = stringBodyparams.entrySet().iterator();
			while(strIterator.hasNext()){
				Map.Entry<String, String> entry = strIterator.next();
				multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName(stringBodyCharset)));
			}
						
		}

		if(fileBodyParams != null){
			Iterator<Entry<String, String>> fileIteator = fileBodyParams.entrySet().iterator();
			while(fileIteator.hasNext()){
				Map.Entry<String, String> entry = fileIteator.next();
				if (StringUtils.isBlank(entry.getValue()) || entry.getValue() == null) continue;
				File file = new File(entry.getValue());
				multipartEntityBuilder.addBinaryBody(entry.getKey(), file);
			}
		}
		
		HttpEntity httpEntity = multipartEntityBuilder.build();
		
		HttpPost hp = new HttpPost(url);
		if(headerParams != null){
			setHeader(hp,headerParams);
		}
		hp.setEntity(httpEntity);
		
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hp, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str);
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	public static HttpClientResult post(String url,Map<String, String> headerParams,Map<String,String> stringBodyparams,String stringBodyCharset,Map<String,String> fileBodyParams,String fileBodyCharset,HttpClientContext context)
			throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		if(stringBodyparams != null){
			Iterator<Map.Entry<String, String>> strIterator = stringBodyparams.entrySet().iterator();
			while(strIterator.hasNext()){
				Map.Entry<String, String> entry = strIterator.next();
				multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName(stringBodyCharset)));
			}
						
		}

		if(fileBodyParams != null){
			Iterator<Entry<String, String>> fileIteator = fileBodyParams.entrySet().iterator();
			while(fileIteator.hasNext()){
				Map.Entry<String, String> entry = fileIteator.next();
				if (StringUtils.isBlank(entry.getValue()) || entry.getValue() == null) continue;
				File file = new File(entry.getValue());
				multipartEntityBuilder.addBinaryBody(entry.getKey(), file);
			}
		}
		
		HttpEntity httpEntity = multipartEntityBuilder.build();
		
		HttpPost hp = new HttpPost(url);
		if(headerParams != null){
			setHeader(hp,headerParams);
		}
		hp.setEntity(httpEntity);
		
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hp, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str);
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	
//	public static Result post(String url,Object json,String charset,HttpClientContext context)
//			throws NoSuchAlgorithmException, Exception {
//		if (url == null || StringUtils.isBlank(url)) {
//			return null;
//		}
//		CloseableHttpClient httpclient = getHttpClient();
//		
//		StringEntity stringEntity = new StringEntity(json.toString(),charset);
//		stringEntity.setContentEncoding(charset);  
//		stringEntity.setContentType("application/json");  
//		stringEntity.setChunked(true);
//
//		HttpPost hp = new HttpPost(url);
//		hp.setEntity(stringEntity);
//		CloseableHttpResponse response = null;
//		Result result = null;
//		try {
//			response = httpclient.execute(hp, context);
//			String redirectUrl = null;
//			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
//					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
//					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
//					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {
//
//				redirectUrl = context.getTargetHost().toString()
//						+ context.getResponse().getFirstHeader("location")
//								.getValue().toString();
//				System.out.println(redirectUrl);
//			}
//			result = new Result();
//			StringBuffer tmpStr = new StringBuffer();
//			
//			result.setRedirectURL(redirectUrl);
//			result.setHttpClientContext(context);
//			result.setCloseableHttpClient(httpclient);
//			result.setCloseableHttpResponse(response);
//			
//			HttpEntity entity = response.getEntity();
//			InputStreamReader in = new InputStreamReader(entity.getContent());
//			BufferedReader buf = new BufferedReader(in);
//			String str;
//			while ((str = buf.readLine()) != null) {
//				tmpStr.append(str);
//			}
//			result.setHttpBody(tmpStr.toString());
//			
//			result.setStatusCode(response.getStatusLine().getStatusCode());
//			result.setCookies(context.getCookieStore().getCookies());
//			result.setResponseAllHeader(response.getAllHeaders());
//			
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally{
//			closeClient(httpclient);
//			httpclient = null;
//			closeResponse(response);
//		}
//		return result;
//	}
//	
//	
//	public static Result post(String url,Object json,String charset)
//			throws NoSuchAlgorithmException, Exception {
//		if (url == null || StringUtils.isBlank(url)) {
//			return null;
//		}
//		CloseableHttpClient httpclient = getHttpClient();
//		HttpContext localContext = new BasicHttpContext();
//		HttpClientContext context = HttpClientContext.adapt(localContext);
//		
//		StringEntity stringEntity = new StringEntity(json.toString(),charset);
//		stringEntity.setContentEncoding(charset);  
//		stringEntity.setContentType("application/json");  
//		stringEntity.setChunked(true);
//
//		HttpPost hp = new HttpPost(url);
//		hp.setEntity(stringEntity);
//		CloseableHttpResponse response = null;
//		Result result = null;
//		try {
//			response = httpclient.execute(hp, context);
//			String redirectUrl = null;
//			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
//					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
//					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
//					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {
//
//				redirectUrl = context.getTargetHost().toString()
//						+ context.getResponse().getFirstHeader("location")
//								.getValue().toString();
//				System.out.println(redirectUrl);
//			}
//			result = new Result();
//			StringBuffer tmpStr = new StringBuffer();
//			
//			result.setRedirectURL(redirectUrl);
//			result.setHttpClientContext(context);
//			result.setCloseableHttpClient(httpclient);
//			result.setCloseableHttpResponse(response);
//			
//			HttpEntity entity = response.getEntity();
//			InputStreamReader in = new InputStreamReader(entity.getContent());
//			BufferedReader buf = new BufferedReader(in);
//			String str;
//			while ((str = buf.readLine()) != null) {
//				tmpStr.append(str);
//			}
//			result.setHttpBody(tmpStr.toString());
//			
//			result.setStatusCode(response.getStatusLine().getStatusCode());
//			result.setCookies(context.getCookieStore().getCookies());
//			result.setResponseAllHeader(response.getAllHeaders());
//			
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally{
//			closeClient(httpclient);
//			httpclient = null;
//			closeResponse(response);
//		}
//		return result;
//	}
	
	
	public static HttpClientResult post(String schema,String host,String path,Map<String, String> headerParams,Map<Object, Object> paramsMap,String charset,HttpClientContext context) throws NoSuchAlgorithmException, Exception{
		URI uri = setURI(schema, host, path);
		if(uri != null){
			return post(uri.toString(),headerParams,paramsMap,charset,context);
		}
		return null;
	}
	
	public static HttpClientResult post(String url,Map<String, String> headerParams, Map<Object,Object> params,String charset,HttpClientContext context)
			throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}

		CloseableHttpClient httpclient = getHttpClient();

		UrlEncodedFormEntity formEntity = null;
		try {
			if (charset == null || StringUtils.isBlank(charset)) {
				formEntity = new UrlEncodedFormEntity(getParamsList(params));
			} else {
				formEntity = new UrlEncodedFormEntity(getParamsList(params),
						charset);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpPost hp = new HttpPost(url);
		if(headerParams != null){
			setHeader(hp,headerParams);
		}
		hp.setEntity(formEntity);
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hp, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str);
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}

		return result;
	}
	
	public static HttpClientResult post(String schema,String host,String path,Map<String, String> headerParams,Map<Object, Object> paramsMap,String charset) throws NoSuchAlgorithmException, Exception{
		URI uri = setURI(schema, host, path);
		if(uri != null){
			return post(uri.toString(),headerParams,paramsMap,charset);
		}
		return null;
	}
	
	public static HttpClientResult post(String url,Map<String, String> headerParams, Map<Object,Object> params,String charset)
			throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}

		CloseableHttpClient httpclient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpClientContext context = HttpClientContext.adapt(localContext);
		UrlEncodedFormEntity formEntity = null;
		try {
			if (charset == null || StringUtils.isBlank(charset)) {
				formEntity = new UrlEncodedFormEntity(getParamsList(params));
			} else {
				formEntity = new UrlEncodedFormEntity(getParamsList(params),
						charset);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpPost hp = new HttpPost(url);
		if(headerParams != null){
			setHeader(hp,headerParams);
		}
		hp.setEntity(formEntity);
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hp, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str);
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}

		return result;
	}
	
	public static HttpClientResult post(String schema, String host, String path,
			Map<Object, Object> paramsMap, String charset) throws NoSuchAlgorithmException, Exception{
		URI uri = setURI(schema, host, path);
		if (uri != null) {
			return post(uri.toString(),paramsMap,charset);
		}
		return null;
	}
	
	public static HttpClientResult post(String url,Map<Object,Object> params,String charset)
			throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpClientContext context = HttpClientContext.adapt(localContext);
		UrlEncodedFormEntity formEntity = null;
		try {
			if (charset == null || StringUtils.isBlank(charset)) {
				formEntity = new UrlEncodedFormEntity(getParamsList(params));
			} else {
				formEntity = new UrlEncodedFormEntity(getParamsList(params),
						charset);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpPost hp = new HttpPost(url);
		hp.setEntity(formEntity);
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hp, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str);
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	public static HttpClientResult post(String schema, String host, String path,
			Map<Object, Object> paramsMap,HttpClientContext context) throws NoSuchAlgorithmException, Exception{
		URI uri = setURI(schema, host, path);
		System.out.println("Url is : " + uri);
		if (uri != null) {
			return post(uri.toString(),paramsMap,null,context);
		}
		return null;
	}
	
	public static HttpClientResult post(String schema, String host, String path,
			Map<Object, Object> paramsMap,String charset,HttpClientContext context) throws NoSuchAlgorithmException, Exception{
		URI uri = setURI(schema, host, path);
		if (uri != null) {
			return post(uri.toString(),paramsMap,charset,context);
		}
		return null;
	}
	
	
	public static HttpClientResult post(String url, Map<Object,Object> params,String charset, HttpClientContext context)
			throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		UrlEncodedFormEntity formEntity = null;
		try {
			if (charset == null || StringUtils.isBlank(charset)) {
				formEntity = new UrlEncodedFormEntity(getParamsList(params));
			} else {
				formEntity = new UrlEncodedFormEntity(getParamsList(params),
						charset);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpPost hp = new HttpPost(url);
		hp.setEntity(formEntity);
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hp, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);			
		}
		return result;
	}
	
	public static HttpClientResult get(String schema,String host,String path,Map<Object, Object> params, Map<String, String> headerParams,HttpClientContext context, String charset) throws NoSuchAlgorithmException, Exception{
		URI uri = setURI(schema, host, path);
		if(uri !=null){
			return get(uri.toString(),params,headerParams,context,charset);
		}
		return null;
	}
	
	public static HttpClientResult get(String schema,String host,String path, Map<String, String> headerParams,HttpClientContext context) throws NoSuchAlgorithmException, Exception{
		URI uri = setURI(schema, host, path);
		if (uri != null) {
			return get(uri.toString(),headerParams,context);
		}
		return null;
	}
	
	public static HttpClientResult get(String url, Map<String, String> headerParams,HttpClientContext context) throws NoSuchAlgorithmException, Exception{
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		
		HttpGet hg = new HttpGet(url);
		if(headerParams != null){
			setHeader(hg,headerParams);
		}
		
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hg, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	public static HttpClientResult get(String url, Map<Object, Object> params, Map<String, String> headerParams,HttpClientContext context, String charset) throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		ArrayList qparams = (ArrayList) getParamsList(params);
		if (qparams != null && qparams.size() > 0) {
			charset = (charset == null ? CHARSET_UTF8 : charset);
			String formatParams = URLEncodedUtils.format(qparams, charset);
			url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url
					.substring(0, url.indexOf("?") + 1) + formatParams);
		}
		
		CloseableHttpClient httpclient = getHttpClient();
		
		HttpGet hg = new HttpGet(url);
		if(headerParams != null){
			setHeader(hg,headerParams);
		}
		
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hg, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			System.out.println("context cookies:"
					+ context.getCookieStore().getCookies());
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	
	public static HttpClientResult get(String schema, String host, String path,
			Map<Object, Object> paramsMap, Map<String, String> headerParams,String charset)
			throws NoSuchAlgorithmException, Exception {
		URI uri = setURI(schema, host, path);
		if (uri != null) {
			return get(uri.toString(),headerParams,paramsMap,charset);
		}
		return null;
	}

	public static HttpClientResult get(String scheme, String host, String path, Map<String, String> headerParams) throws NoSuchAlgorithmException, Exception {
		URI uri = setURI(scheme, host, path);
		if (uri != null) {
			return get(uri.toString(),headerParams);
		}
		return null;
	}
	
	
	public static HttpClientResult get(String url, Map<String, String> headerParams) throws NoSuchAlgorithmException, Exception{
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpClientContext context = HttpClientContext.adapt(localContext);
		
		HttpGet hg = new HttpGet(url);
		if(headerParams != null){
			setHeader(hg,headerParams);
		}
		
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hg, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	public static HttpClientResult get(String url, Map<String, String> headerParams,Map<Object, Object> params,  String charset) throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		ArrayList qparams = (ArrayList) getParamsList(params);
		if (qparams != null && qparams.size() > 0) {
			charset = (charset == null ? CHARSET_UTF8 : charset);
			String formatParams = URLEncodedUtils.format(qparams, charset);
			url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url
					.substring(0, url.indexOf("?") + 1) + formatParams);
		}
		
		CloseableHttpClient httpclient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpClientContext context = HttpClientContext.adapt(localContext);
		
		HttpGet hg = new HttpGet(url);
		if(headerParams != null){
			setHeader(hg,headerParams);
		}
		
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hg, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			System.out.println("context cookies:"
					+ context.getCookieStore().getCookies());
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	
	public static HttpClientResult get(String schema, String host, String path,
			Map<Object, Object> paramsMap, String charset)
			throws NoSuchAlgorithmException, Exception {
		URI uri = setURI(schema, host, path);
		if (uri != null) {
			return get(uri.toString(),paramsMap,charset);
		}
		return null;
	}

	public static HttpClientResult get(String scheme, String host, String path) throws NoSuchAlgorithmException, Exception {
		URI uri = setURI(scheme, host, path);
		if (uri != null) {
			return get(uri.toString());
		}
		return null;
	}

	public static HttpClientResult get(String url) throws NoSuchAlgorithmException, Exception{
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpClientContext context = HttpClientContext.adapt(localContext);
		HttpGet hg = new HttpGet(url);
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hg, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}

	public static HttpClientResult get(String url, Map<Object, Object> params,String charset) throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		ArrayList qparams = (ArrayList) getParamsList(params);
		if (qparams != null && qparams.size() > 0) {
			charset = (charset == null ? CHARSET_UTF8 : charset);
			String formatParams = URLEncodedUtils.format(qparams, charset);
			url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url
					.substring(0, url.indexOf("?") + 1) + formatParams);
		}
		CloseableHttpClient httpclient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpClientContext context = HttpClientContext.adapt(localContext);
		HttpGet hg = new HttpGet(url);
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			response = httpclient.execute(hg, context);
			String redirectUrl = null;
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);		
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	
	public static HttpClientResult get(String schema, String host, String path,
			Map<Object, Object> paramsMap,String charset, HttpClientContext context)
			throws NoSuchAlgorithmException, Exception {
		URI uri = setURI(schema, host, path);
		if (uri != null) {
			return get(uri.toString(),paramsMap,charset,context);
		}
		return null;
	}

	public static HttpClientResult get(String scheme, String host, String path,HttpClientContext context) throws NoSuchAlgorithmException, Exception {
		URI uri = setURI(scheme, host, path);
		if (uri != null) {
			return get(uri.toString(),context);
		}
		return null;
	}

	public static HttpClientResult get(String url,HttpClientContext context) throws NoSuchAlgorithmException, Exception{
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		CloseableHttpClient httpclient = getHttpClient();
		HttpGet hg = new HttpGet(url);
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			String redirectUrl = null;
			response = httpclient.execute(hg, context);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectUrl);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}
	
	public static HttpClientResult get(String url, Map<Object, Object> params, String charset,HttpClientContext context) throws NoSuchAlgorithmException, Exception {
		if (url == null || StringUtils.isBlank(url)) {
			return null;
		}
		ArrayList qparams = (ArrayList) getParamsList(params);
		if (qparams != null && qparams.size() > 0) {
			charset = (charset == null ? CHARSET_UTF8 : charset);
			String formatParams = URLEncodedUtils.format(qparams, charset);
			url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url
					.substring(0, url.indexOf("?") + 1) + formatParams);
		}
		CloseableHttpClient httpclient = getHttpClient();
		HttpGet hg = new HttpGet(url);
		CloseableHttpResponse response = null;
		HttpClientResult result = null;
		try {
			String redirectURL = null;
			response = httpclient.execute(hg, context);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {

				String redirectUrl = context.getTargetHost().toString()
						+ context.getResponse().getFirstHeader("location")
								.getValue().toString();
				System.out.println(redirectUrl);
			}
			result = new HttpClientResult();
			StringBuffer tmpStr = new StringBuffer();
			
			result.setRedirectURL(redirectURL);
			result.setHttpClientContext(context);
			result.setCloseableHttpClient(httpclient);
			result.setCloseableHttpResponse(response);
			
			HttpEntity entity = response.getEntity();
			InputStreamReader in = new InputStreamReader(entity.getContent());
			BufferedReader buf = new BufferedReader(in);
			String str;
			while ((str = buf.readLine()) != null) {
				tmpStr.append(str).append("\n");
			}
			result.setHttpBody(tmpStr.toString());
			
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setCookies(context.getCookieStore().getCookies());
			result.setResponseAllHeader(response.getAllHeaders());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeClient(httpclient);
			httpclient = null;
			closeResponse(response);
		}
		return result;
	}

	public static void closeClient(CloseableHttpClient client) {
		if (null != client) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeResponse(CloseableHttpResponse response) {
		if (null != response) {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	private static void setCookieStore(CloseableHttpResponse response) {
//		System.out.println("----setCookieStore");
//		cookieStore = new BasicCookieStore();
//		String setCookie = response.getFirstHeader("Set-Cookie").getValue();
//		String JSESSIONID = setCookie.substring("JSESSIONID=".length(),
//				setCookie.indexOf(";"));
//		System.out.println("JSESSIONID:" + JSESSIONID);
//		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID",
//				JSESSIONID);
//	}

	private static void setHeader(HttpGet request, Map<String, String> headerParams) {
		if (request != null && headerParams != null) {
			Iterator<Entry<String, String>> entries = headerParams.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, String> entry = entries.next();
				request.setHeader(entry.getKey(), entry.getValue());
			}
		}
	}
	
	private static void setHeader(HttpPost request, Map<String, String> headerParams) {
		if (request != null && headerParams != null) {
			Iterator<Map.Entry<String, String>> entries = headerParams.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, String> entry = entries.next();
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public static Map<Object, Object> getHeadersValue(Header[] headers) {

		return null;
	}

	private static String getProtocol(String url) {
		String protocol = null;
		if (url != null) {
			int location = url.indexOf(":");
			protocol = url.substring(0, location);
			System.out.println(protocol);
		}
		return protocol;
	}

	private static List getParamsList(Map<Object, Object> paramsMap) {
		if (paramsMap == null || paramsMap.size() == 0) {
			return null;
		}
		List<Object> params = new ArrayList<Object>();
		for (Map.Entry<Object, Object> map : paramsMap.entrySet()) {
			params.add(new BasicNameValuePair(map.getKey().toString(), map
					.getValue().toString()));
		}
		return params;
	}

//	private static KeyStore createKeyStore(final URL url, final String password)
//			throws KeyStoreException, NoSuchAlgorithmException,
//			CertificateException, IOException {
//		if (url == null) {
//			throw new IllegalArgumentException("Keystore url may not be null");
//		}
//		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
//		InputStream is = null;
//		try {
//			is = url.openStream();
//			keystore.load(is, password != null ? password.toCharArray() : null);
//		} finally {
//			if (is != null) {
//				is.close();
//				is = null;
//			}
//		}
//		return keystore;
//	}

	private static URI setURI(String scheme, String host, String path)
			throws URISyntaxException {
		URI uri = null;
		if (scheme != null && host != null) {
			uri = new URIBuilder().setScheme(scheme).setHost(host)
					.setPath(path).build();
		}
		return uri;
	}

//	private static URI setURI(String scheme, String host, String path,Map<Object, Object> paramsMap) throws URISyntaxException {
//		URI uri = null;
//		if (scheme != null && host != null) {
//			uri = new URIBuilder().setScheme(scheme).setHost(host)
//					.setPath(path).setParameters(getParamsList(paramsMap))
//					.build();
//		}
//		return uri;
//	}

	public static void main(String[] args) throws NoSuchAlgorithmException,
			Exception {
		
		String scheme = "http";
		String host = "192.168.0.105:7000"; 
		String path = "/form/file/";
		Map<String,String> headerParams = new HashMap<String,String>();
		headerParams.put("Accept-Encoding", "gzip");
		HttpClientResult result = HttpClientUtil.get(scheme,host,path,headerParams);
//		System.out.println(result.getCookies().get(0).getValue());
//		System.out.println(result.getHttpClientContext().getCookieStore());
//		Header[] headerList = result.getResponseAllHeader();
//		for (Header header : headerList) {
//			System.out.println(header.getName() + " : " + header.getValue());
//		}
		
		CloseableHttpResponse response = result.getCloseableHttpResponse();
		HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));

		String csrfmiddlewaretokenValue = null;
		while (it.hasNext()) {
			HeaderElement elem = it.nextElement();
			if(elem.getName().equals("csrftoken"))
				csrfmiddlewaretokenValue = elem.getValue(); 
			// NameValuePair[] param = elem.getParameters();
			// for (int i = 0; i < param.length; i++) {
			// System.out.println(" " + param[i]);
			// }
		}
//		HttpEntity entity = result.getHttpEntity();
//		InputStreamReader in = new InputStreamReader(entity.getContent());
//		BufferedReader buf = new BufferedReader(in);
//		String str;
//		while((str = buf.readLine() )!=null){
//			System.out.println(str);
//		}
//		
		

		String fileName = "E:\\图片\\psb91617UO2.jpg";
		String url = "http://192.168.0.107:7000/form/file/";
		Map<Object,Object> params = new HashMap<Object,Object>();
		params.put("csrfmiddlewaretoken", csrfmiddlewaretokenValue);
		params.put("name", "ddd");
		params.put("title", "dddd");
		params.put("email", "d@d");
		params.put("file", "psb91617UO2.jpg");
		
		//Result resultContext = httpclientUtil.post(scheme,host,path,params,result.getHttpClientContext());
		
		
//		Result resultContext = httpclientUtil.post(url,fileName,ContentType.DEFAULT_BINARY);
//		System.out.println(resultContext.getHttpBody());
//		System.out.println(result.getHttpClientContext().getResponse().getHeaders("Content-Type")[0].getName());
		
		
		
		//String testUri = "http://192.168.3.229:7000/form/search/?q=abc";
		// Result result = HttpclientUtil
		// .get("https://www.baidu.com/");
		// // Result result =
		// HttpclientUtil.get("http://www.yeetrack.com/?p=773");
		// HttpEntity entity = result.getHttpEntity();
		// InputStreamReader in = new InputStreamReader(entity.getContent());
		// BufferedReader buf = new BufferedReader(in);
		// String str;
		// while ((str = buf.readLine()) != null) {
		// System.out.println(str);
		// }
		
		// testing for the get params
		/*
		Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("q", "abc");
		
		Map<String,String> headerParams = new HashMap<String,String>();
		headerParams.put("Accept-Encoding", "gzip");
		
		String uri = "http://192.168.0.107:7000/form/search/";
		
		Result results = httpclientUtil.get(uri, params,CHARSET_GBK);
		System.out.println(results.getStatusCode());
		// Result results = HttpclientUtil.get(scheme, host, path,CHARSET_GBK);
		HttpEntity entity = results.getHttpEntity();
		InputStreamReader in = new InputStreamReader(entity.getContent());
		BufferedReader buf = new BufferedReader(in);
		String str;
		while ((str = buf.readLine()) != null) {
			System.out.println(str);
		}*/
		
		
		String httpsurl = "https://www.baidu.com/";
		HttpClientResult httpsresult = HttpClientUtil.get(httpsurl);
		System.out.println(httpsresult.getHttpBody());
		
/*
		for (int i = 0; i < results.getCookies().size(); i++) {
			Cookie cookie = results.getCookies().get(i);
			// System.out.println(cookie);
			// System.out.println(cookie.getName());
			// System.out.println(cookie.getValue());

			String cookieName = null;
			String cookieValue = null;
			if (cookie.getName().equals("csrftoken"))
				cookieName = "";
		}

		Header[] headerList = results.getResponseHeaderAll();
		for (Header header : headerList) {
			// System.out.println(header.getName());
			// System.out.println(header.getValue());
		}

		CloseableHttpResponse response = results.getCloseableHttpResponse();
		HeaderElementIterator it = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));

		int n = 0;

		while (it.hasNext()) {
			n++;
			HeaderElement elem = it.nextElement();
			System.out.println(elem.getName());
			System.out.println(n);

			// NameValuePair[] param = elem.getParameters();
			// for (int i = 0; i < param.length; i++) {
			// System.out.println(" " + param[i]);
			// }
		}*/
	}
}
