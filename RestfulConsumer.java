
package com.example.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;


public class RestfulConsumer {
	private String baseURI;
	//private String format;
	private String authHash;

	//getters and setters
	public void setAuthHash(String authHash) {
		this.authHash = authHash;
	}

	public String getAuthHash() {
		return authHash;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	public String getBaseURI() {
		return baseURI;
	}

	/*public void setFormat(String format) {
		this.format = format;
	}
	public String getFormat() {
		String result;
		if (format == null) {
			result = "";
		} else {
			result = "." + format;
		}
		return result;
	} */

	public String formatGetParams(ArrayList<NameValuePair> params) throws UnsupportedEncodingException {
		String combinedParams = "";
        if(!params.isEmpty()){
            combinedParams += "?";
            for(NameValuePair p : params)
            {
                String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                if(combinedParams.length() > 1)
                {
                    combinedParams  +=  "&" + paramString;
                }
                else
                {
                    combinedParams += paramString;
                }
            }
        }
		return combinedParams;
	}

	public String buildPostUrl(String path) {
		return this.getBaseURI() + path;
	}

	public String buildGetUrl(String path, ArrayList <NameValuePair> params) throws Exception {
		return this.getBaseURI() + path + formatGetParams(params);
	}

	public String get(String path, ArrayList <NameValuePair> params) throws Exception {
		String url = buildGetUrl(path, params);
		HttpGet request = new HttpGet(url);
		if (getAuthHash() != null) {
			request.setHeader("Authorization", "Basic "+ getAuthHash());
		}
		String response = executeRequest(request, url).get(0);

		return response;
	}


	public String post(String path, ArrayList <NameValuePair> params) throws Exception {
        String url = buildPostUrl(path);
        HttpPost request = new HttpPost(url);
		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
		if (getAuthHash() != null) {
			request.setHeader("Authorization", "Basic "+ getAuthHash());
		}
		String response = executeRequest(request, url).get(0);

		return response;
	}

	private ArrayList<String> executeRequest(HttpUriRequest request, String url)
    {
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;
        String response = "";
        int responseCode = 0;
        String message = "";

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();

            }

        } catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }

        ArrayList <String> result = new ArrayList<String>();
        result.add(response);
        result.add(String.valueOf(responseCode));
        result.add(message);
		return result;
    }

	private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
