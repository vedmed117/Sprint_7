import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.example.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginCourierTest {

    private final String BASE_URI = "https://qa-scooter.praktikum-services.ru/api/v1/courier";
    private String courierLogin;
    private String courierPassword;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        courierLogin = "test_login_" + System.currentTimeMillis();
        courierPassword = "test_password";
        Courier courier = new Courier(courierLogin, courierPassword, "test_name");
        createCourier(courier);
    }

    @After
    public void tearDown() {
        deleteCourier();
    }

    @Test
    @DisplayName("Login Courier with Valid Data")
    @Description("This test logs in a courier with valid data and verifies the response")
    public void loginCourierWithValidData() {
        Courier courier = new Courier(courierLogin, courierPassword);
        ValidatableResponse response = loginCourier(courier);
        response.statusCode(200).body("id", equalTo(courierId));
    }

    @Test
    @DisplayName("Login Courier with Invalid Password")
    @Description("This test logs in a courier with an invalid password and verifies the response")
    public void loginCourierWithInvalidPassword() {
        Courier courier = new Courier(courierLogin, "wrong_password");
        ValidatableResponse response = loginCourier(courier);
        response.statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Login Courier with Missing Fields")
    @Description("This test logs in a courier with missing fields and verifies the response")   // Ссылка на документацию: https://qa-scooter.praktikum-services.ru/docs/#api-Courier-Login
    public void loginCourierWithMissingFields() {
        ValidatableResponse response = loginCourier(new Courier(courierLogin, null));
        response.statusCode(400).body("message", equalTo("Недостаточно данных для входа"));

        response = loginCourier(new Courier(null, courierPassword));
        response.statusCode(400).body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Login Non-Existent Courier")
    @Description("This test logs in a non-existent courier and verifies the response")
    public void loginNonExistentCourier() {
        Courier courier = new Courier("nonexistent_login", "password");
        ValidatableResponse response = loginCourier(courier);
        response.statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Logging in courier with login: {courier.login} and password: {courier.password}")
    private ValidatableResponse loginCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(BASE_URI + "/login")
                .then();
    }

    @Step("Creating courier with login: {courier.login}, password: {courier.password}, and name: {courier.firstName}")
    private void createCourier(Courier courier) {
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(BASE_URI)
                .then()
                .statusCode(201);

        // Логинимся сразу после создания, чтобы получить courierId
        courierId = loginCourier(new Courier(courier.getLogin(), courier.getPassword()))
                .extract().path("id");
    }

    @Step("Deleting courier with ID: {courierId}")
    private void deleteCourier() {
        given()
                .header("Content-type", "application/json")
                .when()
                .delete(BASE_URI + "/" + courierId)
                .then()
                .statusCode(200);
    }
}
