package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

import ru.netology.data.DataHelper;

public class DashboardPage {

    private ElementsCollection cards = $$(".list__item"); // или другой общий локатор карточек

    // Получение баланса по карте
    public int getCardBalance(DataHelper.CardInfo cardInfo) {
        SelenideElement card = cards.find(text(mask(cardInfo.getNumber())));
        String text = card.getText();
        return extractBalance(text);
    }

    // Выбор карты для пополнения (возвращаем страницу перевода)
    public TransferPage selectCardToTopUp(DataHelper.CardInfo cardInfo) {
        SelenideElement card = cards.find(text(mask(cardInfo.getNumber())));
        card.$("button").click(); // кнопка «Пополнить», если другая разметка — поправь локатор
        return new TransferPage();
    }

    // Маскируем номер карты под тот формат, который в UI
    private String mask(String fullNumber) {
        // допустим, в интерфейсе показаны только последние 4 цифры, вида "**** **** **** 0001"
        String last4 = fullNumber.substring(fullNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    // Вытаскиваем баланс из текста карточки
    // Например, текст: "**** **** **** 0001\nбаланс: 10000 р."
    private int extractBalance(String text) {
        String balanceStart = "баланс: ";
        String balanceFinish = " р.";
        int start = text.indexOf(balanceStart);
        int finish = text.indexOf(balanceFinish);
        String value = text.substring(start + balanceStart.length(), finish).trim();
        return Integer.parseInt(value);
    }
}