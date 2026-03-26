package com.diplomado.ms_auth.config;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.Entry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class EmbeddedLdapConfig {

    @PostConstruct
    public void startLdap() throws Exception {
        String baseDN = "dc=diplomado,dc=local";
        InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(baseDN);

        InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig(
                "default",
                8389
        );

        config.setListenerConfigs(listenerConfig);
        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
        ds.startListening();

        Entry baseEntry = new Entry(baseDN);
        baseEntry.addAttribute("objectClass", "top", "domain");
        baseEntry.addAttribute("dc", "diplomado");
        ds.add(baseEntry);

        addUser(ds, baseDN,
                "admin.callcenter",
                "Admin Call Center",
                "CallCenter",
                "Admin2026*",
                "ADMIN");

        addUser(ds, baseDN,
                "agente.lima01",
                "Agente Lima 01",
                "Lima01",
                "Agente2026*",
                "AGENTE");

        addUser(ds, baseDN,
                "supervisor.n1",
                "Supervisor Nivel 1",
                "Supervisor",
                "Super2026*",
                "SUPERVISOR");

        System.out.println("LDAP listo en puerto 8389");
    }

    private void addUser(InMemoryDirectoryServer ds,
                         String baseDN,
                         String uid,
                         String cn,
                         String sn,
                         String password,
                         String role) throws Exception {

        Entry userEntry = new Entry("uid=" + uid + "," + baseDN);
        userEntry.addAttribute("objectClass", "inetOrgPerson");
        userEntry.addAttribute("cn", cn);
        userEntry.addAttribute("sn", sn);
        userEntry.addAttribute("uid", uid);
        userEntry.addAttribute("userPassword", password);
        userEntry.addAttribute("employeeType", role);

        ds.add(userEntry);
    }
}