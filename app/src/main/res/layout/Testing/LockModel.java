package com.tgs.lock.Testing;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class LockModel implements Serializable {
    Boolean lock;

    public LockModel() {

    }

    public LockModel(Boolean lock) {
        this.lock = lock;
    }

    public Boolean getLock() {
        return lock;
    }

    public void setLock(Boolean lock) {
        this.lock = lock;
    }
}
