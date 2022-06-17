package com.example.springrestapi.configs;

import com.example.springrestapi.accounts.Account;
import com.example.springrestapi.accounts.AccountRole;
import com.example.springrestapi.accounts.AccountService;
import com.example.springrestapi.common.AppProperties;
import com.example.springrestapi.common.BaseControllerTest;
import com.example.springrestapi.common.TestDesctiption;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDesctiption("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        String username = "hong1@gmail.com";
        String passsword = "honghong";
        Account account = Account.builder()
                                 .email(username)
                                 .passsword(passsword)
                                 .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                 .build();

        accountService.saveAccount(account);

        String clientId = appProperties.getClientId();
        String clientSecret = appProperties.getClientSecret();

        mockMvc.perform(post("/oauth/token")
                                .with(httpBasic(clientId, clientSecret))
                                .param("username", username)
                                .param("password", passsword)
                                .param("grant_type", "password"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("access_token").exists())
               .andDo(print());
    }
}