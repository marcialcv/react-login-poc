package rest.consumer.api;

import java.util.Map;

public interface RestConsumer {

	public String post(String endpoint,String contentType, Map<String,String> mapAttrs);
	

}
