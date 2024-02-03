/******************************************************************
*
*	Copyright (C) Satoshi Konno 1999
*
*	File : BeeBox.java
*
******************************************************************/

public class BeeBox extends SerialPort {

	private static final int MIN_VALUE	= 8400;
	private static final int MAX_VALUE	= 25000;
	private static final int	MID_POINT	= MIN_VALUE + (MAX_VALUE - MIN_VALUE) / 2;
	private static final int	RANGE			= (MAX_VALUE - MIN_VALUE) / 2;

	public static final int SWITCH1 = 0x0001;
	public static final int SWITCH2 = 0x0002;
	public static final int SWITCH7 = 0x0004;
	public static final int SWITCH8 = 0x0008;

	private float		x;
	private float		y;
	private float		lever;
	private int			switches;
	
	private static final int X_UPDATE_FLAG				= 0x01;
	private static final int Y_UPDATE_FLAG				= 0x02;
	private static final int LEVER_UPDATE_FLAG		= 0x04;
	private static final int SWITCHES_UPDATE_FLAG	= 0x08;
	private static final int ALL_UPDATE_FLAG			= 0x0F;

	private int			updateFlag;

	/**
	 *	@param deviceName	name of the port to open.  ex. "COM1", "/dev/ttyd1"
	 *	@see vrml.sensor.SerialPort#SerialPort(String deviceName, int baudrate, int dataBits, int stopBits, int parity)
	 */
	public BeeBox(String deviceName) {
		super(deviceName, 19200, DATABITS_8, STOPBITS_1, PARITY_NONE);
		initialize();
	}

	/**
	 *	@param device		number of the port to open.  ex. "COM1", "/dev/ttyd1"
	 *	<UL>
	 *	SERIAL1<BR>
	 *	SERIAL2<BR>
	 *	SERIAL3<BR>
	 *	SERIAL4<BR> 
	 *	</UL>
	 *	@see vrml.sensor.SerialPort#SerialPort(int serialport, int baudrate, int dataBits, int stopBits, int parity)
	 */
	public BeeBox(int device) {
		super(device, 19200, DATABITS_8, STOPBITS_1, PARITY_NONE);
		initialize();
	}

	private void initialize() {
		x = 0;
		y = 0;
		lever = 0;
		switches = 0;
	}

	private void updateData() {
		write("o");
		waitData(22);
		
		byte data[] = read(22).getBytes();
		
		x = (float)((data[5]*256 + data[6])-MID_POINT) / (float)RANGE;
		y = (float)((data[7]*256 + data[8])-MID_POINT) / (float)RANGE;
		lever = (float)((data[11]*256 + data[12])-MID_POINT) / (float)RANGE;
		
		switches = 0;
		if (((data[4]-0x21) & 0x01) != 0)
			switches |= SWITCH1;
		if (((data[4]-0x21) & 0x02) != 0)
			switches |= SWITCH2;
		if (((data[3]-0x21) & 0x04) != 0)
			switches |= SWITCH7;
		if (((data[3]-0x21) & 0x08) != 0)
			switches |= SWITCH8;

		updateFlag = ALL_UPDATE_FLAG;
	}

	public float getX() {
		if ((updateFlag & X_UPDATE_FLAG) == 0)
			updateData();
		updateFlag &= ~X_UPDATE_FLAG;
		return x;
	}

	public float getY() {
		if ((updateFlag & Y_UPDATE_FLAG) == 0)
			updateData();
		updateFlag &= ~Y_UPDATE_FLAG;
		return y;
	}

	public float getLever() {
		if ((updateFlag & LEVER_UPDATE_FLAG) == 0)
			updateData();
		updateFlag &= ~LEVER_UPDATE_FLAG;
		return lever;
	}

	public int getSwitches() {
		if ((updateFlag & SWITCHES_UPDATE_FLAG) == 0)
			updateData();
		updateFlag &= ~SWITCHES_UPDATE_FLAG;
		return switches;
	}

/*
	public void write(String data) {
		System.out.println("write = \"" + data + "\"");
		super.write(data);
	}

	public String read(int ndata) {
		String data = super.read(ndata);
		System.out.println("read  = \"" + data + "\"");
		return data; 
	}

	public String read(int ndata, long timeOut) {
		String data = super.read(ndata, timeOut);
		System.out.println("read  = \"" + data + "\"");
		return data; 
	}
*/

	public String toString() {
		String msg = x + " " + y + " " + lever + " "  + switches;
		return msg;
	}
}	

