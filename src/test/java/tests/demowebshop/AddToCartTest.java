package tests.demowebshop;

import com.codeborne.selenide.Configuration;
import config.AppConfig;
import io.restassured.RestAssured;
import org.openqa.selenium.Cookie;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static org.hamcrest.Matchers.is;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class AddToCartTest {


    public static AppConfig webShopConfig = ConfigFactory.create(AppConfig.class, System.getProperties());

    @BeforeAll
    static void configureBaseUrl() {
        RestAssured.baseURI = webShopConfig.apiUrl();
        Configuration.baseUrl = webShopConfig.webUrl();
    }


    @Test
    @Tag("demoshop")
    @DisplayName("bla bla bla")
    void loginWithCookieToCheckUsersNamesTest() {

        step("Get cookie by api and set it to browser", () -> {
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


            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open("/Themes/DefaultClean/Content/images/logo.png"));


            step("Set cookie to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));

        });


        step("Open page", () ->
                open(""));

        //получить номер вещей в корзине и запомнить в отдельную переменную
        step("Get Item Cart Number", () ->
                $(".cart-qty").getValue());


        step("Add item to cart", () ->
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .body("product_attribute_16_5_4=14&product_attribute_16_6_5=15&" +
                                "product_attribute_16_3_6=19&product_attribute_16_4_7=44&" +
                                "product_attribute_16_8_8=22&addtocart_16.EnteredQuantity=1")
                        .cookie("__utmz=78382081.1642971029.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); NopCommerce.RecentlyViewedProducts=RecentlyViewedProductIds=71&RecentlyViewedProductIds=44&RecentlyViewedProductIds=75&RecentlyViewedProductIds=13; __atuvc=1%7C4%2C4%7C5; nop.CompareProducts=CompareProductIds=71&CompareProductIds=44&CompareProductIds=75; ARRAffinity=55622bac41413dfac968dd8f036553a9415557909fd0cd3244e7e0e656e4adc8; __utmc=78382081; __utma=78382081.751897874.1642971029.1644069830.1644073425.8; NOPCOMMERCE.AUTH=AE331BCDD069993CD89B27E626849C86DD38C87E17770F8D15C0D3DBACD345DDE97D524974926F987BD3BC009BA535F4BF660524B7ED93706EBAD241C76EA8B38D1F54AF2008E38C47364C04A91F5D6B45B7FC7F62AEB020C94A1FA2DED011044C8AAE7E77DB3C259FB856EB1F0FA9D77992C4517866519DB09633AF5F1665FD3CE0C26A907C8C9523A9A4DBD2D1C870; Nop.customer=2e93fb29-9633-422e-85c6-244ab4364fca; __utmb=78382081.3.10.1644073425")
                        .when()
                        .post("/addproducttocart/details/16/1")
                        .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                    //    .body("updatetopcartsectionhtml", is("(12)"))
                        .extract()
        );


        //получить новый номер, запомнить в отдельную переменную

  //      step("Compare two numbers of cart items", () ->


 //       );

        // сравнить первый номер и второй - второй отличается на единицу

    }
}
