package com.example.notifications.command;

import java.util.LinkedList;
import java.util.Queue;

public class NotificationCommandInvoker {

    private final Queue<NotificationCommand> queue = new LinkedList<>();

    public void addCommand(NotificationCommand command) {
        queue.add(command);
    }

    public void executeAll() {
        while (!queue.isEmpty()) {
            queue.poll().execute();
        }
    }
}
