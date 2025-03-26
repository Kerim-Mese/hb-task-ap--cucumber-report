package runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "pretty",
                "html:target/default-cucumber-reports.html",
                "json:target/json-reports/cucumber.json",
                "junit:target/xlm-report/cucumber.xml",
                "rerun:TestOutput/failed_scenario.txt"},
        features = "src/test/resources/features",
        glue = {"stepdefinations","utilities"},
        tags = "@US001",
        dryRun = false
)
public class runner {
}
/*
Runner clasin boddysi boodysi icine birsey yazılmaz
Runner classinda önemli olan iki tane notasyon vardir.
1)@RunWith==> Runner clasimizda calisma özelliği getirdi
2)@CucumberOptions==> parantezi icinde hangi testleri calistiracagımızı
                    hangi raporları alacagımızı, features ve glue para metreleri ile bu dosyaların path yolları
                    gibi test ayrıntılarını tanımlarız ve bazi pluginler ekleyebilriz
    dryRun = true ==> iken feature file daki test senaryolarini calistirmadan ,
     eksik stepDefinition i olup olmadıgını kontrol eder, browser i çalıştırmaz
     rerun plugini ile fail olan scenariolari burada belirtmiş oldugumuz failed_scenario.txt dosyasi icinde tutabiliriz
 */