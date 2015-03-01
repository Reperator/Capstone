import com.googlecode.lanterna.terminal.Terminal.ResizeListener;
import com.googlecode.lanterna.terminal.TerminalSize;


public class Listener implements ResizeListener{

	@Override
	public void onResized(TerminalSize newSize) {
		//Saves the changed terminal size and redraws the current frame
		Capstone.terminalWidth = newSize.getColumns();
		Capstone.terminalHeight = newSize.getRows();
		Capstone.Draw(true, true);
		Capstone.Scroll();
	}

}
