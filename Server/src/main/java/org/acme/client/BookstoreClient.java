package org.acme.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class BookstoreClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        System.out.print("Introdu ID-ul Casei de Marcat (ex: 1): ");
        String registerId = scanner.nextLine();

        while (true) {
            System.out.println("\n--- CASA DE MARCAT " + registerId + " ---");
            System.out.println("Introdu ID-urile cartilor separate prin virgula (ex: 1,2,3) sau 'REPORT' pentru raport global:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("EXIT")) break;

            if (input.equalsIgnoreCase("REPORT")) {
                // Cerere pentru Raport Global (Nota 10)
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/sales/report"))
                        .GET()
                        .build();
                sendRequest(client, request);
            } else {
                // Creare vanzare
                String jsonBody = String.format(
                        "{\"registerId\": %s, \"items\": [%s]}",
                        registerId,
                        formatItems(input)
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/sales"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                sendRequest(client, request);
            }
        }
    }

    private static String formatItems(String input) {
        // Transform "1,2" -> "{ \"bookId\": 1, \"quantity\": 1 }, { \"bookId\": 2, \"quantity\": 1 }"
        String[] ids = input.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            sb.append(String.format("{ \"bookId\": %s, \"quantity\": 1 }", ids[i].trim()));
            if (i < ids.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    private static void sendRequest(HttpClient client, HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Raspuns Server: " + response.body());
        } catch (Exception e) {
            System.out.println("Eroare conexiune: " + e.getMessage());
        }
    }
}