package ru.demo.cqrs;

import java.time.Instant;

public record TaskCreatedEvent(String taskId, String title, Instant createdAt) { }
