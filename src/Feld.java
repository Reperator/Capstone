public abstract class Feld {
	final int type; // stores the type of Feld for easy access

	Feld(int type) {
		this.type = type;
	}

	abstract boolean collide(); // returns false if the player needs to take
								// damage

	abstract boolean isTransparent(); // returns true if the player can move
										// onto the Feld

	abstract char getCharacter(); // returns the character that represents the
									// Feld

	abstract int[] getColor(); // returns a color value that represents the Feld
}
