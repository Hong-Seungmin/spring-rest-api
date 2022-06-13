package com.example.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
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
               .andExpect(jsonPath("id").value(Matchers.not(100)))
               .andExpect(jsonPath("free").value(Matchers.not(true)))
               .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
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
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isBadRequest());
    }
}
