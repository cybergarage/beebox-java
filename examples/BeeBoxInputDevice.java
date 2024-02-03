/******************************************************************
*
*	Copyright (C) Satoshi Konno 1999
*
*	File : BeeBoxInputDevice.java
*
******************************************************************/

import javax.media.j3d.*;
import javax.vecmath.*;

public class BeeBoxInputDevice implements InputDevice {
	
	private BeeBox			beeBox;
	private Sensor			beeBoxSensor;
	private SensorRead	beeBoxSensorRead	= new SensorRead();
	private Transform3D	beeBoxTransform		= new Transform3D();
	private Transform3D	rotTransform		= new Transform3D();
	private float			beeBoxPos[]			= new float[3];
	private float			beeBoxRot[]			= new float[3];
	private Vector3f		beeBoxTransVec		= new Vector3f();
	private float			sensitivity			= 1.0f;
	private float			angularRate			= 1.0f;
	private float			x, y, z;
	
	public BeeBoxInputDevice(int serialPort) {
		beeBox = new BeeBox(serialPort);
		beeBoxSensor = new Sensor(this);
		
		setSensitivity(0.1f);
		setAngularRate(0.01f);
	}

	public boolean initialize() {
		for (int i=0; i<3; i++) {
			beeBoxPos[i]	= 0.0f;
			beeBoxRot[i]	= 0.0f;
		}
		return true;
	}

	public void close() {
	}

	public int getProcessingMode() {
		return DEMAND_DRIVEN;
	}

	public int getSensorCount() {
		return 1;
	}

	public Sensor getSensor(int id)  {
		return beeBoxSensor;
	}

	public void setProcessingMode(int mode) {
	}

	public void pollAndProcessInput(BeeBox beeBox, Sensor beeBoxSensor, float beeBoxPos[], float beeBoxRot[]) {
		int switches = beeBox.getSwitches();
		if ( ((switches & BeeBox.SWITCH7) != 0) &&  ((switches & BeeBox.SWITCH8) != 0)) {
			setNominalPositionAndOrientation();
			return;
		}
		
		beeBoxSensorRead.setTime(System.currentTimeMillis());
		
		x = beeBox.getX(); 
		y = beeBox.getLever(); 
		z = beeBox.getY();
		
		if (Math.abs(x) < 0.2f)
			x = 0.0f;
		if (Math.abs(z) < 0.2f)
			z = 0.0f;
		if (Math.abs(y) < 0.2f)
			y = 0.0f;
		
 		beeBoxRot[1] -= x * angularRate;
 		beeBoxPos[2] -= z * sensitivity;
 		beeBoxPos[1] += y * sensitivity;
 		
 		beeBoxTransVec.x = beeBoxPos[0];
 		beeBoxTransVec.y = beeBoxPos[1];
 		beeBoxTransVec.z = beeBoxPos[2];

 		rotTransform.setIdentity();
 		rotTransform.rotY(beeBoxRot[1]);
 		
 		beeBoxTransform.setIdentity();
		beeBoxTransform.set(beeBoxTransVec);
		beeBoxTransform.mul(rotTransform);

		beeBoxSensorRead.set( beeBoxTransform );
		beeBoxSensor.setNextSensorRead( beeBoxSensorRead );
	}
	
	public void pollAndProcessInput() {
		pollAndProcessInput(beeBox, beeBoxSensor, beeBoxPos, beeBoxRot);
	}

	public void processStreamInput() {
	}

	public void setNominalPositionAndOrientation() {
		initialize();
		beeBoxSensorRead.setTime(System.currentTimeMillis());
		beeBoxTransform.setIdentity();
		beeBoxSensorRead.set(beeBoxTransform);
		beeBoxSensor.setNextSensorRead(beeBoxSensorRead);
	}
	
	public void setSensitivity(float value) {
		sensitivity = value;
	}

	public float getSensitivity() {
		return sensitivity;
	}
	
	public void setAngularRate(float value) {
		angularRate = value;
	}

	public float getAngularRate() {
		return angularRate;
	}
}
