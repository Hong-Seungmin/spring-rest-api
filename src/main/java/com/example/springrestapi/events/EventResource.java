package com.example.springrestapi.events;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {
    public EventResource(Event content) {
        super(content);
        add(linkTo(EventController.class).slash(content.getId()).withSelfRel());
    }
}
