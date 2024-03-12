package com.dws.challenge.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLock<K> {
    Logger logger = Logger.getLogger(String.valueOf(CustomLock.class));
    private final Set<K> lockedKeys = new HashSet<>();

    public void lock(K key) throws InterruptedException {
        synchronized (lockedKeys) {

            while (!lockedKeys.add(key)) {
                logger.log(Level.INFO,() -> "Waiting for the key " + key);
                lockedKeys.wait();
            }
            logger.log(Level.INFO,() -> "Locking key " + key);
        }
    }

    public void unlock(K key) {
        synchronized (lockedKeys) {
            lockedKeys.remove(key);
            logger.log(Level.INFO,() -> "Releasing key " + key);
            lockedKeys.notifyAll();
        }
    }

}
