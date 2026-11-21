package jm.edu.utech.helpdesk.service;
//Uses inbuilt httpclient to for the connection of the javafx client to the REST services
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.exception.ServerConnectionException;
import jm.edu.utech.helpdesk.util.ErrorHandler;

public class ApiClient {

	//server connecting to with endpoint
	private static final String BASE_URL = "http://localhost:8080/api";

	//connect timeout so a dead server does not hang the screen or the background refresh
	private static final HttpClient client = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(3))
			.build();

	//converts objects to and from JSON like in Guided Lab 5
	//ignores any extra field the server sends that the client's records don't have
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private ApiClient() {}

	//sends the request and gives back the response when the server says OK (2xx)
	//no Alerts here, this can run on a background thread. Problems are thrown and the controller
	//catches them and calls ErrorHandler.handle(e) on the JavaFX thread
	public static ApiResponse send(String method, String path, Object body) {
		try {
			HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(BASE_URL + path)).timeout(Duration.ofSeconds(5)).header("Content-Type", "application/json");

			if (body == null) {
				builder.method(method, HttpRequest.BodyPublishers.noBody());
			} else {
				builder.method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
			}

			HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
			ApiResponse apiResponse = new ApiResponse(response.statusCode(), response.body());

			//not 2xx, turn the server's JSON error into the matching exception eg ticket not found or duplicate user)
			if (!apiResponse.isOk()) {
				throw ErrorHandler.fromHttpError(apiResponse.getStatus(), apiResponse.getBody());
			}
			return apiResponse;

		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new ServerConnectionException("Request was interrupted", exception);
		} catch (IOException exception) {
			//server is off, wrong address or it timed out
			throw new ServerConnectionException("Unable to reach the server", exception);
		}
	}

	//turns the JSON body into one object, like readValue in Guided Lab 5
	public static <T> T read(ApiResponse response, Class<T> type) {
		try {
			return objectMapper.readValue(response.getBody(), type);
		} catch (IOException exception) {
			throw new HelpDeskClientException("The server sent a response the client could not read", exception);
		}
	}

	//turns the JSON body into a list, like new TypeReference<List<Book>>() {} in Guided Lab 5
	public static <T> T read(ApiResponse response, TypeReference<T> type) {
		try {
			return objectMapper.readValue(response.getBody(), type);
		} catch (IOException exception) {
			throw new HelpDeskClientException("The server sent a response the client could not read", exception);
		}
	}
}
