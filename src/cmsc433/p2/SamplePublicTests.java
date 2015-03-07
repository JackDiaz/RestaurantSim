package cmsc433.p2;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import junit.framework.TestCase;
import cmsc433.p2.Simulation;
import cmsc433.p2.SimulationEvent;
import cmsc433.p2.Validate;

public class SamplePublicTests extends TestCase {
	//Jack Diaz 111499298

	////////
	// Tests for your Validate.validateSimulation method
	////////

	private final Food burger = new Food("burger",500);
	private final Food fries = new Food("fries",250);
	private final Food coke = new Food("coke",100);
	
	public void testValidate1Eater()
	{
		trueFalse();
	}
	public void testValidate2Eater()
	{
		trueFalse();
		validate(true,generateEvents(2));
	}
	public void testNumEaters()
	{
		//make sure total number of eaters is correct
		trueFalse();
		List<SimulationEvent> events = generateEvents(2);
		events.set(0, SimulationEvent.startSimulation(1,1,1,1));//set number of eaters to 1 (originally 2)
		validate(false,events);
	}
	public void testMaxEaters()
	{
		//make sure number of eaters in restaurant <= number of table
		trueFalse();
		List<SimulationEvent> events = generateEvents(2);
		SimulationEvent se = events.remove(23);//Eater1 entering restaurant
		events.add(6, se);//insert right after Eater0 enters
		validate(false,events);
	}
	
	//Helper methods for Validate
	
	private void validate(boolean expected, List<SimulationEvent> events)
	{
		TestCase.assertEquals(expected, Validate.validateSimulation(events));
	}
	
	private void trueFalse()
	{
		//make sure students dont just return true or false
		List<SimulationEvent> events = new ArrayList<SimulationEvent>();
		events.add(SimulationEvent.startSimulation(1,1,1,1));
		TestCase.assertFalse(Validate.validateSimulation(events));
		
		events = generateEvents(1);
		TestCase.assertTrue(Validate.validateSimulation(events));
	}
	
	private List<SimulationEvent> generateEvents(int numEaters)
	{
		List<SimulationEvent> events = new ArrayList<SimulationEvent>();
		int numCooks = 1, numTables = numEaters, capacity = 1;
		events.add(SimulationEvent.startSimulation(numEaters,numCooks,numTables,capacity));
		List<Food> order = new ArrayList<Food>(3);
		order.add(burger);
		order.add(fries);
		order.add(coke);
		HashMap<Food,Machine> machines = new HashMap<Food,Machine>();
		machines.put(order.get(0), new Machine("Grill",burger,capacity));
		machines.put(order.get(1), new Machine("Frier",fries,capacity));
		machines.put(order.get(2), new Machine("Soda Fountain",coke,capacity));
		for(Food f : machines.keySet())
			events.add(SimulationEvent.machineStarting(machines.get(f), f, capacity));
		Cook cook = new Cook("Cook 0");
		events.add(SimulationEvent.cookStarting(cook));
		for(int i=0;i<numEaters;i++)
		{
			Eater eater = new Eater("Eater "+i,order);
			events.add(SimulationEvent.eaterStarting(eater));
			events.add(SimulationEvent.eaterEnteredRestaurant(eater));
			events.add(SimulationEvent.eaterPlacedOrder(eater, order, i));
			events.add(SimulationEvent.cookReceivedOrder(cook, order, i));
			for(Food f : order)
			{
				events.add(SimulationEvent.cookStartedFood(cook, f, i));
				events.add(SimulationEvent.machineCookingFood(machines.get(f), f));
				events.add(SimulationEvent.machineDoneFood(machines.get(f), f));
				events.add(SimulationEvent.cookFinishedFood(cook, f, i));
			}
			events.add(SimulationEvent.cookCompletedOrder(cook, i));
			events.add(SimulationEvent.eaterReceivedOrder(eater, order, i));
			events.add(SimulationEvent.eaterLeavingRestaurant(eater));
		}
		events.add(SimulationEvent.cookEnding(cook));
		for(Food f : machines.keySet())
			events.add(SimulationEvent.machineEnding(machines.get(f)));
		events.add(SimulationEvent.endSimulation());
		return events;
	}
	
	private List<SimulationEvent> generateEvents2(int numEaters, int numCooks, int numTables, int capacity)
	{
		List<SimulationEvent> events = new ArrayList<SimulationEvent>();
		events.add(SimulationEvent.startSimulation(numEaters,numCooks,numTables,capacity));
		List<Food> order = new ArrayList<Food>(3);
		order.add(burger);
		order.add(fries);
		order.add(coke);
		HashMap<Food,Machine> machines = new HashMap<Food,Machine>();
		machines.put(order.get(0), new Machine("Grill",burger,capacity));
		machines.put(order.get(1), new Machine("Frier",fries,capacity));
		machines.put(order.get(2), new Machine("Soda Fountain",coke,capacity));
		for(Food f : machines.keySet())
			events.add(SimulationEvent.machineStarting(machines.get(f), f, capacity));
		List<Cook> cooks = new ArrayList<Cook>();
		for(int i=0;i<numEaters;i++){
			Cook cook = new Cook("Cook "+i);
			cooks.add(cook);
			events.add(SimulationEvent.cookStarting(cook));
		}
		for(int i=0;i<numEaters;i++)
		{
			Eater eater = new Eater("Eater "+i,order);
			events.add(SimulationEvent.eaterStarting(eater));
			events.add(SimulationEvent.eaterEnteredRestaurant(eater));
			events.add(SimulationEvent.eaterPlacedOrder(eater, order, i));
			events.add(SimulationEvent.cookReceivedOrder(cooks.get(0), order, i));
			for(Food f : order)
			{
				events.add(SimulationEvent.cookStartedFood(cooks.get(0), f, i));
				events.add(SimulationEvent.machineCookingFood(machines.get(f), f));
				events.add(SimulationEvent.machineDoneFood(machines.get(f), f));
				events.add(SimulationEvent.cookFinishedFood(cooks.get(0), f, i));
			}
			events.add(SimulationEvent.cookCompletedOrder(cooks.get(0), i));
			events.add(SimulationEvent.eaterReceivedOrder(eater, order, i));
			events.add(SimulationEvent.eaterLeavingRestaurant(eater));
		}
		events.add(SimulationEvent.cookEnding(cooks.get(0)));
		for(Food f : machines.keySet())
			events.add(SimulationEvent.machineEnding(machines.get(f)));
		events.add(SimulationEvent.endSimulation());
		return events;
	}
/*	
	public void testNumCooks()
	{
		//make sure total number of cooks is correct
		trueFalse();
		List<SimulationEvent> events = generateEvents2(4,2,2,2);
		events.set(0, SimulationEvent.startSimulation(4,1,2,2));//set number of eaters to 1 (originally 2)
		validate(false,events);
	}*/

	////////
	// Simulation tests (assumes your Validate.validateSimulation method is implemented and works)
	////////
	
	private static boolean graderVal(int eaters, int cooks, int cap, int machcap) {
        boolean ret = false;
        List<SimulationEvent> set = null;
        try{
            set = Simulation.runSimulation(eaters, cooks, cap, machcap);
        } catch(Exception exc){
            exc.printStackTrace();
            fail("Uncaught exception in student's runSimulation: ");
        }
        if (set == null)
            fail("runSimulation returned null List or no list");
        else
            ret = Validate.validateSimulation(set);
        return ret;
    }

	public static void testRun1x1x1x1(){
		assertTrue(graderVal(1,1,1,1));
	}
	
	public static void testRun2x1x1x1(){
		assertTrue(graderVal(2,1,1,1));
	}
	
	public static void testRun3x1x1x1(){
		assertTrue(graderVal(3,1,1,1));
	}
	/*
	public static void testRun1(){
		assertTrue(graderVal(300,2,40,17));
	}
	
	public static void testRun2(){
		assertTrue(graderVal(1000,1010,10,400));
	}
	
	public static void testRun3(){
		assertTrue(graderVal(500,75,5,100));
	}
	*/
	
	
    
    
}
