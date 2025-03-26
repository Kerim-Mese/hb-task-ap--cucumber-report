package utilities;

import io.cucumber.java.Before;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class Base_Request {
    public static RequestSpecification spec;
    public static String token;

    @Before
    public void setUp() {
        String baseUrlHerokuapp=ConfigReader.getProperty("baseUrlHerokuapp");

        spec = new RequestSpecBuilder()
                .setBaseUri(baseUrlHerokuapp)
                .setContentType("application/json")
                .build();
    }
}
