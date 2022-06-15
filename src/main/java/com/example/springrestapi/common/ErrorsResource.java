package com.example.springrestapi.common;

import com.example.springrestapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {

    public ErrorsResource(Errors content) {
        super(content);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
