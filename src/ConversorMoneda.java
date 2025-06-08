import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

public class ConversorMoneda {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/bb194823189da2c0530a3233/latest/USD";
    private static Map<String, Double> tasasDeCambio = new HashMap<>();

    public static void main(String[] args) {
        obtenerTasasDeCambio();

        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("-------------------------------------------");
            System.out.println("\nBienvenido(a) al conversor de moneda");
            System.out.println("-------------------------------------------");
            System.out.println("1) Dólar a Peso argentino");
            System.out.println("2) Peso argentino a dólar");
            System.out.println("3) Dólar a Real brasileño");
            System.out.println("4) Real brasileño a Dólar");
            System.out.println("5) Dólar a Peso colombiano");
            System.out.println("6) Peso colombiano a Dólar");
            System.out.println("7) Dolar a Peso chileno");
            System.out.println("8) Salir");
            System.out.println("-------------------------------------------");
            System.out.println("Elija una opción válida:");
            System.out.println("-------------------------------------------");

            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    convertirMoneda("USD", "ARS", scanner);
                    break;
                case 2:
                    convertirMoneda("ARS", "USD", scanner);
                    break;
                case 3:
                    convertirMoneda("USD", "BRL", scanner);
                    break;
                case 4:
                    convertirMoneda("BRL", "USD", scanner);
                    break;
                case 5:
                    convertirMoneda("USD", "COP", scanner);
                    break;
                case 6:
                    convertirMoneda("COP", "USD", scanner);
                    break;
                case 7:
                    convertirMoneda("USD", "CLP", scanner);
                    break;

                case 8:
                    System.out.println("¡Gracias por usar el conversor de moneda!");
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, elija una opción válida.");
            }
        } while (opcion != 8);

        scanner.close();
    }

    private static void obtenerTasasDeCambio() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            Map<String, Object> jsonResponse = gson.fromJson(response.toString(), Map.class);

            if ("success".equals(jsonResponse.get("result"))) {
                Map<String, Double> rates = (Map<String, Double>) jsonResponse.get("conversion_rates");
                tasasDeCambio.putAll(rates);
            } else {
                System.err.println("Error al obtener las tasas de cambio: " + jsonResponse.get("error"));
            }

            connection.disconnect();

        } catch (IOException e) {
            System.err.println("Error de conexión o al procesar la respuesta de la API: " + e.getMessage());
        }
    }

    private static void convertirMoneda(String monedaOrigen, String monedaDestino, Scanner scanner) {
        if (tasasDeCambio.containsKey(monedaDestino)) {

            System.out.printf("Ingrese el valor en %s que desee convertir: ", obtenerNombreMoneda(monedaOrigen));

            double valorOrigen = scanner.nextDouble();
            double tipoDeCambio;

            if (monedaOrigen.equals("USD")) {
                tipoDeCambio = tasasDeCambio.get(monedaDestino);
                double valorDestino = valorOrigen * tipoDeCambio;
                System.out.println("*************************************************************************");
                System.out.printf("%.2f %s equivalen a %.2f %s.\n", valorOrigen, obtenerNombreMoneda(monedaOrigen), valorDestino, obtenerNombreMoneda(monedaDestino));
                System.out.println("*************************************************************************");
            } else {
                if (tasasDeCambio.containsKey(monedaOrigen)) {
                    tipoDeCambio = 1.0 / tasasDeCambio.get(monedaOrigen); // Convertir a USD primero
                    double valorEnUSD = valorOrigen * tipoDeCambio;
                    double tipoDeCambioFinal = tasasDeCambio.get(monedaDestino);
                    double valorDestino = valorEnUSD * tipoDeCambioFinal;
                    System.out.printf("%.2f %s equivalen a %.2f %s.\n", valorOrigen, obtenerNombreMoneda(monedaOrigen), valorDestino, obtenerNombreMoneda(monedaDestino));
                } else {
                    System.out.printf("La tasa de cambio para %s no está disponible en este momento.\n", obtenerNombreMoneda(monedaOrigen));
                }
            }
        } else {
            System.out.printf("La tasa de cambio para %s no está disponible en este momento.\n", obtenerNombreMoneda(monedaDestino));
        }
    }

    private static String obtenerNombreMoneda(String codigoMoneda) {
        return switch (codigoMoneda) {
            case "USD" -> "dólares estadounidenses";
            case "ARS" -> "pesos argentinos";
            case "BRL" -> "reales brasileños";
            case "COP" -> "pesos colombianos";
            case "CLP" -> "pesos chilenos";
            default -> codigoMoneda;
        };
    }
}