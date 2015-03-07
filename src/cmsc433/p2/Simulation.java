package cmsc433.p2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Simulation is the main class used to run the simulation.  You may
 * add any fields (static or instance) or any methods you wish.
 */
public class Simulation {
	//Jack Diaz 111499298

	static List<SimulationEvent> events;
	static BlockingQueue<Eater> host;
	static BlockingQueue<Map<String, Object>> waiter;
	static Map<String, Machine> machines;
	static CountDownLatch closingTime;

	public static void logEvent(SimulationEvent event){
		System.out.println(event.toString());
		events.add(event);
	}

	/**
	 * This function performs the simulation. Returns a List of
	 * SimulationEvent objects, constructed any way you see
	 * fit. This List will be validated by a call to
	 * Validate.validateSimulation. This method is called from
	 * Simulation.main(). We should be able to test your code by
	 * only calling runSimulation.
	 *
	 * @param numEaters the number of eaters wanting to enter the restaurant
	 * @param numCooks the number of cooks in the simulation
	 * @param numTables the number of tables in the restaurant (i.e. restaurant capacity)
	 * @param machineCapacity the capicity of all machines in the restaurant 
	 * @throws Exception 
	 */
	public static List<SimulationEvent> runSimulation(int numEaters, int numCooks, int numTables, int machineCapacity) throws Exception{
		if(numEaters <= 0){
			throw new Exception("numEaters <= 0");
		}
		if(numCooks <= 0){
			throw new Exception("numCooks <= 0");
		}
		if(numTables <= 0){
			throw new Exception("numTables <= 0");
		}
		if(machineCapacity <= 0){
			throw new Exception("machineCapacity <= 0");
		}

		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());

		host = new LinkedBlockingQueue<Eater>(numTables);

		waiter = new LinkedBlockingQueue<Map<String, Object>>(); 
		//does it need a limit?
		//probably limited by numTables~~~

		List<Food> order = new ArrayList<Food>();


		order.add(new Food("burger", 500));
		order.add(new Food("fries", 250));
		order.add(new Food("coke", 100));

		Eater[] eaters = new Eater[numEaters];
		Cook[] cooks = new Cook[numCooks];
		machines = new HashMap<String, Machine>();

		List<Thread> cookThreads = new ArrayList<Thread>();
		List<Thread> eaterThreads = new ArrayList<Thread>();


		for(int i = 0; i < eaters.length; i++){
			eaters[i] = new Eater("Eater "+i, order);
		}

		for(int i = 0; i < cooks.length; i++){
			cooks[i] = new Cook("Cook "+i);
		}

		closingTime = new CountDownLatch(numEaters);

		logEvent(SimulationEvent.startSimulation(numEaters, numCooks, numTables, machineCapacity));

		machines.put("burger", Machine.logMachine("Grill", order.get(0), machineCapacity));
		machines.put("fries", Machine.logMachine("Frier", order.get(1), machineCapacity));
		machines.put("coke", Machine.logMachine("Soda Fountain", order.get(2), machineCapacity));


		for(Cook c : cooks){
			Thread toAdd = new Thread(c);
			cookThreads.add(toAdd);
			toAdd.start();
		}
		for(Eater e : eaters){
			Thread toAdd = new Thread(e);
			eaterThreads.add(toAdd);
			toAdd.start();		
		}

		closingTime.await();
		// when eaters have all left
		// interrupt cooks
		for(Thread t : cookThreads){
			t.interrupt();
		}
		for(Thread t : eaterThreads){
			t.join();
		}
		for(Machine m : machines.values()){
			m.turnOff();
		}
		for(Thread t : cookThreads){
			t.join();
		}
		logEvent(SimulationEvent.endSimulation());
		return events;
	}

	/**
	 * Entry point for the simulation program. All simulation
	 * code, however, should be in runSimulation, so that we can
	 * test your simulation by only calling runSimulation() then
	 * Validate.validateSimulation. This means that most code from
	 * your original Simulation.main should probably now be in
	 * Simulation.runSimulation.
	 *
	 * @param args the command-line arguments for the simulation.  There
	 * should be exactly four arguments: the first is the number of eaters,
	 * the second is the number of cooks, the third is the number of tables
	 * in the restaurant, and the fourth is the number of items each cooking
	 * machine can make at the same time.  
	 * @throws Exception 
	 */
	public static void main(String args[]) throws Exception {
		// Parameters to the simulation
		if (args.length != 4) {
			System.err.println("usage: java Simulation <#eaters> <#cooks> <#tables> <capacity>");
			System.exit(1);
		}
		int numEaters = new Integer(args[0]).intValue();
		int numCooks = new Integer(args[1]).intValue();
		int numTables = new Integer(args[2]).intValue();
		int machineCapacity = new Integer(args[3]).intValue();

		List<SimulationEvent> simEvents;

		// Run the simulation
		simEvents = runSimulation(numEaters, numCooks, numTables, machineCapacity);

		// Validate the simulation
		Validate.validateSimulation(simEvents);
	}
}
