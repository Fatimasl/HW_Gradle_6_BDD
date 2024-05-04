package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Value;
import lombok.val;
import ru.netology.web.data.DataHelper;

import java.util.ArrayList;
import java.util.Collections;

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

    public int getCardBalance(String fourSymbolsCardNumber) {
        String text;
        for (SelenideElement item : listItem) {
            text = item.text();
            String shortCardNumber = text.substring(text.indexOf(cardNumberStart) + 15, text.indexOf(balanceStart));
            if (shortCardNumber.equals(fourSymbolsCardNumber)) {
                return extractBalance(text);
            }
        }
        return 0;
    }

    private int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    //нажимает кнопку "Пополнить" для карты, которую решили пополнять
    public static DashboardPageForTransfer clickCardTransferButton(DataHelper.CardId cardOn) {
        String text;
        for (SelenideElement item : listItem) {
            text = item.text();
            String shortCardNumber = text.substring(text.indexOf(cardNumberStart) + 15, text.indexOf(balanceStart));
            if (shortCardNumber.equals(cardOn.getShortCardId())) {
                item.$("button").click();
                return new DashboardPageForTransfer();
            }
        }
        return null;
    }

    //возвращает псевдослучайное целое число рублей, которое можно списать с карты, на которой сумма баланса равна cardBalance (от 1 до cardBalance)
    public int randomPossibleAmountForTransfer(int cardBalance) {
        return (int) (Math.random() * (cardBalance + 1));
    }

    //выбирает случайную карту из коллекции карт
    public DataHelper.CardId getRandomCard(ArrayList<DataHelper.CardId> cards) {
        Collections.shuffle(cards);
        return cards.get(0);
    }

}
