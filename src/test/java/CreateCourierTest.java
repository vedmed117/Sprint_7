import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private String courierId;
    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Step("Deleting courier with ID: {courierId}")
    private void deleteCourier(String courierId) {
        given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"id\":\"" + courierId + "\"}")
                .when()
                .delete("/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Create Courier with Valid Data")
    @Description("This test creates a courier with valid data and verifies the response")
    public void createCourierWithValidData() {
        courier = new Courier("test_login_" + System.currentTimeMillis(), "test_password", "test_first_name");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/courier");

        response.then().statusCode(201).body("ok", equalTo(true));

        // Extract courierId from the response
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"login\":\"" + courier.getLogin() + "\", \"password\":\"" + courier.getPassword() + "\"}")
                .when()
                .post("/courier/login");

        courierId = loginResponse.then().extract().path("id").toString();
    }

    @Test
    @DisplayName("Create Courier with Existing Login")
    @Description("This test tries to create a courier with an existing login and verifies the response")
    public void createCourierWithExistingLogin() {
        courier = new Courier("test_login_" + System.currentTimeMillis(), "test_password", "test_first_name");

        // Создаем курьера
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(201).body("ok", equalTo(true));

        // Пытаемся создать курьера с таким же логином
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));       // Тест падает, поскольку в https://qa-scooter.praktikum-services.ru/docs/#api-Courier-CreateCourier другое body
    }


    @Test
    @DisplayName("Create Courier without Login")
    @Description("This test tries to create a courier without a login and verifies the response")
    public void createCourierWithoutLogin() {
        courier = new Courier(null, "test_password", "test_first_name");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Create Courier without Password")
    @Description("This test tries to create a courier without a password and verifies the response")
    public void createCourierWithoutPassword() {
        courier = new Courier("test_login_" + System.currentTimeMillis(), null, "test_first_name");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
