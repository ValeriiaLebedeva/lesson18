package tests.demowebshop;

import com.codeborne.selenide.Configuration;
import config.AppConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;


import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;

public class AddToCartUsingCookieTest {

    public static AppConfig webShopConfig = ConfigFactory.create(AppConfig.class, System.getProperties());

    @BeforeAll
    static void configureBaseUrl() {
        RestAssured.baseURI = webShopConfig.apiUrl();
        Configuration.baseUrl = webShopConfig.webUrl();
    }

    Integer getItemsCountFromCart(String value) {
        if (value != null) {
            return Integer.parseInt(value.substring(1, value.length() - 1));
        }
        return 0;
    }


    @Test
    void checkCartNumberTest() {

        //Get cookie
        String authorizationCookie =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .formParam("Email", webShopConfig.userLogin())
                        .formParam("Password", webShopConfig.userPassword())
                        .when()
                        .post("/login")
                        .then()
                        .statusCode(302)
                        .extract()
                        .cookie("NOPCOMMERCE.AUTH");


        //Open minimal content, because cookie can be set when site is opened
        open("/Themes/DefaultClean/Content/images/logo.png");


        //Set cookie to browser
        getWebDriver().manage().addCookie(
                new Cookie("NOPCOMMERCE.AUTH", authorizationCookie));


        //Open cart page
        open("/cart");


        //Get item cart number before adding new item
        String cartItemNumber = $(".cart-qty").getText();


        //Add item to cart using api and check the new number is greater by 1 number before adding the item to cart
        Response response =
                given()
                        .cookie("NOPCOMMERCE.AUTH", authorizationCookie)
                        .when()
                        .post("addproducttocart/catalog/13/1/1")
                        .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                        .body("updatetopcartsectionhtml", is("(" + (getItemsCountFromCart(cartItemNumber) + 1) + ")"))
                        .extract().response();
        assertThat((String) response.path("updatetopcartsectionhtml")).isEqualTo("(" + (getItemsCountFromCart(cartItemNumber) + 1) + ")");

    }
}

