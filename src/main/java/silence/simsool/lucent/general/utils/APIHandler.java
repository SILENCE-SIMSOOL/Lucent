package silence.simsool.lucent.general.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.utils.useful.UChat;

public class APIHandler {

	private static final HttpClient CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	public static String getResponse(String url) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("User-Agent", "LUCENT/" + Lucent.VERSION)
					.GET()
					.build();

			HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) return response.body();
			else UChat.chat("§f[§bLucent§f] §cRequest failed. HTTP Code: " + response.statusCode() + " §7(" + url + ")");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ERROR";
	}

	public static JsonObject getResponsePOST(String url, JsonObject body) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(body.toString()))
					.build();

			HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			return JsonParser.parseString(response.body()).getAsJsonObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JsonObject();
	}

	public static JsonObject getObjectResponse(String url) {
		String response = getResponse(url);
		if (response == null || response.equals("ERROR")) return null;
		try {
			return JsonParser.parseString(response).getAsJsonObject();
		} catch (JsonIOException e) {
			return null;
		}
	}

	public static JsonArray getArrayResponse(String url) {
		String response = getResponse(url);
		if (response == null || response.equals("ERROR")) return new JsonArray();
		try {
			return JsonParser.parseString(response).getAsJsonArray();
		} catch (Exception e) {
			return new JsonArray();
		}
	}

	public static String getUUIDFromName(String name) {
		JsonObject obj = getObjectResponse("https://api.mojang.com/users/profiles/minecraft/" + name);
		return (obj != null && obj.has("id")) ? obj.get("id").getAsString() : null;
	}

	public static Pair<String, String> getUUIDAndName(String name) {
		JsonObject obj = getObjectResponse("https://api.mojang.com/users/profiles/minecraft/" + name);
		if (obj == null || !obj.has("id") || !obj.has("name")) return null;
		return new Pair<>(obj.get("name").getAsString(), obj.get("id").getAsString());
	}

}