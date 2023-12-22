package kz.bcc.dbpjunioraccountmanageservice.model.enums;

public enum PushSmsDescCode {
    PUSH_REISSUE_BCC_RU("Перевыпуск #JuniorCard Карта готова, можно пользоваться"),
    PUSH_REISSUE_BCC_EN("Reissue #JuniorCard The card is done, you can use it"),
    PUSH_REISSUE_BCC_KZ("#JuniorCard қайта шығару Карта дайын, оны пайдалануға болады"),
    PUSH_REISSUE_JB_EN("Reissue card. The card is already done. The card data has also been updated, you can use it"),
    PUSH_REISSUE_JB_RU("Перевыпуск карты. Карта уже готова. Данные карты тоже обновились, можно ею пользоваться"),
    PUSH_REISSUE_JB_KZ("Картаны қайта шығару. Карта дайын. Карта деректері де жаңартылды, сіз оны пайдалана аласыз"),
    PUSH_CLOSE_BCC_RU("#JuniorCard закрыта. Для безопасности физическую карту можно уничтожить"),
    PUSH_CLOSE_BCC_EN("#JuniorCard is closed. For security, the physical card can be destroyed"),
    PUSH_CLOSE_BCC_KZ("#JuniorCard жабық. Қауіпсіздік үшін физикалық карта жойылуы мүмкін"),
    PUSH_NOT_CLOSED_JB_EN("Failed to close the card. A server error has occurred, we are already fixing it. Try closing later"),
    PUSH_NOT_CLOSED_JB_RU("Не удалось закрыть карту. Произошла ошибка сервера, уже исправляем. Попробуй закрыть позже"),
    PUSH_NOT_CLOSED_JB_KZ("Картаны жабу мүмкін болмады. Сервер қатесі орын алды, біз оны түзетіп жатырмыз. Кейінірек жауып көріңіз"),
    PUSH_NOT_CLOSED_BCC_EN("Failed to close #JuniorCard. A server error has occurred, we are already fixing it. Try closing later"),
    PUSH_NOT_CLOSED_BCC_RU("Не удалось закрыть #JuniorCard. Произошла ошибка сервера, уже исправляем. Попробуйте закрыть позже"),
    PUSH_NOT_CLOSED_BCC_KZ("#JuniorCard жабылмады. Сервер қатесі орын алды, біз оны түзетіп жатырмыз. Кейінірек жауып көріңіз"),
    PUSH_REJECTED_JB_EN("Junior Bank. Card closure rejected "),
    PUSH_REJECTED_JB_RU("Junior Bank. Закрытие карты отклонено "),
    PUSH_REJECTED_JB_KZ("Junior Bank. Картаны жабу қабылданбады "),
    PUSH_CLOSE_BALANCE_BCC_RU("#JuniorCard закрыта. Остаток денег перевели на выбранный счет. Для безопасности физическую карту можно уничтожить"),
    PUSH_CLOSE_BALANCE_BCC_EN("#JuniorCard is closed. The rest of the money was transferred to the selected account. For security, the physical card can be destroyed"),
    PUSH_CLOSE_BALANCE_BCC_KZ("#JuniorCard жабық. Қалған ақша таңдалған шотқа аударылды. Қауіпсіздік үшін физикалық карта жойылуы мүмкін"),
    SERVICE_NAME("ACCOUNT")
    ;
    private final String description;

    PushSmsDescCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
