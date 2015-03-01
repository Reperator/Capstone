
public class Wall extends Feld{
	Wall(){
		super(0);
	}
	
	boolean collide(){
		return true;
	}

	@Override
	boolean isTransparent() {
		return false;
	}

	@Override
	char getCharacter() {
		return '\u2588';
	}

	@Override
	int[] getColor() {
		int[] output=new int[3];
		output[0]=200;
		output[1]=200;
		output[2]=200;
		return output;
	}
}
