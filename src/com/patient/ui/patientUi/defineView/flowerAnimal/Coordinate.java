package com.patient.ui.patientUi.defineView.flowerAnimal;

public class Coordinate {
	public int x;
	public int y;

	public Coordinate(int newX, int newY) {
		x = newX;
		y = newY;
	}
 
	@Override
	public String toString() {
		return "Coordinate: [" + x + "," + y + "]";
	}
}
