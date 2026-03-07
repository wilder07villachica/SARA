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
        baseEntry.addAttribute(
                "objectClass",
                "top",
                "domain"
        );
        baseEntry.addAttribute(
                "dc",
                "diplomado"
        );

        ds.add(baseEntry);

        Entry userEntry = new Entry("uid=testuser," + baseDN);
        userEntry.addAttribute("objectClass", "inetOrgPerson");
        userEntry.addAttribute("cn", "Test User");
        userEntry.addAttribute("sn", "User");
        userEntry.addAttribute("uid", "testuser");
        userEntry.addAttribute("userPassword", "test123");

        ds.add(userEntry);

        System.out.println("LDAP listo en puerto 8389");
    }
}
