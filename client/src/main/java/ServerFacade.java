import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ResponseException;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
  private static final ServerFacade instance = new ServerFacade();
  private String authToken = "";

  public static ServerFacade getInstance() {
    return instance;
  }

  private static void writeBody(Object request, HttpURLConnection http) throws IOException {
    if (request != null) {
      http.addRequestProperty("Content-Type", "application/json");
      String reqData = new Gson().toJson(request);
      try (OutputStream reqBody = http.getOutputStream()) {
        reqBody.write(reqData.getBytes());
      }
    }
  }

  private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
    T response = null;
    if (http.getContentLength() < 0) {
      try (InputStream respBody = http.getInputStream()) {
        InputStreamReader reader = new InputStreamReader(respBody);
        if (responseClass != null) {
          response = new Gson().fromJson(reader, responseClass);
        }
      }
    }
    return response;
  }

  public boolean login(String username, String password) {
    try {
      var loginRequest = new LoginRequest(username, password);
      var response = this.makeRequest("POST", "/session", loginRequest, LoginResult.class);

      authToken = response.authToken();

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean register(String username, String password, String email) {
    try {
      var registerRequest = new UserData(username, password, email);
      this.makeRequest("POST", "/user", registerRequest, UserData.class);

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean create(String gameId) {
    try {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("gameName", gameId);

      this.makeRequest("POST", "/game", jsonObject, JsonObject.class);

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
    try {
      URL url = (new URI("http://localhost:1234" + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);
      http.setDoOutput(true);

      if (!authToken.isEmpty()) {
        http.addRequestProperty("Authorization", authToken);
      }

      writeBody(request, http);
      http.connect();
      throwIfNotSuccessful(http);
      return readBody(http, responseClass);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
    var status = http.getResponseCode();
    if (!isSuccessful(status)) {
      throw new ResponseException(status, "failure: " + status);
    }
  }

  private boolean isSuccessful(int status) {
    return status / 100 == 2;
  }
}
