package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;
import static edu.rice.pcdp.PCDP.finish;
import static edu.rice.pcdp.PCDP.async;

public final class SieveActor extends Sieve {
    @Override
    public int countPrimes(final int limit) {
	 SieveActorActor sieveActor2 = new SieveActorActor(2, 1);
	 finish(() -> {
	     async(() -> {
	      for (int i = 2; i <= limit; i++) {
	          sieveActor2.process(i);
	      }
	     });
	 });

	 int numPrimes = 0;
	 SieveActorActor nextActor = sieveActor2;
	 while (nextActor != null) {
	     numPrimes += 1;
	     nextActor = nextActor.nextActor;
	 }
	 return numPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
	private int localPrime;
	private SieveActorActor nextActor;
	private int numPrimes;

	SieveActorActor(int localPrime, int numPrimes) {
	    this.localPrime = localPrime;
	    this.numPrimes = numPrimes;
	}

        @Override
        public void process(final Object msg) {
	    Integer msgInt = (Integer) msg;

	    if ((Integer) msg % localPrime != 0) {
		if (this.nextActor == null) {
		    numPrimes += 1;
		    nextActor = new SieveActorActor(msgInt, numPrimes);
		} else {
		    nextActor.send(msgInt);
		}
	    }
        }

	public int getNumPrimes() {
	    return numPrimes;
	}
    }
}
