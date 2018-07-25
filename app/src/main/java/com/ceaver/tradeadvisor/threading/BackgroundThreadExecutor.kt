package com.ceaver.tradeadvisor.threading

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object BackgroundThreadExecutor : ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors() * 2,
        Runtime.getRuntime().availableProcessors() * 2,
        10, TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>(),
        BackgroundPriorityThreadFactory()) {
}

private class BackgroundPriorityThreadFactory : ThreadFactory {
    override fun newThread(runnable: Runnable?): Thread {
        return Thread(Runnable { android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND); runnable!!.run() })
    }
}