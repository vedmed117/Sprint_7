import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.example.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final String BASE_URI = "https://qa-scooter.praktikum-services.ru/api/v1/orders";
    private final Order order;

    public CreateOrderTest(Order order) {
        this.order = order;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][]{
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", Arrays.asList("BLACK"))},
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", Arrays.asList("BLACK", "GREY"))},
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", Arrays.asList())}
        });
    }

    @Test
    @DisplayName("Create Order with Different Color Options")
    @Description("This test creates orders with different color options and verifies the response")
    public void createOrder() {
        RestAssured.baseURI = BASE_URI;
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
