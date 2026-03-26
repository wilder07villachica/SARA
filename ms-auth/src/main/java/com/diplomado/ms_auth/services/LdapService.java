package com.diplomado.ms_auth.services;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import org.springframework.stereotype.Service;

@Service
public class LdapService {

    private static final String HOST = "localhost";
    private static final int PORT = 8389;
    private static final String BASE_DN = "dc=diplomado,dc=local";

    public AuthenticatedUser authenticate(String username, String password) {
        try (LDAPConnection connection = new LDAPConnection(HOST, PORT)) {
            String userDN = "uid=" + username + "," + BASE_DN;
            connection.bind(userDN, password);

            Entry entry = connection.getEntry(userDN, "uid", "employeeType");
            String role = entry != null ? entry.getAttributeValue("employeeType") : "AGENTE";

            return new AuthenticatedUser(username, role);
        } catch (LDAPException e) {
            return null;
        }
    }

    public record AuthenticatedUser(String username, String role) {
    }
}