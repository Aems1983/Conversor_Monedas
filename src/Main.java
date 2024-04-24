import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String API_KEY = "cffca1f0d9146636486bc95b";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    // Lista para almacenar el historial de conversiones
    private static List<String> conversionHistory = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Consumo de la API
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        // Verificar el código de estado de la respuesta
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            System.out.println("La solicitud fue exitosa.");
        } else {
            System.out.println("Hubo un error con la solicitud. Código de estado: " + statusCode);
        }

        // Imprimir los encabezados de la respuesta
        System.out.println("Encabezados de la respuesta:");
        response.headers().map().forEach((k, v) -> System.out.println(k + ": " + String.join(", ", v)));

        // Obtener el cuerpo de la respuesta
        String content = response.body();
        System.out.println("Cuerpo de la respuesta:");
        System.out.println(content);

        // Análisis de la respuesta JSON
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(content).getAsJsonObject();
        JsonObject rates = json.getAsJsonObject("conversion_rates");

        // Filtro de monedas
        double arsRate = rates.get("ARS").getAsDouble();
        double bobRate = rates.get("BOB").getAsDouble();
        double brlRate = rates.get("BRL").getAsDouble();
        double clpRate = rates.get("CLP").getAsDouble();
        double copRate = rates.get("COP").getAsDouble();
        double usdRate = rates.get("USD").getAsDouble();
        double penRate = rates.get("PEN").getAsDouble();
        double uyuRate = rates.get("UYU").getAsDouble();
        double pygRate = rates.get("PYG").getAsDouble();
        double vesRate = rates.get("VES").getAsDouble();

        // Solicita al usuario la cantidad de dinero y la moneda de origen y destino
        Scanner scanner = new Scanner(System.in);
        boolean continueConverting = true;
        while (continueConverting) {
            System.out.println("Ingrese la cantidad de dinero que desea convertir:");
            double amount = scanner.nextDouble();

            System.out.println("Seleccione la moneda de origen:");
            displayCurrencyOptions();
            String fromCurrency = getCurrencyCode(scanner);

            System.out.println("Seleccione la moneda de destino:");
            displayCurrencyOptions();
            String toCurrency = getCurrencyCode(scanner);


            // Calcular el valor convertido y mostrar el resultado
            double rate = getRate(fromCurrency, arsRate, bobRate, brlRate, clpRate, copRate, usdRate, penRate, uyuRate, pygRate, vesRate);
            long convertedAmount = convertAmount(amount, rate);
            System.out.println(Math.round(amount) + " " + fromCurrency + " es igual a " + convertedAmount + " " + toCurrency);

            // Agregar la conversión al historial
            String timestamp = LocalDateTime.now().toString();
            String conversionRecord = timestamp + ": " + Math.round(amount) + " " + fromCurrency + " es igual a " + convertedAmount + " " + toCurrency;
            conversionHistory.add(conversionRecord);

            System.out.println("¿Desea realizar otra conversión? Ingrese 's' para sí, cualquier otra tecla para no.");
            String userResponse = scanner.next();
            if (!userResponse.equalsIgnoreCase("s")) {
                continueConverting = false;
            }
        }

        // Mostrar el historial de conversiones
        System.out.println("Historial de conversiones:");
        for (String record : conversionHistory) {
            System.out.println(record);
        }
    }

    private static void displayCurrencyOptions() {
        System.out.println(" 1. ARS - Peso Argentino");
        System.out.println(" 2. BOB - Boliviano");
        System.out.println(" 3. BRL - Real Brasileño");
        System.out.println(" 4. CLP - Peso Chileno");
        System.out.println(" 5. COP - Peso Colombiano");
        System.out.println(" 6. USD - Dólar Estadounidense");
        System.out.println(" 7. PEN - Sol Peruano");
        System.out.println(" 8. UYU - Peso Uruguayo");
        System.out.println(" 9. PYG - Guaraní Paraguayo");
        System.out.println("10. VES - Bolívar Soberano Venezolano");
    }

    private static String getCurrencyCode(Scanner scanner) {
        int option;
        do {
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    return "ARS";
                case 2:
                    return "BOB";
                case 3:
                    return "BRL";
                case 4:
                    return "CLP";
                case 5:
                    return "COP";
                case 6:
                    return "USD";
                case 7:
                    return "PEN";
                case 8:
                    return "UYU";
                case 9:
                    return "PYG";
                case 10:
                    return "VES";
                default:
                    System.out.println("Opción no soportada. Por favor, intente de nuevo.");
                    displayCurrencyOptions();
            }
        } while (true);
    }

    private static double getRate(String currency, double arsRate, double bobRate, double brlRate, double clpRate, double copRate, double usdRate, double penRate, double uyuRate, double pygRate, double vesRate) {
        switch (currency) {
            case "ARS":
                return arsRate;
            case "BOB":
                return bobRate;
            case "BRL":
                return brlRate;
            case "CLP":
                return clpRate;
            case "COP":
                return copRate;
            case "USD":
                return usdRate;
            case "PEN":
                return penRate;
            case "UYU":
                return uyuRate;
            case "PYG":
                return pygRate;
            case "VES":
                return vesRate;
            default:
                throw new IllegalArgumentException("Moneda no soportada: " + currency);
        }
    }

    private static long convertAmount(double amount, double rate) {
        return Math.round(amount * rate);
    }
}