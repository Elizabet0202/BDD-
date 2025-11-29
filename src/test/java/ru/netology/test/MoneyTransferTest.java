package ru.netology.test;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.netology.data.DataHelper;
import ru.netology.page.LoginPage;

public class MoneyTransferTest {

    @Test
    void shouldTransferMoneyBetweenOwnCards() {
        // 1. Открываем приложение
        var loginPage = open("http://localhost:9999", LoginPage.class);

        // 2. Логин
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);

        // 3. Вводим код подтверждения
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);

        // 4. Подготовка тестовых данных
        int amount = 1000;
        var firstCard = DataHelper.getFirstCard();   // источник
        var secondCard = DataHelper.getSecondCard(); // получатель

        int firstBalanceBefore = dashboardPage.getCardBalance(firstCard);
        int secondBalanceBefore = dashboardPage.getCardBalance(secondCard);

        // 5. Пополняем вторую карту с первой
        var transferPage = dashboardPage.selectCardToTopUp(secondCard);
        dashboardPage = transferPage.transfer(amount, firstCard);

        // 6. Проверяем балансы после перевода
        int firstBalanceAfter = dashboardPage.getCardBalance(firstCard);
        int secondBalanceAfter = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstBalanceBefore - amount, firstBalanceAfter);
        assertEquals(secondBalanceBefore + amount, secondBalanceAfter);
    }
}

