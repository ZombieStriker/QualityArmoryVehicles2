package me.zombie_striker.qav;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BoundingBox {

	private final Location location;
	private final Vector centerOffset = new Vector(0, 1, 0);
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
		return intersects(check, centerOffset);
	}

	public boolean intersects(Location check, Vector vector) {
		Location loc = location.clone().add(vector);

		double xdif = check.getX() - loc.getX();
		double ydif = check.getY() - loc.getY();
		double zdif = check.getZ() - loc.getZ();


		if (xdif * xdif + zdif * zdif <= width * width) {
			return ydif <= height && ydif >= 0;
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
		return intersects(start,distanceVector);
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
