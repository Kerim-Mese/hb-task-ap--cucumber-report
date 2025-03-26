package utilities;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ReusableMethods {

    public static Response sendGetRequestToPath(String path) {
        return given()
                .spec(Base_Request.spec)
                .when()
                .get(path);
    }

    public static Response getRequestTo(String endpoint) {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }

        return given()
                .spec(Base_Request.spec)
                .when()
                .get(endpoint);
    }


   /* public static Response getRequestTo(String endpoint) {
        return given()
                .spec(Base_Request.spec)
                .when()
                .get(endpoint);
    }

    */
    public static Response getRequestWithPathParam(String endpoint, String paramName, Object paramValue) {
        return given()
                .spec(Base_Request.spec)
                .pathParam(paramName, paramValue)
                .when()
                .get(endpoint); // örnek: "/booking/{id}"
    }

    public static Response sendGetRequestWithPathParam(String paramKey, String paramValue) {
        Base_Request.spec.pathParam(paramKey, paramValue);
        return given()
                .spec(Base_Request.spec)
                .when()
                .get("/{"+paramKey+"}");
    }
    public static void verifyBookingDetails(Response response, Map<String, Object> expectedData) {
        // bookingdates altındaki tarihleri ayrı map olarak al
        Map<String, String> bookingDates = (Map<String, String>) expectedData.get("bookingdates");

        // Doğrulama
        response.then()
                .body("booking.firstname", equalTo(expectedData.get("firstname")))
                .body("booking.lastname", equalTo(expectedData.get("lastname")))
                .body("booking.totalprice", equalTo(expectedData.get("totalprice")))
                .body("booking.depositpaid", equalTo(expectedData.get("depositpaid")))
                .body("booking.additionalneeds", equalTo(expectedData.get("additionalneeds")))
                .body("booking.bookingdates.checkin", equalTo(bookingDates.get("checkin")))
                .body("booking.bookingdates.checkout", equalTo(bookingDates.get("checkout")));

        // Konsola yazdır
        System.out.println("✔ Rezervasyon doğrulandı.");
        System.out.println("Booking ID: " + response.jsonPath().getInt("bookingid"));
        System.out.println("Body: " + response.asString());
    }

    public static void verifyBookingList(Response response) {
        List<Integer> idList = response.jsonPath().getList("bookingid");

        // Liste boş değil mi kontrol et
        if (idList == null || idList.isEmpty()) {
            throw new AssertionError("Rezervasyon listesi boş geldi!");
        }

        System.out.println("✔ Rezervasyon listesi başarıyla alındı. Toplam kayıt: " + idList.size());
    }


    public static void verifyStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    public static Response sendGetRequest(String path, Map<String, String> pathParams, Map<String, String> queryParams) {
        // Path paramları ekle
        if (pathParams != null) {
            for (Map.Entry<String, String> entry : pathParams.entrySet()) {
                Base_Request.spec.pathParam(entry.getKey(), entry.getValue());
            }
        }

        // Query paramları ekle
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                Base_Request.spec.queryParam(entry.getKey(), entry.getValue());
            }
        }

        return given()
                .spec(Base_Request.spec)
                .when()
                .get(path);
    }





    public static String generateToken() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", ConfigReader.getProperty("username"));
        credentials.put("password", ConfigReader.getProperty("password"));

        return given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("https://restful-booker.herokuapp.com/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }


    public static Map<String, Object> createBookingBodyWithFaker() {
        Faker faker = new Faker();

        Map<String, Object> booking = new HashMap<>();
        Map<String, String> dates = new HashMap<>();

        String checkin = "2025-07-01";
        String checkout = "2025-07-10";

        dates.put("checkin", checkin);
        dates.put("checkout", checkout);

        booking.put("firstname", faker.name().firstName());
        booking.put("lastname", faker.name().lastName());
        booking.put("totalprice", faker.number().numberBetween(100, 1000));
        booking.put("depositpaid", faker.bool().bool());
        booking.put("bookingdates", dates);
        booking.put("additionalneeds", faker.food().dish());

        return booking;
    }

    public static Response sendPostBookingRequest(Map<String, Object> requestBody) {
        return given()
                .spec(Base_Request.spec)
                .body(requestBody)
                .when()
                .post("/booking");
    }
    public static int extractBookingId(Response response) {
        return response.jsonPath().getInt("bookingid");
    }
    public static Response updateBooking(int bookingId, Map<String, Object> updatedBookingBody, String token) {
        return given()
                .spec(Base_Request.spec)
                .contentType(ContentType.JSON)
                .cookie("token", token) // Auth gerekiyor!
                .body(updatedBookingBody)
                .pathParam("id", bookingId)
                .when()
                .put("/booking/{id}");
    }

    public static Response updateBookingPartially(int bookingId, Map<String, Object> partialUpdateBody, String token) {
        return given()
                .spec(Base_Request.spec)
                .contentType(ContentType.JSON)
                .cookie("token", token) // Auth gereklidir
                .body(partialUpdateBody) // sadece güncellenecek alan
                .pathParam("id", bookingId)
                .when()
                .patch("/booking/{id}");
    }

    public static void verifyOnlyFirstnameUpdated(Response response, Map<String, Object> originalData, Map<String, Object> updatedData) {
        // 1. Güncellenen isim verisini al
        String expectedFirstname = (String) updatedData.get("firstname");

        // 2. JSON yapısına göre path kontrol et
        String actualFirstname = response.jsonPath().getString("booking.firstname");
        if (actualFirstname == null) {
            actualFirstname = response.jsonPath().getString("firstname");
        }

        // 3. Firstname doğrulama
        if (!expectedFirstname.equals(actualFirstname)) {
            throw new AssertionError("❌ Firstname eşleşmiyor! Beklenen: " + expectedFirstname + ", Gelen: " + actualFirstname);
        }
        System.out.println("✔ Firstname güncellendi: " + actualFirstname);

        // 4. Diğer veriler değişmedi mi kontrolü (örnek: lastname)
        String expectedLastname = (String) originalData.get("lastname");

        String actualLastname = response.jsonPath().getString("booking.lastname");
        if (actualLastname == null) {
            actualLastname = response.jsonPath().getString("lastname");
        }

        if (!expectedLastname.equals(actualLastname)) {
            throw new AssertionError("❌ Lastname değişmiş! Beklenen: " + expectedLastname + ", Gelen: " + actualLastname);
        }
        System.out.println("✔ Lastname değişmedi: " + actualLastname);
    }


    public static Response patchBookingWithFirstname(int bookingId, String newFirstName, String token) {
        // Eğer bookingId boşsa, yeni rezervasyon oluştur
        if (bookingId == 0) {
            Map<String, Object> bookingData = createBookingBodyWithFaker();
            Response response = sendPostBookingRequest(bookingData);
            bookingId = extractBookingId(response);
        }

        // PATCH isteği oluşturuluyor
        Map<String, Object> partialUpdateData = new HashMap<>();
        partialUpdateData.put("firstname", newFirstName);

        return updateBookingPartially(bookingId, partialUpdateData, token);
    }
    public static Map<String, Object> extractBookingData(Response response) {
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("firstname", response.jsonPath().getString("firstname"));
        bookingData.put("lastname", response.jsonPath().getString("lastname"));
        bookingData.put("totalprice", response.jsonPath().getInt("totalprice"));
        bookingData.put("depositpaid", response.jsonPath().getBoolean("depositpaid"));
        bookingData.put("additionalneeds", response.jsonPath().getString("additionalneeds"));

        Map<String, String> bookingDates = new HashMap<>();
        bookingDates.put("checkin", response.jsonPath().getString("bookingdates.checkin"));
        bookingDates.put("checkout", response.jsonPath().getString("bookingdates.checkout"));
        bookingData.put("bookingdates", bookingDates);

        return bookingData;
    }

    public static Response deleteBooking(int bookingId, String token) {
        return given()
                .spec(Base_Request.spec)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + bookingId);
    }


















}
