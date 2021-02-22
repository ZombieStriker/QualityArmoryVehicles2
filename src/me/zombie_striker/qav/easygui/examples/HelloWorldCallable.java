package me.zombie_striker.qav.easygui.examples;

import me.zombie_striker.qav.easygui.ClickData;
import me.zombie_striker.qav.easygui.EasyGUICallable;

public class HelloWorldCallable extends EasyGUICallable {

	@Override
	public void call(ClickData data) {
		data.getClicker().sendMessage("Hello World!");
	}
}
