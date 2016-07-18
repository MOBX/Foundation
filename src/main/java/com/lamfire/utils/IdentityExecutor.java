package com.lamfire.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 带标识的多线程执行器
 */
public class IdentityExecutor {

    private final Set<Serializable> taskIds = Sets.newHashSet();
    private ExecutorService         service;

    public IdentityExecutor(ExecutorService service) {
        this.service = service;
    }

    public Set<Serializable> getWaitingTaskIds() {
        return taskIds;
    }

    /**
     * 提交任务
     * 
     * @param id
     * @param task
     * @return
     * @throws TaskExistsException 当该任务ID还存在于队列中,则抛出异常
     */
    public synchronized Future<?> submit(Serializable id, Runnable task) throws TaskExistsException {
        if (taskIds.contains(id)) {
            throw new TaskExistsException("Failed submit task,identity exists - " + id.toString());
        }
        taskIds.add(id);
        return service.submit(new Task(id, task));
    }

    public void shutdown() {
        service.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return service.shutdownNow();
    }

    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        service.awaitTermination(timeout, unit);
    }

    public class TaskExistsException extends Exception {

        private static final long serialVersionUID = 3792731443027168307L;

        public TaskExistsException(String message) {
            super(message);
        }
    }

    private class Task implements Runnable {

        private Serializable id;
        private Runnable     realTask;

        public Task(Serializable id, Runnable runTask) {
            this.id = id;
            this.realTask = runTask;
        }

        @Override
        public void run() {
            try {
                this.realTask.run();
            } finally {
                taskIds.remove(id);
            }
        }
    }
}
