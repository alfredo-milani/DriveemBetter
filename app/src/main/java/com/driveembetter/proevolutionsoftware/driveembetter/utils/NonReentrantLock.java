package com.driveembetter.proevolutionsoftware.driveembetter.utils;

/**
 * Created by alfredo on 08/10/17.
 */

public class NonReentrantLock {

    private final static String TAG = NonReentrantLock.class.getSimpleName();

    private boolean isLocked = false;

    public synchronized void lock() {
        while (this.isLocked) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        this.isLocked = true;
    }

    public synchronized void unlock() {
        this.isLocked = false;
        this.notify();
    }

    public synchronized boolean isLocked() {
        return this.isLocked;
    }
}