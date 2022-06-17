package com.example.springrestapi.configs;

import com.example.springrestapi.accounts.Account;
import com.example.springrestapi.accounts.AccountRole;
import com.example.springrestapi.accounts.AccountService;
import com.example.springrestapi.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {

                Account account = Account.builder()
                                          .email(appProperties.getAdminUsername())
                                          .passsword(appProperties.getAdminPassword())
                                          .roles(Stream.of(AccountRole.ADMIN, AccountRole.USER)
                                                       .collect(Collectors.toSet()))
                                          .build();
                accountService.saveAccount(account);

                account = Account.builder()
                                         .email(appProperties.getUserUsername())
                                         .passsword(appProperties.getUserPassword())
                                         .roles(Stream.of(AccountRole.USER)
                                                      .collect(Collectors.toSet()))
                                         .build();
                accountService.saveAccount(account);
            }
        };
    }
}
