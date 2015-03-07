package cmsc433.p2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Eaters are simulation actors that have two fields: a name, and a list
 * of Food items that constitue the Eater's order.  When running, an
 * eater attempts to enter the restaurant (only successful if the
 * restaurant has a free table), place its order, and then leave the 
 * restaurant when the order is complete.
 */
public class Eater implements Runnable {
	//Jack Diaz 111499298
	private final String name;
	private final List<Food> order;
	private final int orderNum;    
	private static int cnt = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Eater(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++cnt;
	}

	public String toString() {
		return name;
	}

	/** 
	 * This method defines what an Eater does: The eater attempts to
	 * enter the restaurant (only successful if the restaurant has a
	 * free table), place its order, and then leave the restaurant
	 * when the order is complete.
	 */
	public void run() {
		Simulation.logEvent(SimulationEvent.eaterStarting(this));
		// eater now exists
		try {
			Simulation.host.put(this);
			// waiting on line outside my very popular restaurant
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// gets here once a table opens up
		
		Simulation.logEvent(SimulationEvent.eaterEnteredRestaurant(this));
		
		// eater checks in on FourSquare "I'm in the best restaurant ever!"
		
		CountDownLatch waitForFood = new CountDownLatch(1);
		
		Map<String, Object> waiterPad = new ConcurrentHashMap<String, Object>();
		
		waiterPad.put("order", order);
		waiterPad.put("orderNum", orderNum);
		waiterPad.put("latch", waitForFood);
		
		// placing order with waiter
		Simulation.logEvent(SimulationEvent.eaterPlacedOrder(this, order, orderNum));
		try {
			
			Simulation.waiter.put(waiterPad);
			// waiting for the waiter to come over
			// shouldn't be long because 
			// as of right now we have infinite waiters
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			waitForFood.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// get confirmation from cook that order is ready
		
		Simulation.logEvent(SimulationEvent.eaterReceivedOrder(this, order, orderNum));
		
		// eater posts a picture of delicious food items on instagram
		
		Simulation.logEvent(SimulationEvent.eaterLeavingRestaurant(this));
		
		// eater updates facebook status "I just had a wonderful night out!"
		Simulation.closingTime.countDown();
		Simulation.host.remove(this);
	}
}