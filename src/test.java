import java.awt.*;
public class test{
    public static void main(String args[]) throws AWTException{
	Robot robot = new Robot();
	robot.setAutoDelay(1000);
	robot.mouseMove(10,19);
	// String eq = ""
	//     for(int i =0 ; i<eq.length() ; i++){
		robot.keyPress(KeyEvent.VK_H);
	    // }
    }
}