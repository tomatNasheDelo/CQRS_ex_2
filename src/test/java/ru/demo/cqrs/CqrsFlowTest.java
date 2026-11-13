package ru.demo.cqrs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CqrsFlowTest {
    @Test
    void commandChangesWriteStateAndUpdatesReadProjection() {
        TaskProjection projection = new TaskProjection();
        TaskCommandService commands = new TaskCommandService(projection);

        String id = commands.createTask(" Learn CQRS ");
        TaskReadModel created = projection.findById(id);
        assertNotNull(created);
        assertEquals("Learn CQRS", created.title());
        assertFalse(created.completed());

        commands.completeTask(id);
        TaskReadModel completed = projection.findById(id);
        assertTrue(completed.completed());
        assertNotNull(completed.completedAt());
    }

    @Test
    void rejectsBlankTitleAndCompletingUnknownTask() {
        TaskCommandService commands = new TaskCommandService(new TaskProjection());
        assertThrows(IllegalArgumentException.class, () -> commands.createTask("  "));
        assertThrows(TaskNotFoundException.class, () -> commands.completeTask("missing"));
    }
}
