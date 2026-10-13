import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTest {
    public static void main(String[] args) throws Exception {
        String json = "{\n" +
                "  \"sourceAccountNumber\": \"9546277977\",\n" +
                "  \"destinationAccountNumber\": \"3597671072\",\n" +
                "  \"amount\": 200,\n" +
                "  \"description\": \"Chuyển Khoản\"\n" +
                "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/api/v1/transactions/transfer"))
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6ImRhbmc0ODY5MTIxNkBnbWFpbC5jb20iLCJ1c2VybmFtZSI6ImRhbmc0ODY5Iiwic3ViIjoiMSIsImlhdCI6MTc5MDIxNTg3OSwiZXhwIjoxNzkwMjE2Nzc5fQ.A9-JjpfKrFh_rbDmkFqx9PSIJ1iyGflQGM7GvXnhCZZKTZEf6rbOXz3bUhHLzDlAUNCnszYSNpvIbcecS0FSCg")
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }
}
