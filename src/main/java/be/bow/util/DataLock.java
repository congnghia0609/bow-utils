package be.bow.util;

import be.bow.ui.UI;

import java.util.concurrent.Semaphore;

public class DataLock {

    private static final int NUM_OF_LOCKS = 1000; //Some number an order higher then number of expected simultaneous threads.
    private static final int NUM_OF_PERMITS = 100;

    private final boolean debug;
    private final Semaphore[] locks;

    public DataLock() {
        this(false);
    }

    public DataLock(boolean debug) {
        this.locks = new Semaphore[NUM_OF_LOCKS];
        for (int i = 0; i < NUM_OF_LOCKS; i++) {
            this.locks[i] = new Semaphore(NUM_OF_PERMITS, false);
        }
        this.debug = debug;
    }

    public void lockRead(long key) {
        int lockInd = getLockInd(key);
        locks[lockInd].acquireUninterruptibly(1);
        if (debug) {
            UI.writeStackTrace("Locked read " + lockInd);
        }
    }

    public void unlockRead(long key) {
        int lockInd = getLockInd(key);
        locks[lockInd].release(1);
        if (debug) {
            UI.writeStackTrace("Unlocked read " + lockInd);
        }
    }

    public void lockWrite(long key) {
        int lockInd = getLockInd(key);
        locks[lockInd].acquireUninterruptibly(NUM_OF_PERMITS);
        if (debug) {
            UI.writeStackTrace("Locked write " + lockInd);
        }
    }

    public void unlockWrite(long key) {
        int lockInd = getLockInd(key);
        locks[lockInd].release(NUM_OF_PERMITS);
        if (debug) {
            UI.writeStackTrace("Unlocked write " + lockInd);
        }
    }

    private int getLockInd(long key) {
        if (key < 0) {
            key = -key;
        }
        return (int) (key % NUM_OF_LOCKS);
    }

    public void lockReadAll() {
        for (Semaphore lock : locks) {
            lock.acquireUninterruptibly(1);
        }
        if (debug) {
            UI.writeStackTrace("Locked all read");
        }
    }

    public void unlockReadAll() {
        for (Semaphore lock : locks) {
            lock.release(1);
        }
        if (debug) {
            UI.writeStackTrace("Unlocked all read");
        }
    }

    public void lockWriteAll() {
        for (Semaphore lock : locks) {
            lock.acquireUninterruptibly(NUM_OF_PERMITS);
        }
        if (debug) {
            UI.writeStackTrace("Locked all write");
        }
    }

    public void unlockWriteAll() {
        for (Semaphore lock : locks) {
            lock.release(NUM_OF_PERMITS);
        }
        if (debug) {
            UI.writeStackTrace("Unlocked all write");
        }
    }
}
