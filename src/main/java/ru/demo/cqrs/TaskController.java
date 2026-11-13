package ru.demo.cqrs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskCommandService commands;
    private final TaskProjection queries;

    public TaskController(TaskCommandService commands, TaskProjection queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @PostMapping("/commands/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> create(@RequestBody CreateTaskRequest request) {
        return Map.of("id", commands.createTask(request.title()));
    }

    @PostMapping("/commands/tasks/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable String id) {
        commands.completeTask(id);
    }

    @GetMapping("/queries/tasks")
    public List<TaskReadModel> list() {
        return queries.findAll();
    }

    @GetMapping("/queries/tasks/{id}")
    public TaskReadModel get(@PathVariable String id) {
        TaskReadModel task = queries.findById(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        return task;
    }

    public record CreateTaskRequest(String title) { }
}
