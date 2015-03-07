package cmsc433.p2;

/**
 * Food is what is prepared by Cooks, and ordered by Eaters.  Food
 * is defined by its name, and the amount of time it takes to prepare
 * by Machine.  It is an immutable class.  DO NOT CHANGE THIS CLASS.
 */
public class Food {
	public final String name;
	public final int cookTimeMS;

	public Food(String name, int cookTimeMS) {
		this.name = name;
		this.cookTimeMS = cookTimeMS;
	}

	public String toString() {
		return name;
	}
}