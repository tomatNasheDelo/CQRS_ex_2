package ru.demo.cqrs;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskCommandService {
    private final Map<String, WriteTask> writeStore = new ConcurrentHashMap<>();
    private final TaskProjection taskProjection;

    public TaskCommandService(TaskProjection taskProjection) {
        this.taskProjection = taskProjection;
    }

    public String createTask(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        String normalized = title.trim();
        String id = UUID.randomUUID().toString();
        Instant now = Instant.now();
        writeStore.put(id, new WriteTask(id, normalized, false, now, null));
        taskProjection.apply(new TaskCreatedEvent(id, normalized, now));
        return id;
    }

    public void completeTask(String id) {
        WriteTask current = writeStore.get(id);
        if (current == null) {
            throw new TaskNotFoundException(id);
        }
        if (current.completed()) {
            throw new IllegalStateException("Task is already completed");
        }
        Instant now = Instant.now();
        writeStore.put(id, new WriteTask(id, current.title(), true, current.createdAt(), now));
        taskProjection.apply(new TaskCompletedEvent(id, now));
    }

    private record WriteTask(String id, String title, boolean completed, Instant createdAt, Instant completedAt) { }
}
