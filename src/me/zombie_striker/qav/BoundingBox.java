package me.zombie_striker.qav;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BoundingBox {

	private Location location;
	private Vector centerOffset = new Vector(0, 1, 0);
	private double width;
	private double height;

	public BoundingBox(Location location, double widthRadius, double height) {
		this.location = location;
		this.width = widthRadius;
		this.height = height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public boolean intersects(Location check) {


		Location loc = location.clone().add(centerOffset);

		double xdif = check.getX() - loc.getX();
		double ydif = check.getY() - loc.getY();
		double zdif = check.getZ() - loc.getZ();


		if (xdif * xdif + zdif * zdif <= width * width) {
			if (ydif <= height && ydif >= 0) {
				return true;
			}
		}
		return false;
	}

	public boolean intersects(Location start, Vector direction, int reach) {
		if (location.getWorld() != null && !location.getWorld().equals(start.getWorld()))
			return false;

		Location loc = location.clone().add(centerOffset);
		double distance = start.distance(loc);
		if (distance > reach + width)
			return false;
		Vector distanceVector = direction.clone();
		distanceVector.normalize().multiply(distance);
		Location check = start.clone().add(distanceVector);

		double xdif = check.getX() - loc.getX();
		double ydif = check.getY() - loc.getY();
		double zdif = check.getZ() - loc.getZ();


		if (xdif * xdif + zdif * zdif <= width * width) {
			if (ydif <= height && ydif >= 0) {
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

	public void setWidth(double widthRadius0) {
		this.width = widthRadius0;
	}
}
