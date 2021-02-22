package me.zombie_striker.qav.config;

import me.zombie_striker.qav.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ComplexAttatchmentsYML {

	private File f;
	private FileConfiguration c;

	//private static final String LET_USERS_EDIT = "AllowUserModifications";

	private boolean needsUpdate = false;

	public static boolean exists(String name) {
		File f = new File(Main.carData, "default_" + name);
		return f.exists();
	}



	public boolean contains(String path) {
		return c.contains(path);
	}

	public Object get(String path) {
		return c.get(path);
	}

	public ComplexAttatchmentsYML set(String name, Object v) {
		return set(false, name,v);
	}
	public ComplexAttatchmentsYML set(boolean force, String name, Object v) {
		long lastmodifiedFile = f.lastModified();
		long lastmodifiedInternal = contains("lastModifiedByQA") ? (long) get("lastModifiedByQA") :  (  contains("AllowUserModifications") && c.getBoolean("AllowUserModifications") ? 0 : System.currentTimeMillis());
		if(!force && lastmodifiedFile-lastmodifiedInternal > 5000) {
			return this;
		}
		if (!contains(name) || !get(name).equals(v)) {
			c.set(name, v);
			needsUpdate = true;
		}
		return this;
	}
	public ComplexAttatchmentsYML verify(String name, Object v) {
		if (!contains(name)) {
			c.set(name, v);
			needsUpdate = true;
		}
		return this;
	}

	public ComplexAttatchmentsYML(File f) {
		this.f = f;
		this.c = YamlConfiguration.loadConfiguration(f);
	}

	public File getFile() {
		return f;
	}


	/*public static ComplexAttatchmentsYML registerComplexAttachment(String name, int id) {
		File f = new File(Main.complexData, "default_" + name + ".yml");
		ComplexAttatchmentsYML v = new ComplexAttatchmentsYML(f);
		if(v.contains("AllowUserModifications")){
			boolean allow = (boolean) v.get("AllowUserModifications");
			v.set(true,"AllowUserModifications",null);
			if(!allow)
				v.putTimeStamp();
		}
		v.setName(name);
		v.setID(id);
		v.setMaterial(Material.RABBIT_HIDE);
		return v;
	}*/



	public boolean needsUpdate() {
		return needsUpdate;
	}

	public void save() {
		verifyNeededTags();
		if (needsUpdate) {
			try {
				putTimeStamp();
				c.save(f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public ComplexAttatchmentsYML setName(String name) {
		set("name", name);
		return this;
	}

	public ComplexAttatchmentsYML setID(int id) {
		set("id", id);
		return this;
	}
	public ComplexAttatchmentsYML setMaterial(Material material) {
		set("material", material.name());
		return this;
	}
	/*public ComplexAttatchmentsYML setActionLMB(ComplexAdditionAction.Actions a) {
		set("action.lmb", a.getName());
		return this;
	}
	public ComplexAttatchmentsYML setActionLeft(ComplexAdditionAction.Actions a) {
		set("action.left", a.getName());
		return this;
	}
	public ComplexAttatchmentsYML setActionRight(ComplexAdditionAction.Actions a) {
		set("action.right", a.getName());
		return this;
	}
	public ComplexAttatchmentsYML setActionUp(ComplexAdditionAction.Actions a) {
		set("action.up", a.getName());
		return this;
	}
	public ComplexAttatchmentsYML setActionDown(ComplexAdditionAction.Actions a) {
		set("action.down", a.getName());
		return this;
	}
	public ComplexAttatchmentsYML setActionRMB(ComplexAdditionAction.Actions a) {
		set("action.rmb", a.getName());
		return this;
	}*/

	public ComplexAttatchmentsYML putTimeStamp(){
		c.set("lastModifiedByQA", System.currentTimeMillis()+5000);
		needsUpdate = true;
		return this;
	}


	public void verifyNeededTags() {
		verify("action.lmb","none");
		verify("action.rmb","none");
		verify("action.up","none");
		verify("action.down","none");
		verify("action.left","none");
		verify("action.right","none");
	}

}
