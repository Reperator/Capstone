
public class Spawn extends Feld{
	
	Spawn(){
		super(1);
	}
	
	@Override
	boolean collide() {
		return true;
	}

	@Override
	boolean isTransparent() {
		return true;
	}

	@Override
	char getCharacter() {
		return 'S';
	}

	@Override
	int[] getColor() {
		int[] output=new int[3];
		output[0]=255;
		output[1]=255;
		output[2]=255;
		return output;
	}

}
