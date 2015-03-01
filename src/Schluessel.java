
public class Schluessel extends Feld{
	
	Schluessel(){
		super(5);
	}
	
	@Override
	boolean collide() {
		Capstone.player.grabKey();
		return true;
	}

	@Override
	boolean isTransparent() {
		return true;
	}

	@Override
	char getCharacter() {
		return '$';
	}

	@Override
	int[] getColor() {
		int[] output=new int[3];
		output[0]=255;
		output[1]=255;
		output[2]=0;
		return output;
	}

}
