package me.zombie_striker.qav;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BoundingBox {

	private Location location;
	private double width;
	private double height;

	public BoundingBox(Location location, double widthRadius, double height) {
	this.location = location;
	this.width = widthRadius;
	this.height = height;
	}

	public void setWidth(double widthRadius0){
		this.width = widthRadius0;
	}
	public void setHeight(double height){
		this.height = height;
	}

	public boolean intersects(Location check){

		double xdif = check.getX()-location.getX();
		double ydif = check.getY()-location.getY();
		double zdif = check.getZ()-location.getZ();


		if(xdif*xdif + zdif*zdif < width*width){
			if(ydif< height && ydif > 0){
				return true;
			}
		}
		return false;
	}

	public boolean intersects(Location start, Vector direction, int reach){
		double distance = start.distance(location);
		if(distance > reach+width)
			return false;
		Vector distanceVector = direction.clone();
		distanceVector.normalize().multiply(distance);
		Location check = start.clone().add(distanceVector);

		double xdif = check.getX()-location.getX();
		double ydif = check.getY()-location.getY();
		double zdif = check.getZ()-location.getZ();


		if(xdif*xdif + zdif*zdif < width*width){
			if(ydif< height && ydif > 0){
				return true;
			}
		}
		return false;
	}

	public Location getLocation() {
		return location;
	}

	public double getWidth() {
		return width;
	}
}
