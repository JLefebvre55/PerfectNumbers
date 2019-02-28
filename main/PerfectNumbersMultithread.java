package main;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import main.PrimalityTester;

/**
 * Holy shmoly.
 * Manages, creates, instantiates, executes, checks, collects, replaces, and prints results from primality testers.
 * Only for really fast bois.
 * @author jaydenlefebvre
 *
 */
public class PerfectNumbersMultithread {

	//private static Scanner in = new Scanner(System.in);

	public static void main(String[] args) {

		int threadcount = 8;

		if(args.length > 0) {
			threadcount = Integer.parseInt(args[0]);
		}


		//Intro speil
		Date date = new Date();
		String [] sdate = date.toString().split(" ");
		System.out.println("The date is "+sdate[0]+", "+sdate[1]+" "+sdate[2]+", "+sdate[5]+". The time is "+sdate[3]+".");

		date.setTime(date.getTime()+604800000);
		sdate = date.toString().split(" ");
		System.out.println("This program is intended to run for 1 week, and will end on "+sdate[0]+", "+sdate[1]+" "+sdate[2]+", "+sdate[5]+", at "+sdate[3]+".");

		System.out.println("This program finds perfect numbers. A perfect number is when a number is the sum of its divisors (not including itself).\nI.e. 6 = 1 + 2 + 3 = 1 * 6 = 2 * 3");
		System.out.println("This particular instantiation uses the powers of 2, the Lucas-Lehmmer primality test, and asynchronous multithreading (results may be out of order).");

		/*System.out.println("\nPress enter to start...");
		in.nextLine();*/

		//Make ExecutorService, thread pool size is 8. In charge of asynchronous thread management
		ExecutorService executor = Executors.newFixedThreadPool(threadcount);

		//create a list to hold the Future objects representing runtime returns of primality testers
		List<Future<Integer>> list = new ArrayList<Future<Integer>>();

		int prime = 2;	//Starting prime

		/**
		 * Make one prime tester for each thread, initialize to prime, prime+1, prime+2...
		 * Add to executor
		 */
		for(int i=0; i< threadcount; i++){
			//add Future to the list, we can get return value using Future
			list.add(executor.submit(new PrimalityTester(prime)));
			prime++;
		}

		/**
		 * Forever, loop through each list element (Future "Thread").
		 * If the thread is done, get return value and then {
		 * If number (i) was prime (!= -1), calculate 2^i-1 and output.
		 * Remove thread object from list
		 * Add new prime tester to list using next prime
		 * Increment prime
		 * }
		 * catch errors
		 */
		final long startTime = System.currentTimeMillis();
		int count = 1;
		while(true) {
			for(int x = 0; x < list.size(); x++){
				try {
					Future<Integer> fut = list.get(x);
					if(fut.isDone()) {
						int i = fut.get();

						if(i != -1) {

							BigInteger p = BigInteger.valueOf(2).pow(i).subtract(BigInteger.ONE).multiply(BigInteger.valueOf(2).pow(i-1));

							long time = System.currentTimeMillis();	//Current system time

							//System.out.println(count+": ("+i+", "+formatTime(time-startTime)+")\t"+formatBigNumber(p));	//Print
							System.out.format("%1$2s:%3$30s%2$20s\n", count, formatTime(time-startTime), formatBigNumber(p));
							count++;
						}
						list.remove(fut);
						list.add(executor.submit(new PrimalityTester(prime)));
						prime++;
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Formats big numbers as scientific notation
	 * @param b - BigInteger to notate
	 * @return String of sci. notated value
	 */
	public static String formatBigNumber(BigInteger b) {
		String sp = b.toString();
		return (sp.length()>6 ? sp.charAt(0)+"."+ (sp.length()>1 ? (sp.length()>2 ? sp.substring(1, 3) : sp.substring(1, 2)+"0") : "00")+"E"+(sp.length()-1) : sp);
	}

	/**
	 * Formats time properly, up to the hours
	 * @param time - time in ms
	 * @return String of formatted text
	 */
	public static String formatTime(long time) {
		long seconds = time/1000;
		if(time < 1000) {
			return ""+time+"ms";
		} else if(seconds < 60) {
			return ""+String.format("%.2f", ((double)time/1000))+"s";
		} else if(seconds < 3600){
			return ""+seconds/60+(seconds/60 == 1 ? " min, " : " mins, ")+String.format("%.2f", (((double)seconds/60)%1)*60)+"s";
		} else if(seconds < 86400){
			return ""+seconds/3600+(seconds/3600 == 1 ? " hour, " : " hours, ")+(int)((seconds/3600.0)%1*60)+((int)((seconds/3600.0)%1*60) == 1 ? " min, " : " mins, ")+String.format("%.2f", ((seconds/3600.0)%1*60)%1*60)+"s";
		} else {
			return ""+seconds/86400+(seconds/86400 == 1? " day, " : " days, ")+(int)((seconds/86400.0)%1*24)+((int)((seconds/86400.0)%1*24) == 1 ? " hour, " : " hours, ")+(int)(((seconds/86400.0)%1*24)%1*60)+((int)(((seconds/86400.0)%1*24)%1*60) == 1 ? " min, " : " mins, ")+String.format("%.2f", (((seconds/86400.0)%1*24)%1*60)%1*60)+"s";
		}
	}

}
