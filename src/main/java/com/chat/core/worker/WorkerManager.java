package com.chat.core.worker;

import com.chat.common.config.ApplicationConfig;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class WorkerManager {
    public static Worker[] workers;

    public static void init() {
        workers = new Worker[ApplicationConfig.threadNum];
        for (int i = 0; i < ApplicationConfig.threadNum; i++) {
            workers[i] = new Worker();
        }
    }

    private static Worker findByMode(int id) {
        return workers[id % workers.length];
    }

    public static Object runInWorker(Integer userId, Method mapping, Object controller, Object data) throws Exception {
        CompletableFuture future = new CompletableFuture();
        Worker worker = WorkerManager.findByMode(room.getId());
        worker.submit(() -> {
            try {
                Object o = mapping.invoke(controller, userId, room, data);
                future.complete(o);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        Object o = future.get();
        return o;
    }

}
