package com.example.javafx.services;

import com.example.javafx.models.Category;
import com.example.javafx.models.TransactionDTO;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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
                List<TransactionDTO> transactions = objectMapper.readValue(response.body(), new TypeReference<>() {
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

    public boolean csvExport(List<TransactionDTO> transactions) {
        String filename = "transactions.csv";
        Path file = Paths.get(filename);
        String json;
        try {
            json = objectMapper.writeValueAsString(transactions);
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/csv"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                Files.write(file, response.body());
                return true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteTransaction(TransactionDTO transactionDTO) {
        try {
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/" + transactionDTO.getId()))
                    .DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return;
            } else if (response.statusCode() == 404) {
                System.err.println("Not Found");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createTransaction(TransactionDTO transactionDTO) {
        String json;
        try {
            json = objectMapper.writeValueAsString(transactionDTO);
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create((BASE_URL)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return;
            } else if (response.statusCode() == 404) {
                System.err.println("Not Found");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<TransactionDTO> filterByCategory(Category category) {
        try {
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/filter/category?category=" + category.name()))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<TransactionDTO> transactions = objectMapper.readValue(response.body(), new TypeReference<>() {
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

    public ObservableList<TransactionDTO> filterByAmount(Long min, Long max) {
        try {
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/filter/amount?min=" + min + "&max=" + max))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<TransactionDTO> transactions = objectMapper.readValue(response.body(), new TypeReference<>() {
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

    public ObservableList<TransactionDTO> filterByDate(LocalDate min, LocalDate max) {
        try {
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/filter/date?min=" + min + "&max=" + max))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<TransactionDTO> transactions = objectMapper.readValue(response.body(), new TypeReference<>() {
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

    public ObservableList<TransactionDTO> filterByIncome() {
        try {
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/filter/income"))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<TransactionDTO> transactions = objectMapper.readValue(response.body(), new TypeReference<>() {
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

    public ObservableList<TransactionDTO> filterByExpense() {
        try {
            HttpRequest request = addAuth(HttpRequest.newBuilder())
                    .uri(URI.create(BASE_URL + "/filter/expenses"))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<TransactionDTO> transactions = objectMapper.readValue(response.body(), new TypeReference<>() {
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
}
