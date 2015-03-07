package cmsc433.p2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	//Jack Diaz 111499298
	private final String name;

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param name - the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method defines what a Cook does, i.e., process orders
	 * submitted by Eaters. It should work as follows.  The cook
	 * tries to retrieve orders placed by Eaters.  For each order,
	 * a List<Food>, the cook submits each Food item in the List
	 * to an appropriate Machine, by calling makeFood().  Once all
	 * machines have produced the desired Food, the order is
	 * complete, and the Eater is notified.  The cook can then go
	 * to process the next order.  If during its execution the
	 * cook is interrupted (i.e., some other thread calls the
	 * interrupt() method on it, which could raise
	 * InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {
		Simulation.logEvent(SimulationEvent.cookStarting(this));
		// cook shows up for work
		boolean running = true;
		while(running){
			Map<String, Object> waiterPad = null;
			if (Thread.currentThread().isInterrupted()) {
				running = false;
			}else{
				try {
					waiterPad = Simulation.waiter.take();
					// wait for waiter to give him an order
				} catch (InterruptedException e) {
					running = false;
					Thread.currentThread().interrupt();
					// end thread, cook goes home
				}
			}

			if (Thread.currentThread().isInterrupted()) {
				running = false;
			}else{
				
				List<Food> order = (List<Food>) waiterPad.get("order");
				int orderNum = (Integer) waiterPad.get("orderNum");
				CountDownLatch latch = (CountDownLatch) waiterPad.get("latch");

				// received order from waiter
				Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, order, orderNum));

				List<FutureTask<Food>> waitingForFood = new ArrayList<FutureTask<Food>>();
				// "Boy do I love cooking!" -My chefs

				for(Food f : order){
					Machine m = Simulation.machines.get(f.name);
					try {
						// puts food in machine
						Simulation.logEvent(SimulationEvent.cookStartedFood(this, f, orderNum));
						waitingForFood.add(m.makeFood());
					} catch (InterruptedException e) {
						running = false;
						Thread.currentThread().interrupt();
						// end thread, cook goes home
						// we should never get here!!!!
					}

				}

				List<Food> orderUp = new ArrayList<Food>();
				try {
					for(FutureTask<Food> fut : waitingForFood){

						// collecting food from machines

						Food up = fut.get();

						Machine m = Simulation.machines.get(up.name);
						m.cooking.release();
						// I have to release out here because I don't 
						// want someone else sticking in another food
						// item before this cook has had a chance to 
						// remove this one.
						


						orderUp.add(up);

						// food's done!

						Simulation.logEvent(SimulationEvent.cookFinishedFood(this, up, orderNum));
					}
				}
				catch (InterruptedException e) {
					running = false;
					Thread.currentThread().interrupt();
					// end thread, cook goes home
					// we should never get here!!!!
				}
				catch (ExecutionException e) {}

				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, orderNum));

				latch.countDown();

				// tell eater that food is ready and they can leave


			}
		}
		
		Simulation.logEvent(SimulationEvent.cookEnding(this));
		


	}
}