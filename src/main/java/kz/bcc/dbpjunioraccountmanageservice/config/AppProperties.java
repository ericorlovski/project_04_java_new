package kz.bcc.dbpjunioraccountmanageservice.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppProperties {

    @Value("${service.keycloak.url}")
    private String keycloakUrl;

    @Value("${service.keycloak.realm}")
    private String keycloakRealm;

    @Value("${service.keycloak.client-secret}")
    private String keycloakClientSecret;

    @Value("${service.keycloak.client-id}")
    private String keycloakClientId;

    @Value("${service.colvir.exec-url}")
    private String colvirAdapterExecUrl;

    //auth
    @Value("${service.auth-server.token-url}")
    private String authServerTokenUrl;

    @Value("${service.auth-server.admin-login}")
    private String authServerAdminLogin;

    @Value("${service.auth-server.admin-password}")
    private String authServerAdminPassword;

    @Value("${service.auth-server.user-data}")
    private String userDataFromAuthServerUrl;


    @Value("${service.mci.exec-url}")
    private String mciExecUrl;

    @Value("${service.dbp_junior_notify.exec-url}")
    private String notifyExecUrl;

    @Value("${service.dbp_junior_notify.sent-otp}")
    private String notifySentOtp;

    @Value("${service.dbp_junior_notify.check-otp}")
    private String notifyCheckOtp;

    @Value("${validate-token-auth-server}")
    private String validateUrl;
}
