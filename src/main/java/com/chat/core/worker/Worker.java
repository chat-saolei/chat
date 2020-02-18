package com.chat.core.worker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class Worker extends Thread {

    private LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    public void run() {
        for (; ; ) {
            try {
                Runnable task = tasks.take();
                task.run();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
    public void submit(Runnable task) {
        tasks.add(task);
    }
}
