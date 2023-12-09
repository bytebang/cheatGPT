package at.htlle.cheatgpt.bo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.core.env.Environment;

public class Ollama implements Savant 
{

	Environment env;
	
	URL endpoint;
	public Ollama(String backendUrl, Environment env) 
	{
		this.env = env;
		
		try {
			this.endpoint = new URL("http://" + backendUrl + "/api/generate");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Answer ask(String question) 
	{
		try {
            HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            
            JSONObject requestObject = new JSONObject();
            requestObject.put("prompt", question);
            
            requestObject.put("system", env.getProperty("ai.systemprompt"));
            requestObject.put("model", env.getProperty("ai.model"));
            requestObject.put("options", new JSONObject(env.getProperty("ai.options")));
            requestObject.put("raw", true);
            requestObject.put("stream", false);
            requestObject.put("format", "json");
            
            String input = requestObject.toString();
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(input.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            StringBuilder responseText = new StringBuilder();
            String output;
            JSONObject jsonObject = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())))) {
   
                while ((output = br.readLine()) != null) {
                    jsonObject = new JSONObject(output);
                    if(jsonObject.has("response")) {
                        responseText.append(jsonObject.getString("response"));
                    }
                }
            }

            conn.disconnect();
            return new Answer(responseText.toString(), jsonObject.getInt("eval_count"));

        } catch (Exception e) {
            
        	return new Answer("Internal Error: " + e.getMessage(), 0);
        }

	}

}
