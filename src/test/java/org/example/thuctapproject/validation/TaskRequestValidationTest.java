package org.example.thuctapproject.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.thuctapproject.model.request.TaskRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TaskRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldFailWhenTitleBlankOrTooShort() {
        TaskRequest req = new TaskRequest();
        req.setTitle(" "); // blank
        req.setStatus("TODO");
        req.setProject(1);
        req.setAssignee(1);
        req.setDeadline(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")),
                "Expected validation error on title");
    }

    @Test
    void shouldFailWhenDeadlineNotInFuture() {
        TaskRequest req = new TaskRequest();
        req.setTitle("Valid title");
        req.setStatus("TODO");
        req.setProject(1);
        req.setAssignee(1);
        req.setDeadline(LocalDate.now().minusDays(1)); // past

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deadline")),
                "Expected validation error on deadline being in the past");
    }

    @Test
    void shouldPassWithValidRequest() {
        TaskRequest req = new TaskRequest();
        req.setTitle("Implement validation");
        req.setStatus("TODO");
        req.setProject(1);
        req.setAssignee(1);
        req.setDeadline(LocalDate.now().plusDays(3));

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "No validation errors expected");
    }
}
