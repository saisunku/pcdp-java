package edu.coursera.concurrent;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * Wrapper class for two lock-based concurrent list implementations.
 */
public final class CoarseLists {
    /**
     * An implementation of the ListSet interface that uses Java locks to
     * protect against concurrent accesses.
     */
    public static final class CoarseList extends ListSet {
	private ReentrantLock lock;
        /**
         * Default constructor.
         */
        public CoarseList() {
            super();
	    lock = new ReentrantLock();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean add(final Integer object) {
	    lock.lock();
	    try {
            	Entry pred = this.head;
            	Entry curr = pred.next;

            	while (curr.object.compareTo(object) < 0) {
            	    pred = curr;
            	    curr = curr.next;
            	}

            	if (object.equals(curr.object)) {
            	    return false;
            	} else {
            	    final Entry entry = new Entry(object);
            	    entry.next = curr;
            	    pred.next = entry;
            	    return true;
            	}
	    } finally {
		lock.unlock();
	    }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean remove(final Integer object) {
	    lock.lock();
	    try {
            	Entry pred = this.head;
            	Entry curr = pred.next;

            	while (curr.object.compareTo(object) < 0) {
            	    pred = curr;
            	    curr = curr.next;
            	}

            	if (object.equals(curr.object)) {
            	    pred.next = curr.next;
            	    return true;
            	} else {
            	    return false;
            	}
	    } finally {
		lock.unlock();
	    }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean contains(final Integer object) {
	    lock.lock();
	    try {
            	Entry pred = this.head;
            	Entry curr = pred.next;

            	while (curr.object.compareTo(object) < 0) {
            	    pred = curr;
            	    curr = curr.next;
            	}
            	return object.equals(curr.object);
	    } finally {
		lock.unlock();
	    }
        }
    }

    /**
     * An implementation of the ListSet interface that uses Java read-write
     * locks to protect against concurrent accesses.
     *
     */
    public static final class RWCoarseList extends ListSet {
	private ReentrantReadWriteLock readWriteLock;
	private ReadLock readLock;
	private WriteLock writeLock;

        /**
         * Default constructor.
         */
        public RWCoarseList() {
            super();
	    readWriteLock = new ReentrantReadWriteLock();
	    readLock = readWriteLock.readLock();
	    writeLock = readWriteLock.writeLock();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean add(final Integer object) {
	    writeLock.lock();
	    try {
            	Entry pred = this.head;
            	Entry curr = pred.next;

            	while (curr.object.compareTo(object) < 0) {
            	    pred = curr;
            	    curr = curr.next;
            	}

            	if (object.equals(curr.object)) {
            	    return false;
            	} else {
            	    final Entry entry = new Entry(object);
            	    entry.next = curr;
            	    pred.next = entry;
            	    return true;
            	}
	    } finally {
		writeLock.unlock();
	    }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean remove(final Integer object) {
	    writeLock.lock();
	    try {
            	Entry pred = this.head;
            	Entry curr = pred.next;

            	while (curr.object.compareTo(object) < 0) {
            	    pred = curr;
            	    curr = curr.next;
            	}

            	if (object.equals(curr.object)) {
            	    pred.next = curr.next;
            	    return true;
            	} else {
            	    return false;
            	}
	    } finally {
		writeLock.unlock();
	    }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean contains(final Integer object) {
	    readLock.lock();
	    try {
            Entry pred = this.head;
            Entry curr = pred.next;

            while (curr.object.compareTo(object) < 0) {
                pred = curr;
                curr = curr.next;
            }
            return object.equals(curr.object);
	    } finally {
		readLock.unlock();
	    }
        }
    }
}
