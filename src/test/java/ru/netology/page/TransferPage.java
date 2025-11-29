package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {

    private SelenideElement amountInput = $("[data-test-id=amount] input");
    private SelenideElement fromInput = $("[data-test-id=from] input");
    private SelenideElement transferButton = $("[data-test-id=action-transfer]");

    public DashboardPage transfer(int amount, DataHelper.CardInfo fromCard) {
        amountInput.setValue(String.valueOf(amount));
        fromInput.setValue(fromCard.getNumber());
        transferButton.click();
        return new DashboardPage();
    }
}
