package com.qa.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

/**
 * RestClient - Wrapper class for Apache HttpClient Provides methods for HTTP
 * operations: GET, POST, PUT, DELETE
 */
public class RestClient {

	private static final Logger logger = Logger.getLogger(RestClient.class);

	/**
	 * GET Request
	 * 
	 * @param url       - Complete API endpoint URL
	 * @param headerMap - HTTP headers
	 * @return CloseableHttpResponse
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CloseableHttpResponse get(String url, HashMap<String, String> headerMap)
			throws ClientProtocolException, IOException {

		logger.info("Executing GET request to: " + url);

		// Create HTTP Client
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// Create GET request
		HttpGet httpGet = new HttpGet(url);

		// Add headers to request
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				httpGet.addHeader(entry.getKey(), entry.getValue());
				logger.debug("Adding header: " + entry.getKey() + " = " + entry.getValue());
			}
		}

		// Execute request and get response
		CloseableHttpResponse response = httpClient.execute(httpGet);

		logger.info("GET request executed. Status: " + response.getStatusLine().getStatusCode());

		return response;
	}

	/**
	 * POST Request
	 * 
	 * @param url          - Complete API endpoint URL
	 * @param entityString - Request body as JSON string
	 * @param headerMap    - HTTP headers
	 * @return CloseableHttpResponse
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CloseableHttpResponse post(String url, String entityString, HashMap<String, String> headerMap)
			throws ClientProtocolException, IOException {

		logger.info("Executing POST request to: " + url);
		logger.debug("Request body: " + entityString);

		// Create HTTP Client
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// Create POST request
		HttpPost httpPost = new HttpPost(url);

		// Set request body
		StringEntity entity = new StringEntity(entityString);
		httpPost.setEntity(entity);

		// Add headers to request
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
				logger.debug("Adding header: " + entry.getKey() + " = " + entry.getValue());
			}
		}

		// Execute request and get response
		CloseableHttpResponse response = httpClient.execute(httpPost);

		logger.info("POST request executed. Status: " + response.getStatusLine().getStatusCode());

		return response;
	}

	/**
	 * PUT Request
	 * 
	 * @param url          - Complete API endpoint URL
	 * @param entityString - Request body as JSON string
	 * @param headerMap    - HTTP headers
	 * @return CloseableHttpResponse
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CloseableHttpResponse put(String url, String entityString, HashMap<String, String> headerMap)
			throws ClientProtocolException, IOException {

		logger.info("Executing PUT request to: " + url);
		logger.debug("Request body: " + entityString);

		// Create HTTP Client
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// Create PUT request
		HttpPut httpPut = new HttpPut(url);

		// Set request body
		StringEntity entity = new StringEntity(entityString);
		httpPut.setEntity(entity);

		// Add headers to request
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				httpPut.addHeader(entry.getKey(), entry.getValue());
				logger.debug("Adding header: " + entry.getKey() + " = " + entry.getValue());
			}
		}

		// Execute request and get response
		CloseableHttpResponse response = httpClient.execute(httpPut);

		logger.info("PUT request executed. Status: " + response.getStatusLine().getStatusCode());

		return response;
	}

	/**
	 * DELETE Request
	 * 
	 * @param url       - Complete API endpoint URL
	 * @param headerMap - HTTP headers
	 * @return CloseableHttpResponse
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CloseableHttpResponse delete(String url, HashMap<String, String> headerMap)
			throws ClientProtocolException, IOException {

		logger.info("Executing DELETE request to: " + url);

		// Create HTTP Client
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// Create DELETE request
		HttpDelete httpDelete = new HttpDelete(url);

		// Add headers to request
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				httpDelete.addHeader(entry.getKey(), entry.getValue());
				logger.debug("Adding header: " + entry.getKey() + " = " + entry.getValue());
			}
		}

		// Execute request and get response
		CloseableHttpResponse response = httpClient.execute(httpDelete);

		logger.info("DELETE request executed. Status: " + response.getStatusLine().getStatusCode());

		return response;
	}
}