package ru.demo.cqrs;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskProjection {
    private final Map<String, TaskReadModel> readStore = new ConcurrentHashMap<>();

    public void apply(TaskCreatedEvent event) {
        readStore.put(event.taskId(), new TaskReadModel(
                event.taskId(), event.title(), false, event.createdAt(), null));
    }

    public void apply(TaskCompletedEvent event) {
        readStore.computeIfPresent(event.taskId(), (id, task) -> new TaskReadModel(
                task.id(), task.title(), true, task.createdAt(), event.completedAt()));
    }

    public List<TaskReadModel> findAll() {
        return readStore.values().stream()
                .sorted(Comparator.comparing(TaskReadModel::createdAt).reversed())
                .toList();
    }

    public TaskReadModel findById(String id) {
        return readStore.get(id);
    }
}
