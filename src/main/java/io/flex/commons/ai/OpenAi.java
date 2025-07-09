package io.flex.commons.ai;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
 
import io.flex.FleX.Task;
 
public class OpenAi {
 
	private HttpURLConnection connection;
 
	public OpenAi(String key) {
 
		Task.print("OpenAi", "Opening ai connection.");
 
		try {
 
            URI uri = new URI("https://api.openai.com", "/v1/chat/completions", null);
            URL url = uri.toURL();
 
            this.connection = (HttpURLConnection) url.openConnection();
 
            this.connection.setRequestMethod("POST");
            this.connection.setRequestProperty("Authorization", "Bearer " + key);
            this.connection.setRequestProperty("Content-Type", "application/json");
            this.connection.setDoOutput(true);
 
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
 
        	if (this.connection != null)
        		this.connection.disconnect();
 
		}
 
	}
 
	public String getResponse(String request) throws IOException {
 
		String json = "\"{\"model\": \"gpt-3.5-turbo\",\"messages\": [{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}, {\"role\": \"user\", \"content\": \"" + request + "\"} ]}\"";
 
        try (OutputStream os = this.connection.getOutputStream()) {
 
            byte[] input = json.getBytes("utf-8");
 
            os.write(input, 0, input.length);
 
        }
        
        int status = this.connection.getResponseCode();
        
        InputStream responseStream = (status < 400)
            ? this.connection.getInputStream()
            : this.connection.getErrorStream();
 
        StringBuilder response = new StringBuilder();
 
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
 
            String line;
 
            while ((line = br.readLine()) != null)
                response.append(line.trim());
 
        }
 
        return response.toString();
 
	}
 
}