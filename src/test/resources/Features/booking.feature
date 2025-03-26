@US001
Feature: Kullanıcı restful-booker uygulamasında API fonksiyonlarını test eder

  Scenario: Kullanıcı herokuapp sitesine gider ve tüm API çağrılarını test eder
    Given Kullanıcı herokuapp API sitesine gider
   # When Kullanıcı sunucunun çalışıp çalışmadığını kontrol eder (ping endpoint)
    Then API 200 status kodu dönmelidir

    When Kullanıcı geçerli kullanıcı adı ve şifre ile token oluşturur
    Then API 200 status kodu ile token dönmelidir

   When Kullanıcı geçerli bilgilerle yeni bir rezervasyon oluşturur
   Then API 200 status kodu ve rezervasyon bilgisi dönmelidir

    When Manager tüm rezervasyonları görüntülemek için istek gönderir
    Then API 200 status kodu ile rezervasyon listesi dönmelidir

    When Kullanıcı belirli bir rezervasyon ID'si ile rezervasyon detaylarını getirir
    Then API 200 status kodu ile ilgili rezervasyon bilgisi dönmelidir

    When Kullanıcı mevcut bir rezervasyonu PUT isteği ile tamamen günceller
    Then API 200 status kodu ile güncellenen bilgiler dönmelidir

    When Kullanıcı mevcut bir rezervasyonu PATCH isteği ile kısmen günceller (sadece isim)
    Then API 200 status kodu ile yalnızca ismin değiştiği doğrulanmalıdır

    When Kullanıcı bir rezervasyonu siler
    Then API 201 status kodu ile silme işleminin başarılı olduğu doğrulanmalıdır
