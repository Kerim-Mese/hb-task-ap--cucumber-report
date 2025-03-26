package stepdefinations;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import utilities.Base_Request;
import utilities.ReusableMethods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static utilities.Base_Request.spec;

public class StepDefinitions {

    Response response;
    Map<String, Object> bookingData;
    Map<String, Object> updatedBookingData;
    Map<String, Object> partialUpdateData;
    int bookingId;




    @Given("Kullanıcı herokuapp API sitesine gider")
    public void kullanıcı_herokuapp_apı_sitesine_gider() {
        response=ReusableMethods.getRequestTo("/booking");


    }

    @Then("API {int} status kodu dönmelidir")
    public void apı_status_kodu_dönmelidir(Integer int1) {
        ReusableMethods.verifyStatusCode(response,200);

        System.out.println("Kullanıcı basarılı bir şekilde siteye ulaştı");

    }
    @When("Kullanıcı geçerli kullanıcı adı ve şifre ile token oluşturur")
    public void kullanıcı_geçerli_kullanıcı_adı_ve_şifre_ile_token_oluşturur() {
        Base_Request.token= ReusableMethods.generateToken();


    }
    @Then("API {int} status kodu ile token dönmelidir")
    public void apı_status_kodu_ile_token_dönmelidir(Integer int1) {
       ReusableMethods.verifyStatusCode(response,200);
        System.out.println("Giris yapıldı");
    }
    @When("Kullanıcı geçerli bilgilerle yeni bir rezervasyon oluşturur")
    public void kullanıcı_geçerli_bilgilerle_yeni_bir_rezervasyon_oluşturur() {
        bookingData=ReusableMethods.createBookingBodyWithFaker();
        response=ReusableMethods.sendPostBookingRequest(bookingData);
        bookingId = ReusableMethods.extractBookingId(response);
        System.out.println("bookingData = " + bookingData);

    }
    @Then("API {int} status kodu ve rezervasyon bilgisi dönmelidir")
    public void apı_status_kodu_ve_rezervasyon_bilgisi_dönmelidir(Integer int1) {
        ReusableMethods.verifyStatusCode(response,200);
        ReusableMethods.verifyBookingDetails(response,bookingData);

    }
    @When("Manager tüm rezervasyonları görüntülemek için istek gönderir")
    public void kullanıcı_tüm_rezervasyonları_görüntülemek_için_istek_gönderir() {
        response = ReusableMethods.getRequestTo("/booking");
        //response.prettyPrint();
        System.out.println("Body (inline): " + response.asString());


    }
    @Then("API {int} status kodu ile rezervasyon listesi dönmelidir")
    public void apı_status_kodu_ile_rezervasyon_listesi_dönmelidir(Integer int1) {
        ReusableMethods.verifyStatusCode(response,200);
        ReusableMethods.verifyBookingList(response);
        List<Integer> ids = response.jsonPath().getList("bookingid");
        System.out.println("Rezervasyon ID'leri: " + ids);

    }
    @When("Kullanıcı belirli bir rezervasyon ID'si ile rezervasyon detaylarını getirir")
    public void kullanıcı_belirli_bir_rezervasyon_ıd_si_ile_rezervasyon_detaylarını_getirir() {
        bookingData=ReusableMethods.createBookingBodyWithFaker();
        response=ReusableMethods.sendPostBookingRequest(bookingData);
        bookingId=ReusableMethods.extractBookingId(response);



    }
    @Then("API {int} status kodu ile ilgili rezervasyon bilgisi dönmelidir")
    public void apı_status_kodu_ile_ilgili_rezervasyon_bilgisi_dönmelidir(Integer int1) {
        ReusableMethods.verifyStatusCode(response,200);

        response.prettyPrint();

    }
    @When("Kullanıcı mevcut bir rezervasyonu PUT isteği ile tamamen günceller")
    public void kullanıcı_mevcut_bir_rezervasyonu_put_isteği_ile_tamamen_günceller() {
        updatedBookingData = ReusableMethods.createBookingBodyWithFaker(); // Yeni veri
        response = ReusableMethods.updateBooking(bookingId, updatedBookingData, Base_Request.token);
        System.out.println("PUT sonrası response:");
        response.prettyPrint();


    }
    @Then("API {int} status kodu ile güncellenen bilgiler dönmelidir")
    public void apı_status_kodu_ile_güncellenen_bilgiler_dönmelidir(Integer int1) {
        ReusableMethods.verifyStatusCode(response,200);


    }
    @When("Kullanıcı mevcut bir rezervasyonu PATCH isteği ile kısmen günceller \\(sadece isim)")
    public void kullanıcı_mevcut_bir_rezervasyonu_patch_isteği_ile_kısmen_günceller_sadece_isim() {
        if (response == null || bookingId == 0) {
            bookingData = ReusableMethods.createBookingBodyWithFaker();
            response = ReusableMethods.sendPostBookingRequest(bookingData);
            bookingId = ReusableMethods.extractBookingId(response);
        }

        // PATCH öncesi mevcut veriyi GET ile al → Doğrulamada kıyas için kullanacağız
        Response originalResponse = ReusableMethods.getRequestWithPathParam("/booking/{id}", "id", bookingId);
        bookingData = ReusableMethods.extractBookingData(originalResponse); // 👈 Bu metodu şimdi aşağıda vereceğim

        partialUpdateData = new HashMap<>();
        partialUpdateData.put("firstname", "YeniAd");

        response = ReusableMethods.updateBookingPartially(bookingId, partialUpdateData, Base_Request.token);



    }
    @Then("API {int} status kodu ile yalnızca ismin değiştiği doğrulanmalıdır")
    public void apı_status_kodu_ile_yalnızca_ismin_değiştiği_doğrulanmalıdır(Integer int1) {
        ReusableMethods.verifyStatusCode(response,200);
        ReusableMethods.verifyOnlyFirstnameUpdated(response,bookingData,partialUpdateData);

    }
    @When("Kullanıcı bir rezervasyonu siler")
    public void kullanıcı_bir_rezervasyonu_siler() {
        response = ReusableMethods.deleteBooking(bookingId, Base_Request.token);
        System.out.println("Silme işlemi yanıtı:");
        response.prettyPrint();

    }
    @Then("API {int} status kodu ile silme işleminin başarılı olduğu doğrulanmalıdır")
    public void apı_status_kodu_ile_silme_işleminin_başarılı_olduğu_doğrulanmalıdır(Integer int1) {
        Response getResponse = ReusableMethods.getRequestTo("/booking/" + bookingId);
        assert getResponse.getStatusCode() == 404 : "❌ Silinen rezervasyon hala erişilebiliyor!";
        System.out.println("✔ Silinen rezervasyon artık erişilemez.");

    }


}
