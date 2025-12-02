package com.example.javafx.services;

import com.example.javafx.models.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class UserService {
    private final String BASE_URL = "http://localhost:8081/api";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private String basicAuthHeader = null;
    private UserDTO currentUser = null;

    public boolean register(UserDTO user) {
        try {
            String json = mapper.writeValueAsString(user);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String email, String password) {
        String auth = email + ":" + password;
        this.basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/financetracker"))
                    .header("Authorization", basicAuthHeader)
                    .GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                this.currentUser = new UserDTO(email, null, null, null);
                return true;
            } else {
                this.basicAuthHeader = null;
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.basicAuthHeader = null;
            return false;
        }
    }

    public String getAuthHeader() {
        return this.basicAuthHeader;
    }
}
