
public class StaticObstacle extends Feld{
	
	StaticObstacle(){
		super(3);
	}
	
	boolean collide(){
		return false;
	}

	@Override
	boolean isTransparent() {
		return true;
	}

	@Override
	char getCharacter() {
		return 'A';
	}

	@Override
	int[] getColor() {
		int[] output=new int[3];
		output[0]=255;
		output[1]=0;
		output[2]=0;
		return output;
	}
}
