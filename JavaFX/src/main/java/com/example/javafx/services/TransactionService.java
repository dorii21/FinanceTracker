package com.example.javafx.services;

import com.example.javafx.models.TransactionDTO;
import com.example.javafx.models.TransactionType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TransactionService {
    private final String BASE_URL = "http://localhost:8081/api/financetracker";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    public TransactionService(UserService userService) {
        this.userService = userService;
        objectMapper.registerModule(new JavaTimeModule());
    }

    private HttpRequest.Builder addAuth(HttpRequest.Builder builder) {
        String authHeader = userService.getAuthHeader();
        if (authHeader != null) {
            builder.header("Authorization", authHeader);
        }
        return builder;
    }

    public ObservableList<TransactionDTO> listTransactions() {
        try {
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<TransactionDTO> transactions = objectMapper.readValue(response.body(), new TypeReference<List<TransactionDTO>>() {
                });
                return FXCollections.observableArrayList(transactions);
            } else {
                System.err.println("Error: " + response.statusCode());
                return FXCollections.emptyObservableList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return FXCollections.emptyObservableList();
        }
    }

    public TransactionDTO findById(Long id) {
        try {
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/" + id))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), TransactionDTO.class);
            } else if (response.statusCode() == 404) {
                System.err.println("Not Found");
                return null;
            } else {
                System.err.println("Error: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateTransaction(TransactionDTO transactionDTO) {
        String json;
        try {
            json = objectMapper.writeValueAsString(transactionDTO);
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/" + transactionDTO.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return;
            } else if (response.statusCode() == 404) {
                throw new IOException("Not Found");
            } else throw new IOException("Error: " + response.statusCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteTransaction(TransactionDTO transactionDTO) {
        String json;
        try {
            json=objectMapper.writeValueAsString(transactionDTO);
            HttpRequest request=addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL+"/"+transactionDTO.getId()))
                    .DELETE().build();
            HttpResponse<String> response=httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return;
            }else if (response.statusCode() == 404) {
                System.err.println("Not Found");
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
