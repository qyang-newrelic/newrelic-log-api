package logapi;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap; 
import java.util.Map; 
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Date;


public class nrLogClient {
  private String httpEndpoint = "https://log-api.newrelic.com/log/v1";
  private String nrLicenseKey;
  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  OkHttpClient client = new OkHttpClient();
  public void nrLogClient(String licenseKey) 
  {
      nrLicenseKey = licenseKey; 
  }

  public String post(String json) throws IOException 
	{
    RequestBody body = RequestBody.create(JSON, json);
    Request request = new Request.Builder()
        .addHeader("X-License-Key",nrLicenseKey)
        .url(httpEndpoint)
        .post(body)
        .build();
    try (Response response = client.newCall(request).execute()) { 
			return response.body().string(); 
			}
  }
  
  public String sendSample(String message) 
  {
      String[] msgs = new String[1];
      msgs[0] = message;
      Map <String,String> attrs = new HashMap();
      attrs.put("service","testme");
      attrs.put("hostname","in-the-cloud");
      return sendLogs(msgs, attrs);
      
  }

  public static boolean isJSONValid(String jsonInString ) {
    try {
       final ObjectMapper mapper = new ObjectMapper();
       mapper.readTree(jsonInString);
       return true;
    } catch (IOException e) {
       return false;
    }
  }

  public String sendLogs(String[] messages, Map<String,String> attributes) 
  {
      // detail json format
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode block1 = mapper.createObjectNode();
    ArrayNode rootNode = mapper.createArrayNode();
    ArrayNode logs = mapper.createArrayNode();
    ObjectNode common = mapper.createObjectNode();
    Date date = new Date(); 
    String ts = "test me"; //date.getTime()
   /* 
    for (Map.Entry<String,String> entry : attributes.entrySet()) {
        System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
    */
	  //attributes.forEach((k,v) -> common.put(entry.getKey(),entry.getValue()));
	  attributes.forEach((k,v) -> common.put(k,v));

    for (String msg : messages) {
        ObjectNode row = mapper.createObjectNode();
    		try {
       		row = (ObjectNode) mapper.readTree(msg);
    		} catch (IOException e) {
					// not a json
          //row.put("time", ts);
        	row.put("message", msg);
    		}
        logs.add(row);
    }

    block1.put("common", common);
    block1.put("logs",logs);
    rootNode.add(block1);
    
    String jsonString =  rootNode.toString();
    System.out.println(jsonString);
    return jsonString;
  }

  public void setEndpoint(String newEndPoint) 
  {
      httpEndpoint = newEndPoint; 
  } 

  public void setLicenseKey(String newLicenseKey)
  {
      nrLicenseKey = newLicenseKey;
  }

  public static void main(String[] args) throws IOException {
    nrLogClient client = new nrLogClient();
		client.setLicenseKey("ec9772a11dd7d654d71d35c86f6f168ae99e4e05");

    String myStr = "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
    myStr = "testme";

    String json = client.sendSample(myStr);
    String response = client.post(json);
    System.out.println(response);


  }

	public String getGreeting() {
		return "success";
	}
}


