package com.parkit.parkingsystem.model;

import java.util.Calendar;
import java.util.Date;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
    	ParkingSpot result = new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
        return result;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
    	ParkingSpot set = new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
        this.parkingSpot = set;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
    	Date result = new Date(inTime.getTime());
        return result;
    }

    public void setInTime(Date inTime) {
    	Date date = new Date(inTime.getTime());
        this.inTime = date;
    }

    public Date getOutTime() {
    	if (outTime != null) {
    		Date result = new Date(outTime.getTime());
    		return result;
    	}
    	else
    		return null;
    }

    public void setOutTime(Date outTime) {
    	if (outTime != null) {
    		Date date = new Date(outTime.getTime());
        	this.outTime = date;
    	}
    	else
    		this.outTime = null;
    }
}
