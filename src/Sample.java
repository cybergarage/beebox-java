/******************************************************************
*
*	Copyright (C) Satoshi Konno 1999
*
*	File : Sample.java
*
******************************************************************/

public class Sample {

	public static void main(String args[]) {

		BeeBox beeBox = new BeeBox(BeeBox.SERIALPORT1);

		float x, y, lever;
		int	switches;
		
		for (int n=0; n<10000; n++) {
			x = beeBox.getX();
			y = beeBox.getY();
			lever = beeBox.getLever();
			switches = beeBox.getSwitches();

			StringBuffer switchesString = new StringBuffer();
			if ((switches & BeeBox.SWITCH1) != 0)
				switchesString.append("SWITCH1 ");
			if ((switches & BeeBox.SWITCH2) != 0)
				switchesString.append("SWITCH2 ");
			if ((switches & BeeBox.SWITCH7) != 0)
				switchesString.append("SWITCH7");
			if ((switches & BeeBox.SWITCH8) != 0)
				switchesString.append("SWITCH8 ");
			
			if (switchesString.length() == 0)
				switchesString.append("NONE");
			
			System.out.println("Data : " + x + ", " + y + ", " + lever + ", "+ switchesString);
		}
	}

}	

