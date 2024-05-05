package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPageForTransfer {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement heading2 = $("[id=root] h1");
    private SelenideElement elAmount = $("[data-test-id=amount] input");
    private SelenideElement elFrom = $("[data-test-id=from] input");
    private SelenideElement elButton = $("[data-test-id=action-transfer]");
    private SelenideElement elErrorNote = $("[data-test-id=error-notification]");
    private SelenideElement elErrorNotificationContent = $("[data-test-id=error-notification] .notification__content");

    public DashboardPageForTransfer() {
        heading.shouldBe(visible);
        heading2.shouldHave(text("Пополнение карты"));
    }

    public DashboardPage doValidTransfer(String amount, String otherCardNumber) {
        doTransfer(amount, otherCardNumber);
        return new DashboardPage();
    }

    //      return new DashboardPage();

    public void doTransfer(String amount, String otherCardNumber) {
        elAmount.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        elAmount.setValue(amount);

        elFrom.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        elFrom.setValue(otherCardNumber);

        elButton.click();
    }

    public void haveError(String errorText) {
        elErrorNote.shouldBe(visible);
        elErrorNotificationContent.shouldHave(text(errorText));
    }
}
