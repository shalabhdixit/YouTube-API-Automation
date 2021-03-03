package com.xxxx.Scripts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONArray;
import org.json.JSONObject;
import com.xxxx.Support.ConfiguratorSupport;

public class ValidateVideos {

	static String totalResults;
	static String videoID = "4qr2wvqEjmY";
	static String apiKey = ConfiguratorSupport.getProperty("apiKEY");

	public static void main(String args[]) {
        // Proxy settings if applicable
		System.getProperties().put("http.proxyHost", "xxx.example.com");
		System.getProperties().put("http.proxyPort", "xxx");
		System.getProperties().put("http.proxyUser", "xxx");
		System.getProperties().put("http.proxyPassword", "xxx");
		System.getProperties().put("https.proxyHost", "xxx.example.com");
		System.getProperties().put("https.proxyPort", "xxx");
		System.getProperties().put("https.proxyUser", "xxx");
		System.getProperties().put("https.proxyPassword", "xxx");

		InputStreamReader input = null;
		StringBuilder strBuilder = new StringBuilder();

		String linkUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id="
				+ videoID + "&key=" + apiKey;

		try {

			URL url = new URL(linkUrl);

			HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			httpURLConnect.setRequestProperty("cache-control", "no-cache");
			httpURLConnect.setRequestProperty("if-none-match", "no-cache");
			httpURLConnect.setRequestMethod("GET");
			httpURLConnect.setConnectTimeout(20000);
			httpURLConnect.setRequestProperty("Content-Type", "application/json");
			httpURLConnect.setRequestProperty("charset", "utf-8");
			httpURLConnect.setUseCaches(false);

			httpURLConnect.connect();
			System.out.println("Response Code: " + httpURLConnect.getResponseCode());

			if (httpURLConnect.getResponseCode() == 200) {
				System.out.println("Response Message: " + httpURLConnect.getResponseMessage().toString());
				System.out.println(httpURLConnect.getInputStream());
				input = new InputStreamReader(httpURLConnect.getInputStream(), Charset.defaultCharset());

				BufferedReader buffReader = new BufferedReader(input);
				if (buffReader != null) {
					int cp;
					while ((cp = buffReader.read()) != -1) {
						strBuilder.append((char) cp);
					}
					final JSONObject obj = new JSONObject(strBuilder.toString());
					totalResults = obj.getString("pageInfo");
					System.out.println("Total Results - " + totalResults);

					final JSONArray videoAllDetails = obj.getJSONArray("items");
					if (videoAllDetails != null) {
						final int n = videoAllDetails.length();
						for (int i = 0; i < n; ++i) {
							final JSONObject videoContent = videoAllDetails.getJSONObject(i);
							System.out.println("\nVideo ID - " + videoContent.getString("id"));
							System.out.println("\nVideo Details - " + videoContent.getString("snippet"));
						}
						String words[] = totalResults.split(",");
						System.out.println("Displaying Splitted Contents..");
						for (String w : words) {
							w = w.replaceAll("\\W", "");
							if (w.contains("totalResults")) {
								int resultCount = Integer.parseInt(w.substring(w.length() - 1));
								if (resultCount > 0) {
									System.out.println("\nVideo with ID: " + videoID + " is valid");
									// logStatement.log(LogStatus.PASS, "Video with ID: " + videoID + " is valid");
								} else {
									System.out.println("\nVideo with ID: " + videoID + " is NOT valid");
									// logStatement.log(LogStatus.FAIL,
									// "Video with ID: " + videoID + " is InValid or does not exist anymore");
								}
								System.out.println("Total Results Count: " + resultCount);
								break;
							}
						}
						//System.out.println("Updated Total Results: " + totalResults);
						buffReader.close();
					}
				}
			}
			input.close();
			httpURLConnect.disconnect();

		} catch (Exception e) {
			System.out.println("Response code: " + e.getMessage() + " " + linkUrl);
		}
		System.out.println(strBuilder.toString());
	}
}