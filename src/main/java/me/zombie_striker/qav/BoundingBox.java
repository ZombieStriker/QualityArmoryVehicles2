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
		// Skip if in different worlds
		if (location.getWorld() != null && !location.getWorld().equals(start.getWorld()))
			return false;

		// Get center of vehicle with offset
		Location vehicleCenter = location.clone().add(centerOffset);
		
		// Calculate direct distance to vehicle center
		double directDistance = start.distance(vehicleCenter);
		
		// Quick check - if distance is too far, skip complex calculations
		if (directDistance > reach + width)
			return false;
			
		// Approach 1: Ray casting approach (more accurate)
		// Normalize the direction vector
		Vector ray = direction.clone().normalize();
		
		// Calculate the closest point along the ray to the vehicle center
		// ray · (vehicleCenter - start) gives the projection length
		Vector startToVehicle = vehicleCenter.toVector().subtract(start.toVector());
		double projectionLength = ray.dot(startToVehicle);
		
		// If the projection is negative, the vehicle is behind the player
		if (projectionLength < 0)
			return directDistance <= width; // Only return true if player is inside vehicle
		
		// If the projection is beyond our reach, it's too far
		if (projectionLength > reach)
			return false;
		
		// Find the closest point on the ray to the vehicle center
		Vector closestPointOnRay = start.toVector().add(ray.clone().multiply(projectionLength));
		
		// Calculate distance from closest point to vehicle center
		double closestDistance = closestPointOnRay.distance(vehicleCenter.toVector());
		
		// Check if this point is within the vehicle's radius + some buffer for easier interaction
		// Adding 0.5 to width makes interaction slightly easier
		return closestDistance <= (width + 0.5);
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
