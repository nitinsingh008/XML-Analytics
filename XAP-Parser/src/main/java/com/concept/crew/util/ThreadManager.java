package com.concept.crew.util;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 
 * ThreadManager is a predefined list of Worker Threads which can be re-used for
 * concurrent processing.<br>
 * <br>
 * 
 * List of predefined <b>ThreadManagers</b><br>
 * <i>UNO(1) - 1 Worker Thread</i><br>
 * <i>FIVE_GUYS(5) - 5 Worker Threads</i><br>
 * <i>SEVEN_ELEVEN(7, 11) - Minimum 7, Maximum 11 Worker Threads</i><br>
 * <i>BONE_COLLECTOR(10) - 10 Worker Threads</i><br>
 * <i>EXECUTIONS(5) - 5 Worker Threads</i><br>
 * <i>SELECTIONS(5) - 5 Worker Threads</i><br>
 * <i>GROUPS(5) - 5 Worker Threads</i><br>
 * <br>
 * 
 * Code base using the ThreadManger must call the static
 * <b>ThreadManager.shutdown()</b> method when the server/process if shutting
 * down.<br>
 * <br>
 * 
 * <b>ThreadManager.newCompletionService()</b>: Can be used to wrap the above
 * ThreadManagers to extract Future&lt;T&gt;s based on task completion time
 * instead of task submission time.<br>
 * <br>
 * <b>ThreadManager.handleTaskCompletion(List&lt;Future&lt;T&gt;&gt;,
 * <b>CompletionService&lt;T&gt;</b>, ...)</b> is a helper method to facilitate
 * the above.
 * 
 * 
 */
public enum ThreadManager {
	UNO(1), FIVE_GUYS(5), SEVEN_ELEVEN(7, 11), BONE_COLLECTOR(10), EXECUTIONS(5), SELECTIONS(5), GROUPS(5), //
	DOS_EQUIS(2), FAT_BOYS(10), KWIK_E_MART(14, 22), AMERICAN_GANSTER(20), HURRICANE(10), PICKING(10), CONGREGATION(10);

	private final ExecutorService executorService;
	private int workers;

	private ThreadManager(int workers) {
		this.executorService = Executors.newFixedThreadPool(workers);
		this.workers = workers;
	}

	private ThreadManager(int min, int max) {
		this.executorService = new ThreadPoolExecutor(min, // core size
				max, // max size
				10 * 60, // idle timeout
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		this.workers = min;
	}

	public ExecutorService getInstance() {
		return executorService;
	}

	public int getNumberOfWorkers() {
		return workers;
	}

	public void stop() {
		executorService.shutdownNow();
	}

	public static final <T> CompletionService<T> newCompletionService(final ThreadManager threadManager) {
		CompletionService<T> completionService = new ExecutorCompletionService<T>(threadManager.getInstance());
		return completionService;
	}

	public static final <T> CompletionService<T> newCompletionService(final String threadManager) {
		return newCompletionService(ThreadManager.valueOf(threadManager));
	}

	public static final <T> void handleTaskCompletion(final List<Future<T>> futures) throws InterruptedException, ExecutionException {
		try {
			for (Future<T> future : futures) {
				future.get();
			}
		} finally {
			ThreadManager.cancelTasks(futures);
		}
	}

	public static final <T> void handleTaskCompletion(final List<Future<T>> futures, final Command<T> command) throws InterruptedException, ExecutionException {
		try {
			for (Future<T> future : futures) {
				T t = future.get();
				command.execute(t);
			}
		} finally {
			ThreadManager.cancelTasks(futures);
		}
	}

	public static final <T> void handleTaskCompletion(final List<Future<T>> futures, final CompletionService<T> completionService) throws InterruptedException, ExecutionException {
		try {
			for (int i = 0; i < futures.size(); i++) {
				Future<T> future = completionService.take();
				future.get();
			}
		} finally {
			ThreadManager.cancelTasks(futures);
		}
	}

	public static final <T> void handleTaskCompletion(final List<Future<T>> futures, final CompletionService<T> completionService, final Command<T> command) throws InterruptedException, ExecutionException {
		try {
			for (int i = 0; i < futures.size(); i++) {
				Future<T> future = completionService.take();
				T t = future.get();
				command.execute(t);
			}
		} finally {
			ThreadManager.cancelTasks(futures);
		}
	}

	public static final <T> void cancelTasks(final List<Future<T>> tasks) {
		for (Future<T> task : tasks) {
			task.cancel(true);
		}
	}

	public static final void checkInterruption() {
		if (Thread.currentThread().isInterrupted()) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Thread interrupted");
		}
	}

	public static final void shutdown() {
		for (ThreadManager threadManager : ThreadManager.values()) {
			threadManager.stop();
		}
	}

	public static final class VoidCommand implements Command<Void> {
		@Override
		public void execute(Void v) {
			// nothing to do
		}
	}

	public static void main(String[] args) {
		ThreadManager.valueOf("UNO");
	}

}
