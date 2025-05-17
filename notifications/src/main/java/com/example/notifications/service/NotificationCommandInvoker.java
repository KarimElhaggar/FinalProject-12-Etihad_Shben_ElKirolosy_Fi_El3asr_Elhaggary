package com.example.notifications.service;

import com.example.notifications.command.NotificationCommand;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NotificationCommandInvoker {
    private NotificationCommand command;
    private final Queue<NotificationCommand> queue = new LinkedList<>();
    private final Deque<NotificationCommand> history = new ArrayDeque<>();

    public void addCommand(NotificationCommand command) {
        queue.add(command);
    }

    public void setCommand(NotificationCommand command) {
        this.command = command;
    }

    public void executeCommand() {
        command.execute();
        history.push(command);
    }

    public void undoCommand() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }

    public void executeAll() {
        while (!queue.isEmpty()) {
            NotificationCommand cmd = queue.poll();
            cmd.execute();
            history.push(cmd);
        }
    }
}
