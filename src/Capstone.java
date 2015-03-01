import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.*;
import java.util.Properties;
import java.util.LinkedList;

public class Capstone {
	static Player player; // stores the player character globally
	static boolean won, running; // stores whether or not the user has
									// finished or beaten the game
	static Terminal terminal; // stores the terminal globally
	static Feld[][] level; // stores the level in a two dimensional array
	static boolean[][] toDraw; // stores whether or not an element of the level
								// has changed and needs to be redrawn
	static String filename = "level.properties"; // stores the name of the
													// savegame
	static String defaultLevel = "level_small.properties";
	static int width, height, terminalWidth, terminalHeight, terminalOffsetX,
			terminalOffsetY = 0; // store the width and height of the level and
									// the terminal
	static LinkedList<DynamicObstacle> enemies = new LinkedList<DynamicObstacle>(); // list
																					// of
																					// all
																					// DynamicObstacles

	public static void main(String[] args) {
		int tick = 0; // stores the current tick
		Listener listener = new Listener(); // stores a ResizeListener
		boolean playerMoved = false;
		terminalWidth = 100;
		terminalHeight = 50;
		running = true;
		Properties levelProp = Import(defaultLevel); // imports
														// the
														// level
														// from
														// the
														// given
														// file
		Key k = null; // will later store the key presses made by the user

		try { // width and height of the level are read from the file
			width = Integer.parseInt(levelProp.get("Width").toString());
			height = Integer.parseInt(levelProp.get("Height").toString());
		} catch (Exception e) {
			System.out.println("Invalid file");
		}

		// Initializing the terminal
		terminal = InitializeTerminal();
		terminal.setCursorVisible(false);
		terminal.applyForegroundColor(255, 255, 255);
		terminal.addResizeListener(listener);

		level = FillLevel(levelProp, width, height); // fills the level array
														// with the level stored
														// in the file
		toDraw = new boolean[width][height]; // prepares the array

		Draw(true, true); // draws the first frame of the game
		Scroll();

		while (running) { // Actual game loop
			tick++;

			if (won) {
				DrawWonScreen();
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
				}
				Menu(true);
			}

			if (player.health <= 0) { // checks whether or not the player is
										// alive
				running = false;
			}

			if (player.invincibility > 0) {
				player.invincibility--;
			}

			k = terminal.readInput(); // reads button presses

			if (k != null) { // determines the action if a button was pressed
				try {
					switch (k.getKind()) {
					case ArrowUp: // moves the player up if he's allowed to
						if (level[player.coordinates[0]][player.coordinates[1] - 1]
								.isTransparent()) {
							toDraw[player.coordinates[0]][player.coordinates[1]] = true;
							player.coordinates[1]--;
						}
						break;
					case ArrowDown: // moves the player down if he's allowed to
						if (level[player.coordinates[0]][player.coordinates[1] + 1]
								.isTransparent()) {
							toDraw[player.coordinates[0]][player.coordinates[1]] = true;
							player.coordinates[1]++;
						}
						break;
					case ArrowRight: // moves the player right if he's allowed
										// to
						if (level[player.coordinates[0] + 1][player.coordinates[1]]
								.isTransparent()) {
							toDraw[player.coordinates[0]][player.coordinates[1]] = true;
							player.coordinates[0]++;
						}
						break;
					case ArrowLeft: // moves the player left if he's allowed to
						if (level[player.coordinates[0] - 1][player.coordinates[1]]
								.isTransparent()) {
							toDraw[player.coordinates[0]][player.coordinates[1]] = true;
							player.coordinates[0]--;
						}
						break;
					case Escape:
						Menu(false); // opens the menu
						break;
					default:
						break;
					}
					playerMoved = true;
					Scroll();
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}

			if (!level[player.coordinates[0]][player.coordinates[1]].collide()
					&& player.invincibility <= 0) { // checks whether or not the
													// player needs to take
													// damage in his current
													// position
				player.health--;
				player.invincibility = 60; // gives the player a 3 sec
											// invincibility effect to prevent
											// instant deaths
			}

			// moves all DynamicObstacle
			for (int i = 0; i < enemies.size(); i++) {
				if (tick % 4 == 0) {
					Move(enemies.get(i));
				}
			}

			Draw(false, playerMoved); // Draws the next frame

			playerMoved = false;

			k = null; // resets the button press

			try { // pauses the game logic for 50 ms to achieve an approximate
					// tickrate of 20Hz
				Thread.sleep(50); // framerate is fixed to tickrate, however
									// this affects neither the gameplay nor the
									// user experience
			} catch (InterruptedException e) {
			}
		}
		
		if(player.health<=0){
			DrawGameOverScreen();
			try{
				Thread.sleep(5000);
			}catch(Exception e){
			}
		}
		
		terminal.exitPrivateMode(); // closes the terminal
	}
	
	static void DrawGameOverScreen(){
		terminal.clearScreen();
		String gameOverString = "Game Over";
		for (int i = 0; i < gameOverString.length(); i++) {
			terminal.moveCursor(i, 0);
			printCharacter(gameOverString.charAt(i));
		}
	}

	static void printCharacter(char c) {
		try {
			terminal.putCharacter(c);
		} catch (Exception e) {
		}
	}

	static void Move(DynamicObstacle d) {
		try {
			if (level[d.x + d.vectorX][d.y + d.vectorY].type == 6) {
				// swaps a DynamicObstacle d and a neighbouring Feld in the
				// direction of the vector if possible
				Feld temp = level[d.x + d.vectorX][d.y + d.vectorY];
				level[d.x + d.vectorX][d.y + d.vectorY] = d;
				level[d.x][d.y] = temp;
				toDraw[d.x][d.y] = true;
				d.x = d.x + d.vectorX;
				d.y = d.y + d.vectorY;
				toDraw[d.x][d.y] = true;
			} else {
				// changes the movement direction if the DynamicObstacle ran
				// into an obstacle
				d.vectorX = d.vectorX * -1;
				d.vectorY = d.vectorY * -1;
				if (level[d.x + d.vectorX][d.y + d.vectorY].type == 6) {
					Feld temp = level[d.x + d.vectorX][d.y + d.vectorY];
					level[d.x + d.vectorX][d.y + d.vectorY] = d;
					level[d.x][d.y] = temp;
					toDraw[d.x][d.y] = true;
					d.x = d.x + d.vectorX;
					d.y = d.y + d.vectorY;
					toDraw[d.x][d.y] = true;
				}
			}
		} catch (Exception e) {
		}

	}

	static void DrawWonScreen() {
		terminal.clearScreen();
		String wonString = "Sie haben gewonnen!";
		for (int i = 0; i < wonString.length(); i++) {
			terminal.moveCursor(i, 0);
			printCharacter(wonString.charAt(i));
		}
	}

	static void Menu(boolean endScreen) {
		boolean menuOpen = true;
		Key k;
		int currentSelection = 0;

		// initializes the menu options
		String[] options;
		if (endScreen) {
			options = new String[3];
			options[0] = "Neu starten";
			options[1] = "Spiel laden";
			options[2] = "Beenden";
		} else {
			options = new String[6];
			options[0] = "Weiter";
			options[1] = "Spiel speichern";
			options[2] = "Spiel laden";
			options[3] = "Legende";
			options[4] = "Beenden";
			options[5] = "Speichern und beenden";
		}

		terminal.clearScreen();

		DrawMenu(options, currentSelection); // draws the first frame of the
												// menu

		while (menuOpen) { // menu loop
			k = terminal.readInput(); // reads button presses

			if (k != null) {
				switch (k.getKind()) {
				case ArrowUp: // moves the current selection up by 1
					if (currentSelection > 0) {
						currentSelection--;
					}
					break;
				case ArrowDown: // moves the current selection down by 1
					if (currentSelection < options.length) {
						currentSelection++;
					}
					break;
				case Escape: // closes the menu
					menuOpen = false;
					break;
				case Enter: // activates the current selection
					if (endScreen) {
						switch (currentSelection) {
						case 0: // restarts the game
							level = FillLevel(Import(defaultLevel), width,
									height);
							won = false;
							menuOpen = false;
							break;
						case 1: // reloads the level from the savegame file and
								// closes the menu
							level = FillLevel(Import(filename), width, height);
							won = false;
							menuOpen = false;
							break;
						case 2: // stops the game and closes the menu
							running = false;
							menuOpen = false;
							break;
						}
					} else {
						switch (currentSelection) {
						case 0: // closes the menu and continues the game
							menuOpen = false;
							break;
						case 1: // saves the game
							SaveGame();
							break;
						case 2: // reloads the level from the savegame file and
								// closes the menu
							level = FillLevel(Import(filename), width, height);
							menuOpen = false;
							break;
						case 3: // draws the key
							DrawKey();
							break;
						case 4: // stops the game and closes the menu
							running = false;
							menuOpen = false;
							break;
						case 5: // saves and stops the game, closes the menu
							SaveGame();
							running = false;
							menuOpen = false;
							break;
						}
					}
					break;
				default:
					break;
				}

				DrawMenu(options, currentSelection); // draws a new menu frame
														// if a button has been
														// pressed
			}
		}

		terminal.clearScreen(); // clears the screen

		Draw(true, true); // redraws an entire game frame
	}

	static void DrawKey() {
		Key k = null;
		terminal.clearScreen();
		String[] key = new String[8];
		Feld[] temp = new Feld[6];

		temp[0] = new Wall();
		temp[1] = new Exit();
		temp[2] = new StaticObstacle();
		temp[3] = new DynamicObstacle(0, 0);
		temp[4] = new Spawn();
		temp[5] = new Schluessel();

		key[0] = "  " + temp[0].getCharacter() + " = Wand";
		key[1] = "  " + temp[1].getCharacter() + " = Ausgang";
		key[2] = "  " + temp[2].getCharacter() + " = Statisches Hindernis";
		key[3] = "  " + temp[3].getCharacter() + " = Dynamisches Hindernis";
		key[4] = "  " + temp[4].getCharacter() + " = Eingang";
		key[5] = "  " + temp[5].getCharacter() + " = Schlüssel";
		key[6] = "  " + player.getDefaultCharacter() + " = Player";
		key[7] = "->Zurück zum Hauptmenü";

		for (int i = 0; i < temp.length; i++) {
			terminal.moveCursor(0, i);
			terminal.applyForegroundColor(temp[i].getColor()[0],
					temp[i].getColor()[1], temp[i].getColor()[2]);
			for (int l = 0; l < key[i].length(); l++) {
				printCharacter(key[i].charAt(l));
			}
		}

		for (int i = 0; i < key[6].length(); i++) {
			terminal.moveCursor(i, 6);
			terminal.applyForegroundColor(player.getColor()[0],
					player.getColor()[1], player.getColor()[2]);
			printCharacter(key[6].charAt(i));
		}
		
		terminal.applyForegroundColor(255, 255, 255);
		
		for(int i=0; i<key[7].length(); i++){
			terminal.moveCursor(i, 7);
			printCharacter(key[7].charAt(i));
		}

		while (k == null) {
			k = terminal.readInput();
		}
	}

	static void SaveGame() {
		Properties prop = new Properties(); // temporary storage of the level
		for (int i = 0; i < level.length; i++) { // adds the entire level except
													// Air to prop
			for (int k = 0; k < level.length; k++) {
				if (level[i][k].type >= 0 && level[i][k].type <= 5) {
					prop.setProperty(i + "," + k,
							Integer.toString(level[i][k].type));
				}
			}
		}
		// adds the player and whether or not he has a key and his health and
		// invincibility
		prop.setProperty("playerX", Integer.toString(player.coordinates[0]));
		prop.setProperty("playerY", Integer.toString(player.coordinates[1]));
		if (player.hasKey) {
			prop.setProperty("hasKey", "");
		}
		prop.setProperty("health", player.health + "");
		prop.setProperty("invincibility", player.invincibility + "");

		// adds the DynamicObstacles' directions
		for (int i = 0; i < enemies.size(); i++) {
			prop.setProperty("enemies" + "VectorX" + i, enemies.get(i).vectorX
					+ "");
			prop.setProperty("enemies" + "VectorY" + i, enemies.get(i).vectorY
					+ "");
		}
		// adds the level's height and width
		prop.setProperty("Height", height + "");
		prop.setProperty("Width", width + "");

		FileOutputStream output = null;

		try {
			// initializes the FileOutputStream and stores the file
			output = new FileOutputStream(filename);
			prop.store(output, "");
			output.close(); // closes the FileOutputStream
		} catch (Exception e) {
		}
	}

	static void DrawMenu(String[] options, int currentSelection) {
		terminal.clearScreen();
		// draws the options
		for (int i = 0; i < options.length; i++) {
			for (int k = 0; k < options[i].length(); k++) {
				terminal.moveCursor(k + 2, i);
				printCharacter(options[i].charAt(k));
			}
		}
		// draws the arrow
		terminal.moveCursor(0, currentSelection);
		printCharacter('-');
		terminal.moveCursor(1, currentSelection);
		printCharacter('>');
	}

	static Properties Import(String file) {
		Properties prop = new Properties(); // stores the level
		FileInputStream input = null;

		try { // reads the level from the file
			input = new FileInputStream(file);
			prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close(); // closes the FileInputStream
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	static Terminal InitializeTerminal() {
		// creates a new Terminal and sets its dimensions
		Terminal terminal = TerminalFacade.createSwingTerminal(terminalWidth,
				terminalHeight);
		terminal.enterPrivateMode();
		return terminal;
	}

	static Feld[][] FillLevel(Properties levelProp, int width, int height) {
		Feld[][] output = new Feld[width][height];
		String coordinates;
		int enemyCounter = 0;
		enemies.clear();
		for (int i = 0; i < width; i++) {
			for (int k = 0; k < height; k++) {
				coordinates = i + "," + k;
				try {
					switch (Integer.parseInt(levelProp.getProperty(coordinates)
							.toString())) {
					case 0:
						output[i][k] = new Wall();
						break;
					case 1:
						output[i][k] = new Spawn();
						if (!levelProp.containsKey("playerX")) {
							player = new Player(i, k);
						} else {
							int x = Integer.parseInt(levelProp
									.getProperty("playerX"));
							int y = Integer.parseInt(levelProp
									.getProperty("playerY"));
							player = new Player(x, y);

							if (levelProp.get("hasKey") != null) {
								player.hasKey = true;
							}
							player.health = Integer.parseInt(levelProp.get(
									"health").toString());
							player.invincibility = Integer.parseInt(levelProp
									.get("invincibility").toString());
						}
						break;
					case 2:
						output[i][k] = new Exit();
						break;
					case 3:
						output[i][k] = new StaticObstacle();
						break;
					case 4:
						output[i][k] = new DynamicObstacle(i, k);
						enemies.add((DynamicObstacle) output[i][k]);
						if (levelProp.containsKey("playerX")) {
							enemies.get(enemyCounter).vectorX = Integer
									.parseInt(levelProp
											.getProperty("enemiesVectorX"
													+ enemyCounter));
							enemies.get(enemyCounter).vectorY = Integer
									.parseInt(levelProp
											.getProperty("enemiesVectorY"
													+ enemyCounter));
							enemyCounter++;
						}
						break;
					case 5:
						output[i][k] = new Schluessel();
						break;
					default:
						output[i][k] = new Air();
						break;
					}
				} catch (NullPointerException e) {
					output[i][k] = new Air();
				}
			}
		}
		return output;
	}

	static void Draw(boolean all, boolean redrawPlayer) { // draws the game
		for (int i = terminalOffsetX; i < Math.min(width, terminalWidth
				+ terminalOffsetX); i++) { // draws the level itself
			for (int k = terminalOffsetY; k < Math.min(height, terminalHeight
					+ terminalOffsetY); k++) {
				if (toDraw[i][k] || all) {
					// checks if it needs to draw the current Feld or everything
					if (!all) {
						toDraw[i][k] = false; // resets the toDraw array
					}
					terminal.applyForegroundColor(level[i][k].getColor()[0],
							level[i][k].getColor()[1],
							level[i][k].getColor()[2]);
					terminal.moveCursor(i - terminalOffsetX, k
							- terminalOffsetY);
					printCharacter(level[i][k].getCharacter());
				}
			}
		}

		if (redrawPlayer || player.invincibility > 1) {
			// draws the player if he moved or if he's flickering
			terminal.moveCursor(player.coordinates[0] - terminalOffsetX,
					player.coordinates[1] - terminalOffsetY);
			terminal.applyForegroundColor(player.getColor()[0],
					player.getColor()[1], player.getColor()[2]);
			printCharacter(player.getCharacter());
			terminal.applyForegroundColor(255, 255, 255);
		}

		// clears the bottom line
		for (int i = 0; i < terminalWidth; i++) {
			terminal.moveCursor(i, terminalHeight);
			printCharacter(' ');
		}

		// draws the current health
		terminal.applyForegroundColor(255, 0, 0);
		for (int i = 0; i < player.health; i++) {
			terminal.moveCursor(0 + i * 2, terminalHeight);
			printCharacter('<');
			terminal.moveCursor(1 + i * 2, terminalHeight);
			printCharacter('3');
		}

		if (player.hasKey) { // draws the key symbol
			terminal.moveCursor(terminalWidth - 1, terminalHeight);
			terminal.applyForegroundColor(new Schluessel().getColor()[0],
					new Schluessel().getColor()[1],
					new Schluessel().getColor()[2]);
			printCharacter('$');
		}
		terminal.applyForegroundColor(255, 255, 255);
	}

	static void Scroll() {
		// sets the terminalOffset if the player comes within 2 spaces of the
		// terminal edge
		if (player.coordinates[0] - terminalOffsetX <= 2) {
			// sets terminalOffsetX if the player reaches the left edge
			terminalOffsetX = Math.max(player.coordinates[0]
					- (terminalWidth / 2), 0);
			Draw(true, true);
		} else if ((terminalOffsetX + terminalWidth) - player.coordinates[0] <= 2) {
			// sets terminalOffsetX if the player reaches the right edge
			terminalOffsetX = Math.max(player.coordinates[0]
					- (terminalWidth / 2), 0);
			Draw(true, true);
		} else if (player.coordinates[1] - terminalOffsetY <= 2) {
			// sets terminalOffsetX if the player reaches the top edge
			terminalOffsetY = Math.max(player.coordinates[1]
					- (terminalHeight / 2), 0);
			Draw(true, true);
		} else if ((terminalOffsetY + terminalHeight) - player.coordinates[1] <= 3) {
			// sets terminalOffsetX if the player reaches the bottom edge
			terminalOffsetY = Math.max(player.coordinates[1]
					- (terminalHeight / 2), 0);
			Draw(true, true);
		}
	}
}
