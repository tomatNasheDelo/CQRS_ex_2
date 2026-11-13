package ru.demo.cqrs;

import java.time.Instant;

public record TaskCompletedEvent(String taskId, Instant completedAt) { }
