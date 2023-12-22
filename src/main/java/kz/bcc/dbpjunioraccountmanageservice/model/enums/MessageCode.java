package kz.bcc.dbpjunioraccountmanageservice.model.enums;

public enum MessageCode {
    NAME_ALREADY_EXIST(1, "This idn already exist in the database!"),
    COLVIR_REQUEST_ERROR(2, "An error occured during request to Colvir!"),
    KEYCLOAK_ERROR(3, "Cannot authenticate in keycloak!"),
    MCI_REQUEST_ERROR(4, "An error occured during request to MCI!"),
    NOTIFY_REQUEST_ERROR(5, "An error occured during request to NOTIFY service!"),
    GENERAL_ERROR(6, "General service error!"),
    NO_MATCHING_CHILD_ERROR(7, "No parent-child match!"),
    CARD_CLOSE_ERROR(8, "Error close a card in Colvir!"),
    CARD_REISSUE_ERROR(9, "Error re-issuing a card in Colvir!"),
    CARD_REISSUE_INITIALIZING_ERROR(10, "Error initializing card reissue request!"),
    PARENT_REJECTED_MESSAGE(18, "Parent rejected closure request!"),
    CARD_BLOCK_ERROR(11, "Card reissue error!"),
    REISSUE_CARD_CODE(1, "1"),
    CLOSE_CARD_CODE(1, "0"),
    BLOCK_CARD_TO_CLOSE_CODE(19, "141"),
    PARENT_OR_CHILD_ENTITY_EMPTY(13, "Parent or child entity is empty!"),
    JUNIOR_PHONE_NULL(14, "Junior phone param is empty!"),
    PARENT_PHONE_NULL(15, "Parent phone param is empty!"),
    CARD_CLOSE_INITIALIZING_ERROR(10, "Error initializing card close request!"),
    CLIENT_NOT_FOUND_ERROR(16, "The client was not found in the database!"),
    MCI_NOTIFY_SERVICE_ERROR(17, "Error generating notification to parent in MCI!"),
    ;
    private final int code;
    private final String description;

    MessageCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
