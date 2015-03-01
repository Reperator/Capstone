import java.util.Random;


public class DynamicObstacle extends Feld{
	int x, y, vectorX, vectorY;
	
	DynamicObstacle(int x, int y){
		super(4);
		this.x = x;
		this.y = y;
		Random r = new Random();
		vectorX = r.nextInt(3)-1;
		if(vectorX == 0){
			if(r.nextBoolean()){
				vectorY=1;
			}else{
				vectorY=-1;
			}
		}else{
			vectorY = 0;
		}
	}
	
	@Override
	boolean collide(){
		return false;
	}
	
	@Override
	char getCharacter() {
		return 'O';
	}

	@Override
	boolean isTransparent() {
		return true;
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
