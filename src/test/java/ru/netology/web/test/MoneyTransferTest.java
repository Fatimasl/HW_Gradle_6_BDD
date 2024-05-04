package ru.netology.web.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.VerificationPage;

import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardId сardTransferOn;
    DataHelper.CardId сardTransferOff;

    @BeforeEach
    void setup() {
        open ("http://localhost:9999");
        LoginPage loginPage = new LoginPage();
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();

        VerificationPage verificationPage = loginPage.validLogin(authInfo);
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);

        dashboardPage = verificationPage.validVerify(verificationCode);
        DataHelper.CardId card1 = DataHelper.getCardId1();
        DataHelper.CardId card2 = DataHelper.getCardId2();

        //коллекция из доступных пользователю карт (можно будет легко добавить более 2-х карт при необходимости)
        ArrayList<DataHelper.CardId> cards = new ArrayList<DataHelper.CardId>();
        cards.add(card1);
        cards.add(card2);
        //случайно определим карту для пополнения
        //это для реализации условия задачи "Нужно, чтобы вы через Page Object's добавили доменный метод выбора карты для пополнения",
        // т.е. случайно выбрать одну карту из двух. Я поняла это так.
        сardTransferOn = dashboardPage.getRandomCard(cards);
        //удалим карту для пополнения из коллекции карт пользователя
        cards.remove(сardTransferOn);
        //случайно определим карту, с которой будем пополнять
        сardTransferOff = dashboardPage.getRandomCard(cards);
    }

    @Test
    @DisplayName("shouldTransferMoneyBetweenOwnCardsWithSuccess")
    void shouldTransferMoneyBetweenOwnCardsWithSuccess() {
        //запишем начальные балансы карт (для пополнения и с которой будем пополнять)
        var startBalanceCardTransferOn = dashboardPage.getCardBalance(сardTransferOn.getShortCardId());
        var startBalanceCardTransferOff = dashboardPage.getCardBalance(сardTransferOff.getShortCardId());

        //получим сумму, которую можно перевести
        var amountForTransfer = dashboardPage.randomPossibleAmountForTransfer(startBalanceCardTransferOff);
        //нажмем "Пополнить" около карты, которую решили пополнить
        var dashboardPageForTransfer = dashboardPage.clickCardTransferButton(сardTransferOn);
        //совершим перевод
        var dashboardPageAfterTransfer = dashboardPageForTransfer.doValidTransfer(String.valueOf(amountForTransfer), сardTransferOff.getCardId());

        //запишем актуальные балансы карт (для пополнения и с которой пополнили)
        var actualBalanceCardTransferOn = dashboardPageAfterTransfer.getCardBalance(сardTransferOn.getShortCardId());
        var actualBalanceCardTransferOff = dashboardPageAfterTransfer.getCardBalance(сardTransferOff.getShortCardId());

        assertEquals(startBalanceCardTransferOn + amountForTransfer, actualBalanceCardTransferOn);
        assertEquals(startBalanceCardTransferOff - amountForTransfer, actualBalanceCardTransferOff);
    }

    @Test
    @DisplayName("MustFailedTransferMoneyByEmptyAmount")
    void MustFailedTransferMoneyByEmptyAmount() {
        //нажмем "Пополнить" около карты, которую решили пополнить
        var dashboardPageForTransfer = dashboardPage.clickCardTransferButton(сardTransferOn);
        //попробуем совершить перевод с той же карты, которую выбрали для пополнения
        dashboardPageForTransfer.doTransfer("", сardTransferOff.getCardId());

        dashboardPageForTransfer.haveError("Ошибка! Произошла ошибка");
    }

    @Test
    @DisplayName("MustFailedTransferMoneyByEmptyCardNumber")
    void MustFailedTransferMoneyByEmptyCardNumber() {
        var startBalanceCardTransferOff = dashboardPage.getCardBalance(сardTransferOff.getShortCardId());

        //получим сумму, которую можно перевести
        var amountForTransfer = dashboardPage.randomPossibleAmountForTransfer(startBalanceCardTransferOff);
        //нажмем "Пополнить" около карты, которую решили пополнить
        var dashboardPageForTransfer = dashboardPage.clickCardTransferButton(сardTransferOn);
        //попробуем совершить перевод с той же карты, которую выбрали для пополнения
        dashboardPageForTransfer.doTransfer(String.valueOf(amountForTransfer), "");

        dashboardPageForTransfer.haveError("Ошибка! Произошла ошибка");
    }

    @Test
    @DisplayName("MustFailedTransferMoneyBetweenOwnCardAndNotRightCard")
    void MustFailedTransferMoneyBetweenOwnCardAndNotRightCard() {

        сardTransferOff = DataHelper.getCardIdNotRight();
        //сумма перевода
        var amountForTransfer = 1;
        //нажмем "Пополнить" около карты, которую решили пополнить
        var dashboardPageForTransfer = dashboardPage.clickCardTransferButton(сardTransferOn);
        //попробуем совершить перевод с той же карты, которую выбрали для пополнения
        dashboardPageForTransfer.doTransfer(String.valueOf(amountForTransfer), сardTransferOff.getCardId());

        dashboardPageForTransfer.haveError("Ошибка! Произошла ошибка");
    }

    @Test
    @DisplayName("MustFailedTransferMoneyBetweenTheSameOwnCard")
    void MustFailedTransferMoneyBetweenTheSameOwnCard() {
        //запишем начальный баланс карты для пополнения
        var startBalanceCardTransferOn = dashboardPage.getCardBalance(сardTransferOn.getShortCardId());

        //получим сумму, которую можно перевести с карты, которую хотим пополнить
        var amountForTransfer = dashboardPage.randomPossibleAmountForTransfer(startBalanceCardTransferOn);
        //нажмем "Пополнить" около карты, которую решили пополнить
        var dashboardPageForTransfer = dashboardPage.clickCardTransferButton(сardTransferOn);
        //попробуем совершить перевод с той же карты, которую выбрали для пополнения
        dashboardPageForTransfer.doTransfer(String.valueOf(amountForTransfer), сardTransferOn.getCardId());

        dashboardPageForTransfer.haveError("Ошибка! Укажите другую карту для списания");
    }

    @Test
    @DisplayName("tryTransferMoneyBetweenOwnCardsMustFailedByAmount")
    void tryTransferMoneyBetweenOwnCardsMustFailedByAmount() {
        //запишем начальный баланс карты, с которой будем пополнять
        var startBalanceCardTransferOff = dashboardPage.getCardBalance(сardTransferOff.getShortCardId());

        //получим сумму, которую НЕЛЬЗЯ перевести
        var amountForTransfer = startBalanceCardTransferOff + dashboardPage.randomPossibleAmountForTransfer(startBalanceCardTransferOff);
        //нажмем "Пополнить" около карты, которую решили пополнить
        var dashboardPageForTransfer = dashboardPage.clickCardTransferButton(сardTransferOn);
        //попробуем совершить перевод
        dashboardPageForTransfer.doTransfer(String.valueOf(amountForTransfer), сardTransferOff.getCardId());

        dashboardPageForTransfer.haveError("Ошибка! Указанная сумма превышает баланс карты списания");
    }

}

