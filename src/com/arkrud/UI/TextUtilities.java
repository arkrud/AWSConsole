package com.arkrud.UI;

import java.util.Hashtable;

import javax.swing.Action;

public class TextUtilities {
	private TextUtilities() {
	}

	public static Action findAction(Action actions[], String key) {
		Hashtable<Object, Action> commands = new Hashtable<Object, Action>();
		for (int i = 0; i < actions.length; i++) {
			Action action = actions[i];
			commands.put(action.getValue(Action.NAME), action);
		}
		return commands.get(key);
	}
}
