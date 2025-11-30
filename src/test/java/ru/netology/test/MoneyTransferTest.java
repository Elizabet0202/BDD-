package ru.netology.test;

import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;

public class MoneyTransferTest {

    private DashboardPage dashboardPage;


    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    // ✅ ПОЗИТИВНЫЙ КЕЙС (Happy Path)
    @Test
    @DisplayName("should Transfer Money Between Own Cards")
    void shouldTransferMoneyBetweenOwnCards() {
        var firstCard = DataHelper.getFirstCard();    // пополняем её
        var secondCard = DataHelper.getSecondCard();  // с неё списываем

        // начальные балансы
        int firstCardStartBalance = dashboardPage.getCardBalance(firstCard);
        int secondCardStartBalance = dashboardPage.getCardBalance(secondCard);

        // выбираем первую карту для пополнения
        TransferPage transferPage = dashboardPage.selectCardToTopUp(firstCard);

        // валидная сумма (точно меньше баланса донора)
        int amount = 1000;

        // переводим с второй карты на первую
        dashboardPage = transferPage.transfer(amount, secondCard);

        // балансы после перевода
        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        // ожидаемые значения
        int expectedFirstCardBalance = firstCardStartBalance + amount;
        int expectedSecondCardBalance = secondCardStartBalance - amount;

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        expectedFirstCardBalance,
                        actualFirstCardBalance,
                        "Баланс первой карты должен увеличиться на сумму перевода"
                ),
                () -> Assertions.assertEquals(
                        expectedSecondCardBalance,
                        actualSecondCardBalance,
                        "Баланс второй карты должен уменьшиться на сумму перевода"
                )
        );
    }

    // ❌ НЕГАТИВНЫЙ КЕЙС (перевод сверх лимита)
    @Test
    @DisplayName("should Not Transfer Money Between Own Cards Over Limit")
    void shouldNotTransferMoneyBetweenOwnCardsOverLimit() {
        var firstCard = DataHelper.getFirstCard();    // пополняем её
        var secondCard = DataHelper.getSecondCard();  // с неё списываем

        int firstCardStartBalance = dashboardPage.getCardBalance(firstCard);
        int secondCardStartBalance = dashboardPage.getCardBalance(secondCard);

        TransferPage transferPage = dashboardPage.selectCardToTopUp(firstCard);

        // сумма специально БОЛЬШЕ баланса карты-донора
        int amount = secondCardStartBalance + 1_000;

        dashboardPage = transferPage.transfer(amount, secondCard);

        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

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
