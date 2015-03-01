
public class Air extends Feld{
	Air(){
		super(6);
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
		return ' ';
	}

	@Override
	int[] getColor() {
		int[] output=new int[3];
		output[0]=0;
		output[1]=0;
		output[2]=0;
		return output;
	}

}
