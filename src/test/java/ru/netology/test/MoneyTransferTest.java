package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;

public class MoneyTransferTest {

    private DashboardPage dashboardPage;

    @BeforeAll
    static void setUpAll() {

        //System.setProperty("selenide.shutdownHook", "false");
        //Configuration.holdBrowserOpen = true;
        //Configuration.reopenBrowserOnFail = false;
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    @DisplayName("should Not Transfer Money Between Own Cards Over Limit")
    void shouldNotTransferMoneyBetweenOwnCardsOverLimit() {
        // карта-получатель и карта-донор
        var firstCard = DataHelper.getFirstCard();    // пополняем её
        var secondCard = DataHelper.getSecondCard();  // с неё списываем

        // запоминаем начальные балансы
        int firstCardStartBalance = dashboardPage.getCardBalance(firstCard);
        int secondCardStartBalance = dashboardPage.getCardBalance(secondCard);

        // выбираем первую карту для пополнения (жмём «Пополнить» на первой)
        TransferPage transferPage = dashboardPage.selectCardToTopUp(firstCard);

        // сумма специально БОЛЬШЕ баланса карты-донора
        int amount = secondCardStartBalance + 1_000;

        // пытаемся перевести с второй карты на первую
        dashboardPage = transferPage.transfer(amount, secondCard);

        // читаем балансы после попытки перевода
        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        // ожидаем, что они НЕ изменились
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        firstCardStartBalance,
                        actualFirstCardBalance,
                        "Баланс первой карты не должен меняться при переводе сверх лимита"
                ),
                () -> Assertions.assertEquals(
                        secondCardStartBalance,
                        actualSecondCardBalance,
                        "Баланс второй карты не должен меняться при переводе сверх лимита"
                )
        );
    }
}
