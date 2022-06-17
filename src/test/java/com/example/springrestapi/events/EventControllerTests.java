package com.example.springrestapi.events;

import com.example.springrestapi.accounts.Account;
import com.example.springrestapi.accounts.AccountRepository;
import com.example.springrestapi.accounts.AccountRole;
import com.example.springrestapi.accounts.AccountService;
import com.example.springrestapi.common.BaseControllerTest;
import com.example.springrestapi.common.TestDesctiption;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Before
    public void setUp() {
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @TestDesctiption("정상적으로 이벤트를 생성하는 메소드")
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                                 .name("Spring")
                                 .description("REST API Development with Spring")
                                 .beginEnrollmentDateTime(LocalDateTime.of(2022, 06, 12, 22, 00))
                                 .closeEnrollmentDateTime(LocalDateTime.of(2022, 06, 13, 22, 00))
                                 .beginEventDateTime(LocalDateTime.of(2022, 06, 14, 22, 00))
                                 .endEventDateTime(LocalDateTime.of(2022, 06, 15, 22, 00))
                                 .basePrice(100)
                                 .maxPrice(200)
                                 .limitOfEnrollment(100)
                                 .location("강남역 D2 스타텁 팩토리")
                                 .build();

//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(event)))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(jsonPath("id").exists())
               .andExpect(header().exists(HttpHeaders.LOCATION))
               .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
               .andExpect(jsonPath("free").value(false))
               .andExpect(jsonPath("offline").value(true))
               .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
               .andExpect(jsonPath("_links.self").exists())
               .andExpect(jsonPath("_links.query-events").exists())
               .andExpect(jsonPath("_links.update-event").exists())
               .andDo(document("create-event",
                               links(
                                       linkWithRel("self").description("link to self"),
                                       linkWithRel("query-events").description("link to query events"),
                                       linkWithRel("update-event").description("link to update an existing event"),
                                       linkWithRel("profile").description("link to profile")),
                               requestHeaders(
                                       headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                       headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                               ),
                               requestFields(
                                       fieldWithPath("name").description("Name of new event"),
                                       fieldWithPath("description").description("desctiption of new event"),
                                       fieldWithPath("beginEnrollmentDateTime").description(
                                               "date time of begin of event"),
                                       fieldWithPath("closeEnrollmentDateTime").description(
                                               "date time of close of event"),
                                       fieldWithPath("beginEventDateTime").description(
                                               "date time of close of event"),
                                       fieldWithPath("endEventDateTime").description(
                                               "endEventDateTime"),
                                       fieldWithPath("location").description(
                                               "location"),
                                       fieldWithPath("basePrice").description(
                                               "기본가"),
                                       fieldWithPath("maxPrice").description(
                                               "최대가"),
                                       fieldWithPath("limitOfEnrollment").description(
                                               "limitOfEnrollment")
                               ),
                               responseHeaders(
                                       headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                       headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                               ),
                               relaxedResponseFields(
                                       fieldWithPath("_links.*.*").ignored(),
                                       fieldWithPath("id").description("id"),
                                       fieldWithPath("name").description("Name of new event"),
                                       fieldWithPath("description").description("desctiption of new event"),
                                       fieldWithPath("beginEnrollmentDateTime").description(
                                               "date time of begin of event"),
                                       fieldWithPath("closeEnrollmentDateTime").description(
                                               "date time of close of event"),
                                       fieldWithPath("beginEventDateTime").description(
                                               "date time of close of event"),
                                       fieldWithPath("endEventDateTime").description(
                                               "endEventDateTime"),
                                       fieldWithPath("location").description(
                                               "location"),
                                       fieldWithPath("basePrice").description(
                                               "basePrice"),
                                       fieldWithPath("maxPrice").description(
                                               "maxPrice"),
                                       fieldWithPath("limitOfEnrollment").description(
                                               "limitOfEnrollment"),
                                       fieldWithPath("free").description(
                                               "free"),
                                       fieldWithPath("offline").description(
                                               "offline"),
                                       fieldWithPath("eventStatus").description("eventStatus")
                               )

                      )
               )
        ;
    }

    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    private String getAccessToken() throws Exception {
        String username = "hong1@gmail.com";
        String passsword = "honghong";
        Account account = Account.builder()
                                 .email(username)
                                 .passsword(passsword)
                                 .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                 .build();

        accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                                                        .with(httpBasic(clientId, clientSecret))
                                                        .param("username", username)
                                                        .param("password", passsword)
                                                        .param("grant_type", "password"));
        String responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    @Test
    @TestDesctiption("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {

        Event event = Event.builder()
                           .id(100)
                           .name("Spring")
                           .description("REST API Development with Spring")
                           .beginEnrollmentDateTime(LocalDateTime.of(2022, 6, 12, 22, 00))
                           .closeEnrollmentDateTime(LocalDateTime.of(2022, 6, 13, 22, 00))
                           .beginEventDateTime(LocalDateTime.of(2022, 6, 14, 22, 00))
                           .endEventDateTime(LocalDateTime.of(2022, 6, 15, 22, 00))
                           .basePrice(100)
                           .maxPrice(200)
                           .limitOfEnrollment(100)
                           .location("강남역 D2 스타텁 팩토리")
                           .free(true)
                           .offline(false)
                           .eventStatus(EventStatus.PUBLISHED)
                           .build();

//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(event)))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    @TestDesctiption("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                                     .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isBadRequest());
    }

    @Test
    @TestDesctiption("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                                    .name("Spring")
                                    .description("REST API Development with Spring")
                                    .beginEnrollmentDateTime(LocalDateTime.of(2022, 06, 12, 22, 00))
                                    .closeEnrollmentDateTime(LocalDateTime.of(2022, 06, 13, 22, 00))
                                    .beginEventDateTime(LocalDateTime.of(2022, 06, 14, 22, 00))
                                    .endEventDateTime(LocalDateTime.of(2022, 06, 15, 22, 00))
                                    .basePrice(10000)
                                    .maxPrice(200)
                                    .limitOfEnrollment(100)
                                    .location("강남역 D2 스타텁 팩토리")
                                    .build();

        this.mockMvc.perform(post("/api/events")
                                     .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors[0].objectName").exists())
                    .andExpect(jsonPath("errors[0].defaultMessage").exists())
                    .andExpect(jsonPath("errors[0].code").exists())
                    .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    public void testFree() {
        Event event = Event.builder()
                           .basePrice(0)
                           .maxPrice(0)
                           .build();

        event.update();

        assertThat(event.isFree()).isTrue();

        event = Event.builder()
                     .basePrice(100)
                     .maxPrice(0)
                     .build();

        event.update();

        assertThat(event.isFree()).isFalse();

        event = Event.builder()
                     .basePrice(0)
                     .maxPrice(100)
                     .build();

        event.update();

        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline() {
        Event event = Event.builder()
                           .location("강남역 네이버 D2 스타텁 팩토리")
                           .build();

        event.update();

        assertThat(event.isOffline()).isTrue();

        event = Event.builder()
                     .build();

        event.update();

        assertThat(event.isOffline()).isFalse();
    }

    @Test
    @TestDesctiption("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        IntStream.range(0, 30).forEach(this::generateEvent);

        mockMvc.perform(get("/api/events")
                                .param("page", "1")
                                .param("size", "10")
                                .param("sort", "name,DESC")
               )
               .andExpect(status().isOk())
               .andExpect(jsonPath("page").exists())
               .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
               .andExpect(jsonPath("_links.self").exists())
               .andExpect(jsonPath("_links.profile").exists())
               .andDo(document("query-events"))
               .andDo(print())

        ;

    }

    @Test
    @TestDesctiption("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {
        IntStream.range(0, 30).forEach(this::generateEvent);

        mockMvc.perform(get("/api/events")
                                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                                .param("page", "1")
                                .param("size", "10")
                                .param("sort", "name,DESC")
               )
               .andExpect(status().isOk())
               .andExpect(jsonPath("page").exists())
               .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
               .andExpect(jsonPath("_links.self").exists())
               .andExpect(jsonPath("_links.profile").exists())
               .andExpect(jsonPath("_links.create-event").exists())
               .andDo(document("query-events"))
               .andDo(print())

        ;

    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                           .name("Spring")
                           .description("REST API Development with Spring")
                           .beginEnrollmentDateTime(LocalDateTime.of(2022, 06, 12, 22, 00))
                           .closeEnrollmentDateTime(LocalDateTime.of(2022, 06, 13, 22, 00))
                           .beginEventDateTime(LocalDateTime.of(2022, 06, 14, 22, 00))
                           .endEventDateTime(LocalDateTime.of(2022, 06, 15, 22, 00))
                           .basePrice(100)
                           .maxPrice(200)
                           .limitOfEnrollment(100)
                           .free(false)
                           .offline(true)
                           .eventStatus(EventStatus.DRAFT)
                           .location("강남역 D2 스타텁 팩토리")
                           .build();

        return eventRepository.save(event);
    }
}
