package rest.consumer.impl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;

import rest.consumer.api.RestConsumer;

/**
 * @author marcialcalvo
 */
@Component(immediate = true, service = RestConsumer.class)
public class RestConsumerImpl implements RestConsumer {
	
	private static Log _log = LogFactoryUtil.getLog(RestConsumerImpl.class);

	@Override
	public String post(String endpoint,String contentType, Map<String,String> mapAttrs) {
		_log.info("Endpoint -> "+endpoint);
		_log.info("contentType -> "+contentType);
		_log.info("MapAttrs --> "+ mapAttrs.toString());

		HttpClient instance = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
	   
		HttpResponse response;
	    HttpPost httpPost = new HttpPost(endpoint);
	    
	    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
	    
	    List<NameValuePair> params = convert(mapAttrs);
	    
	    try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    _log.info(httpPost.getURI());
	    
	    String result = "";
		try {
			response = instance.execute(httpPost);
			
			 _log.info(response.getStatusLine().getStatusCode());
			 _log.info(response.getStatusLine().getReasonPhrase());

			 HttpEntity entity = response.getEntity();
			 result = EntityUtils.toString(entity);
			 _log.info(result);
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return result;
	}
	
	protected static ArrayList<NameValuePair> convert(Map<String, String> params) {
	    Map<String, String> callParams = params;
	    ArrayList<NameValuePair> apiParams = new ArrayList<NameValuePair>();
	    for (Map.Entry<String, String> entry : callParams.entrySet()) {
	        apiParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	    }
	    return apiParams;
	}
	
	
}