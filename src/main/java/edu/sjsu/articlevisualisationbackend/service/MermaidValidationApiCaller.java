package edu.sjsu.articlevisualisationbackend.service;


import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class MermaidValidationApiCaller {

    HttpPost postRequest;
    String mermaidCode;

    final String URL = "https://d1doi45x0nyjfu.cloudfront.net/validate-mermaid";

    public MermaidValidationApiCaller(String mermaidCode) {

        this.mermaidCode = mermaidCode;
        this.initHttp();
    }

    private void initHttp(){
        this.postRequest = new HttpPost(this.URL);
        StringEntity input = new StringEntity(
                this.jsonBody(), "UTF-8"
        );
        input.setContentType("application/json");
        this.postRequest.setEntity(input);
    }

    private String jsonBody(){
        final String JSON_KEY = "mermaidCode";
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_KEY, this.mermaidCode);

        return jsonObject.toString();
    }

    private boolean readResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity, "UTF-8");

        final JSONObject jsonResponse = new JSONObject(responseBody);
        return jsonResponse.getBoolean("isValid");
    }

    public boolean validate(){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(this.postRequest);
            int statusCode = response.getStatusLine().getStatusCode();


            if (statusCode == 200) {
                return this.readResponse(response);
            } else {
                throw new Exception("Unknown error" + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
