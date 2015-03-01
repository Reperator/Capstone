
public class Exit extends Feld{
	
	Exit(){
		super(2);
	}
	
	@Override
	boolean isTransparent() {
		if(Capstone.player.hasKey){
			return true;
		}else{
			return false;
		}

	}

	@Override
	char getCharacter() {
		return 'E';
	}

	@Override
	boolean collide() {
		if(Capstone.player.hasKey){
			Capstone.won = true;
		}
		return true;
	}

	@Override
	int[] getColor() {
		int[] output=new int[3];
		output[0]=0;
		output[1]=255;
		output[2]=0;
		return output;
	}
}
