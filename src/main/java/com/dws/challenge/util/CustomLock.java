package com.dws.challenge.util;

import java.util.HashSet;
import java.util.Set;

public class CustomLock<K> {

    private /*static*/ final Set<K> lockedKeys = new HashSet<>();

    public /*static*/ void lock(K key) throws InterruptedException {
        synchronized (lockedKeys) {

            while (!lockedKeys.add(key)) {
                System.out.println("Waiting for the key " + key);
                lockedKeys.wait();
            }
            System.out.println("Locking key " + key);
        }
    }

    public /*static*/ void unlock(K key) {
        synchronized (lockedKeys) {
            lockedKeys.remove(key);
            System.out.println("Releasing key " + key);
            lockedKeys.notifyAll();
        }
    }

}
