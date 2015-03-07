package cmsc433.p2;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	//Jack Diaz 111499298
	public final String name;
	public final Food food;
	public final Semaphore cooking;
	// use this for capacity

	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String name, Food food, int capacity) {
		this.name = name;
		this.food = food;
		this.cooking = new Semaphore(capacity);
	}
	static Machine logMachine(String name, Food food, int capacity){
		Machine ret = new Machine(name, food, capacity);
		Simulation.logEvent(SimulationEvent.machineStarting(ret, food, capacity));
		return ret;
	}
	
	void turnOff(){
		Simulation.logEvent(SimulationEvent.machineEnding(this));
	}

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than void.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public FutureTask<Food> makeFood() throws InterruptedException {
		cooking.acquire();
		Simulation.logEvent(SimulationEvent.machineCookingFood(this, food));
		Callable<Food> c = new Callable<Food> () {
			public Food call() throws InterruptedException {
				Thread.sleep(food.cookTimeMS); // this seems to miss the point...
				Machine m = Simulation.machines.get(food.name);
				Simulation.logEvent(SimulationEvent.machineDoneFood(m, food));
				return food;
			}
		};
		FutureTask<Food> future = new FutureTask<Food>(c);
		new Thread(future).start();

		return future;
	}

	public String toString() {
		return name;
	}
}