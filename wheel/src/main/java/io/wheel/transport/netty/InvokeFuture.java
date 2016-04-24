package io.wheel.transport.netty;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Invoke Future
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 * @param <V>
 */
public class InvokeFuture<T> {

	private T result;
	private Throwable cause;
	private Semaphore semaphore = new Semaphore(0);
	private AtomicBoolean done = new AtomicBoolean(false);
	private AtomicBoolean success = new AtomicBoolean(false);

	public boolean isDone() {
		return done.get();
	}

	public void setResult(T result) {
		this.result = result;
		done.set(true);
		success.set(true);
		semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
	}

	public T getResult(long timeout, TimeUnit unit) throws Exception {
		if (!isDone()) {
			if (!semaphore.tryAcquire(timeout, unit)) {
				setCause(new TimeoutException("Timeout for " + timeout + " " + unit));
			}
		}
		if (cause != null) {
			throw new RuntimeException(cause);
		}
		return this.result;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
		done.set(true);
		success.set(false);
		semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
	}

	public boolean isSuccess() {
		return success.get();
	}

	public Throwable getCause() {
		return cause;
	}
}
