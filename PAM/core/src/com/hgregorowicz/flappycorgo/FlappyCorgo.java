package com.hgregorowicz.flappycorgo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyCorgo extends ApplicationAdapter {
	SpriteBatch batch;
	Texture tlo;

	Texture[] corgisie;
	int stanCorgisia = 0;
	float corgiY = 0;
	float predkosc = 0;

	int stanGry = 0;
	float grawitacja = 2;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		tlo = new Texture("bg.png");
		corgisie = new Texture[2];
		corgisie[0] = new Texture("bird.png");
		corgisie[1] = new Texture("bird2.png");
		corgiY = Gdx.graphics.getHeight()/2 - corgisie[0].getHeight()/2;
	}

	@Override
	public void render () {

	    if (stanGry != 0) {

			if (Gdx.input.justTouched()) {

				predkosc = -30;
			}
			if (corgiY>0 || predkosc<0) {
				predkosc += grawitacja;
				corgiY -= predkosc;
			}

        }
	    else {
            if (Gdx.input.justTouched()) {

                stanGry = 1;
            }
        }

        if (stanCorgisia == 0)     // Zeby rysowal na przemian dwie rozne tekstury
            stanCorgisia = 1;
        else
            stanCorgisia = 0;

        batch.begin();
        batch.draw(tlo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(corgisie[stanCorgisia], Gdx.graphics.getWidth()/2 - corgisie[stanCorgisia].getWidth()/2,
                corgiY );
        batch.end();


	}
	/*
	@Override
	public void dispose () {
		batch.dispose();

	}*/
}
