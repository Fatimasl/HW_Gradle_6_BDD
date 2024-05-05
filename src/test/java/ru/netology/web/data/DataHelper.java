package ru.netology.web.data;

import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;

public class DataHelper {
    private DataHelper() {}

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }

    @Value
    public static class CardId {
        private String shortCardId;
        private String cardId;
    }

    public static CardId getCardId1() {
        return new CardId("**** **** **** 0001","5559 0000 0000 0001");
    }

    public static CardId getCardId2() {
        return new CardId("**** **** **** 0002","5559 0000 0000 0002");
    }

    public static CardId getCardIdNotRight() {
        return new CardId("**** **** **** 9999","5559 0000 0000 9999");
    }

    //возвращает псевдослучайное целое число рублей, которое можно списать с карты, на которой сумма баланса равна cardBalance (от 1 до cardBalance)
    public static int randomPossibleAmountForTransfer(int cardBalance) {
        return (int) (Math.random() * (cardBalance + 1));
    }

    //выбирает случайную карту из коллекции карт
    public static DataHelper.CardId getRandomCard(ArrayList<CardId> cards) {
        Collections.shuffle(cards);
        return cards.get(0);
    }
}
