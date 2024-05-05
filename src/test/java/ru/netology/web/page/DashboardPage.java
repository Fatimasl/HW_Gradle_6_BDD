package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement heading2 = $("[id=root] h1");
    private static ElementsCollection listItem = $$(".list__item div");
    private static final String balanceStart = ", баланс: ";
    private final String balanceFinish = " р.";
    private static final String cardNumberStart = "****";

    public DashboardPage() {
        heading.shouldBe(visible);
        heading2.shouldHave(text("Ваши карты"));
    }

    public int getCardBalance(String maskCardNumber) {
        var item = listItem.findBy(text(maskCardNumber));
        return extractBalance(item.text());
    }

    private int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    //нажимает кнопку "Пополнить" для карты, которую решили пополнять
    public static DashboardPageForTransfer clickCardTransferButton(String maskCardNumber) {
        var item = listItem.findBy(text(maskCardNumber));
        item.$("button").click();
        return new DashboardPageForTransfer();
    }

}
