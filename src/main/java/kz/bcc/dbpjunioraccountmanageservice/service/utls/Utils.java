package kz.bcc.dbpjunioraccountmanageservice.service.utls;

import kz.bcc.dbpjunioraccountmanageservice.model.enums.PushSmsDescCode;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static String getFullName(String firstName, String lastName, String middleName) {
        List<String> nameParts = new ArrayList<>();
        addNonEmptyPart(nameParts, firstName);
        addNonEmptyPart(nameParts, lastName);
        addNonEmptyPart(nameParts, middleName);
        return String.join(" ", nameParts);
    }

    private static void addNonEmptyPart(List<String> parts, String part) {
        if (!StringUtils.isEmpty(part)) {
            parts.add(part);
        }
    }

    public static String cutPhone(String login) {
        String cleanedNumber = login.replaceAll("[^0-9]", "");

        if (cleanedNumber.length() == 10) {
            cleanedNumber = "+7" + cleanedNumber;
        } else if (cleanedNumber.length() == 11 && cleanedNumber.startsWith("8")) {
            cleanedNumber = "+7" + cleanedNumber.substring(1);
        }

        if (!cleanedNumber.startsWith("+")) {
            cleanedNumber = "+" + cleanedNumber;
        }

        return cleanedNumber;
    }

    public static String getPushText(String code, String lang) {
        String text;

        if (Objects.equals(lang, "en")) {
            text = code + "_EN";
        } else if (Objects.equals(lang, "kz")) {
            text = code + "_KZ";
        } else {
            text = code + "_RU";
        }

        return PushSmsDescCode.valueOf(text).getDescription();
    }
}
