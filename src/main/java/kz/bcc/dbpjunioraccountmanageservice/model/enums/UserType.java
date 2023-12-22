package kz.bcc.dbpjunioraccountmanageservice.model.enums;

public enum UserType {
    PARENT("PARENT"),
    JUNIOR("JUNIOR"),
    SYSTEM("SYSTEM");

    private final String name;

    UserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static class Constants {
        public static final String PARENT = "PARENT";
        public static final String JUNIOR = "JUNIOR";
    }
}