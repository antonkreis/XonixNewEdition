package com.xonix_new_edition.game;

import com.badlogic.gdx.Game;

public class XonixNewEdition extends Game {

	@Override
	public void create () {
		setScreen(new MainWindow(this, "Player", "Player"));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}
}
