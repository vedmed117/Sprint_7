import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {

    private final String BASE_URI = "https://qa-scooter.praktikum-services.ru/api/v1/orders";

    @Test
    @DisplayName("Get Orders List")
    @Description("This test fetches the list of orders and verifies the response")
    public void getOrdersList() {
        RestAssured.baseURI = BASE_URI;
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
