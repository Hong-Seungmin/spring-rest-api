package com.example.springrestapi.events;

import com.example.springrestapi.common.RestDocsConfiguration;
import com.example.springrestapi.common.TestDesctiption;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

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
                               responseFields(
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

    private void generateEvent(int i) {
        Event event = Event.builder()
                           .name("event " + i)
                           .description("test event")
                           .build();

        eventRepository.save(event);
    }
}
