package main;

import java.math.BigInteger;
import java.util.concurrent.Callable;

/**
 * Hey! It tests for primes upon construction. What more do ya want.
 * @author jaydenlefebvre
 *
 */
public class PrimalityTester implements Callable<Integer> {
	private int i;
	//long startTime;

	/**
	 * Instantiates a single primality tester. Tests to see if i is prime.
	 * @param i - int to test
	 */
	public PrimalityTester(int i) {
		//System.out.println("Made "+i+"!");
		this.i = i;
		//startTime = System.currentTimeMillis();
	}

	@Override
	public Integer call() throws Exception {
		//System.out.println("Called "+i+"!");
		boolean b = isMersennePrime(i);
		//System.out.println(i+" took "+(System.currentTimeMillis()-startTime)+"ms");
		return (b ? i : -1);
	}

	/**
	 * Uses Lucas-Lehmer primality test to determine if a number is prime
	 * @param i - i where 2^i-1 is a Mersenne prime
	 * @return Boolean, is prime?
	 */
	public static boolean isMersennePrime(int i) {	//Lucas-Lehmer primality test
		//System.out.println("Testing "+i+"!");
		if (i == 2) {	//2^i-1 = 3, p = 6 case
			return true;
		} else {
			BigInteger mersenne = BigInteger.valueOf(2).pow(i).subtract(BigInteger.ONE);	//2^i-1
			BigInteger s = BigInteger.valueOf(4);	//4
			for (int x = 3; x <= i; x++) {
				s = s.pow(2).subtract(BigInteger.valueOf(2)).mod(mersenne);	//s = s^2-2 % 2^i-1
			}
			return s.equals(BigInteger.ZERO);
		}
	}
}
