package com.bybit.config;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.config.EnvironmentVaultConfiguration;


/** * Vault 초기화 */  
@Configuration 
@PropertySource("vault.properties")
@Import(EnvironmentVaultConfiguration.class) 
@VaultPropertySource("${spring.application.name}/config/${spring.profiles.active:default}")
@RefreshScope
public class VaultConfig  {	
}
