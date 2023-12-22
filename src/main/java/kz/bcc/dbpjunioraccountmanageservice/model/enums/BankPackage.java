package kz.bcc.dbpjunioraccountmanageservice.model.enums;

public enum BankPackage {
    COLVIR_CARD_HOLD_STATUS("TEST.TEST"),
    COLVIR_CARD_BLOCK("TEST.TEST"),
    COLVIR_CARD_REISSUE("TEST.TEST"),
    COLVIR_CARD_REISSUE_STATUS("TEST.TEST"),
    COLVIR_CARD_UNBLOCK("TEST.TEST"),
    COLVIR_CARD_CLOSE("TEST.TEST"),
    COLVIR_JUNIOR_PRODUCTS("TEST.TEST"),
    MCI_GET_JUNIOR_LANG("TEST.TEST"),
    MCI_GET_PARENT_LANG("TEST.TEST"),
    MCI_PUSH_CLOSE_NOTIFY("TEST.TEST")
    ;
    private final String description;

    BankPackage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
