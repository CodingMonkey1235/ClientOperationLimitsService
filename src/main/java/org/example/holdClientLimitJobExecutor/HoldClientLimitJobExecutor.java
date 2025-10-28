package org.example.holdClientLimitJobExecutor;

import org.example.config.ClientsLimitsConfiguration;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class HoldClientLimitJobExecutor {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private HashMap<UUID, ScheduledFuture> featureTasks = new HashMap<>();
    private HashMap<UUID, HoldClientLimitTask> runnableTasks = new HashMap<>();

    private ClientsLimitsConfiguration clientsLimitsConfiguration;

    public HoldClientLimitJobExecutor() {}

    public UUID addTask(UUID uuid, HoldClientLimitTask task) {
        int delay = clientsLimitsConfiguration.getCancelLimitDelay();
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(task, delay, TimeUnit.MINUTES);
        featureTasks.put(uuid,scheduledFuture);
        runnableTasks.put(uuid,task);
        return uuid;
    }

    // не хватило времени разобраться с ScheduledFuture<?>
    public void cancelTask(UUID uuid) {
        HoldClientLimitTask runnableTask = runnableTasks.get(uuid);
        if (runnableTask != null) { runnableTask.setIsCancelled(true); }
        runnableTasks.remove(uuid);

        ScheduledFuture<?> scheduledFuture = featureTasks.remove(uuid);
        scheduledFuture.cancel(true);
    }

}
