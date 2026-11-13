package ru.demo.cqrs;

public record TaskReadModel(String id, String title, boolean completed, java.time.Instant createdAt, java.time.Instant completedAt) { }
