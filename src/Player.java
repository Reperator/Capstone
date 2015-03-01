public class Player {
	int health, invincibility;
	int maxhealth = 3;
	int[] coordinates;
	boolean hasKey;

	Player(int x, int y) {
		health = maxhealth;
		coordinates = new int[2];
		coordinates[0] = x;
		coordinates[1] = y;
		invincibility = 0;
	}

	public void grabKey() { // gives the player a key and removes the key
		hasKey = true;
		Capstone.level[coordinates[0]][coordinates[1]] = new Air();
	}

	public char getCharacter() {
		// creates a flickering effect
		if (invincibility % 2 == 0) {
			return getDefaultCharacter();
		} else {
			return ' ';
		}
	}

	public char getDefaultCharacter() {
		return 'P';
	}

	public int[] getColor() {
		int[] output = new int[3];
		output[0] = 0;
		output[1] = 255;
		output[2] = 0;
		return output;
	}
}
