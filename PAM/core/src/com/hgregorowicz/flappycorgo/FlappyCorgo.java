package com.hgregorowicz.flappycorgo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyCorgo extends ApplicationAdapter {
	SpriteBatch batch;
	Texture tlo;
	//ShapeRenderer shapeRenderer;
    int punkty = 0;
    int tubaPunktujaca = 0;     // Tuba, ktorej przekroczenie skutkuje dodaniem punktu
	BitmapFont font;

	Texture[] corgisie;
	Texture tubaGora;
	Texture tubaDol;
	int stanCorgisia = 0;
	float corgiY = 0;
	float predkosc = 0;
	Circle corgiKolko;		// Przyblizenie corgi do kolizji
    Rectangle[] gorneTubyRect;
    Rectangle[] dolneTubyRect;

	int stanGry = 0;
	float grawitacja = 2;

	float szczelina = 400;		// Miedzy rura gorna a dolna
	float maksPrzesuniecie;		// O ile tuby moga byc przesuwane
	Random losujemy;

    float predkoscTub = 4.2f;
    int liczbaTub = 4;		// 4 "komplety" tub zeby bylo wrazenie ze sa nieskonczone
	float tubaX[] = new float[liczbaTub];
	float przesuniecie[] = new float[liczbaTub];
    float odlegloscMiedzyTubami;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		tlo = new Texture("bg.png");
		//shapeRenderer = new	ShapeRenderer();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		corgiKolko = new Circle();
		dolneTubyRect = new Rectangle[liczbaTub];
		gorneTubyRect = new Rectangle[liczbaTub];

		tubaGora = new Texture("toptube.png");
		tubaDol = new Texture("bottomtube.png");

		corgisie = new Texture[2];
		corgisie[0] = new Texture("bird.png");
		corgisie[1] = new Texture("bird2.png");

		corgiY = Gdx.graphics.getHeight()/2 - corgisie[0].getHeight()/2;
		maksPrzesuniecie = Gdx.graphics.getHeight()/2 - szczelina/2 - 100;
		losujemy = new Random();
		odlegloscMiedzyTubami = Gdx.graphics.getWidth() * 0.75f;

		for (int i=0; i<liczbaTub; i++) {
			przesuniecie[i] = (losujemy.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - szczelina - 200);
			tubaX[i] = Gdx.graphics.getWidth()/2 - tubaGora.getWidth()/2 + Gdx.graphics.getWidth() + i*odlegloscMiedzyTubami;       // Startowe pozycje tub
			dolneTubyRect[i] = new Rectangle();
			gorneTubyRect[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(tlo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	    if (stanGry != 0) {

            if (tubaX[tubaPunktujaca] < Gdx.graphics.getWidth()/2) {      // Sprawdzenie czy nalezy dodać punkt
                punkty++;
                Gdx.app.log("Punkty: ", String.valueOf(punkty));
                if (tubaPunktujaca < liczbaTub-1)
                    tubaPunktujaca++;
                else
                    tubaPunktujaca = 0;
            }

			if (Gdx.input.justTouched()) {
				predkosc = -30;
			}

			for (int i=0; i < liczbaTub; i++) {

				if (tubaX[i] < -tubaDol.getWidth()) {		// Czy tuba wychodzi poza ekran
					tubaX[i] += liczbaTub * odlegloscMiedzyTubami;		// Przesuwam w prawo
					przesuniecie[i] = (losujemy.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - szczelina - 200);
				}
				else
                    tubaX[i] -= predkoscTub;


				batch.draw(tubaGora, tubaX[i], Gdx.graphics.getHeight()/2+szczelina/2 + przesuniecie[i]);
				batch.draw(tubaDol, tubaX[i], Gdx.graphics.getHeight()/2-szczelina/2-tubaDol.getHeight() + przesuniecie[i]);

				gorneTubyRect[i] = new Rectangle(tubaX[i], Gdx.graphics.getHeight()/2+szczelina/2 + przesuniecie[i], tubaGora.getWidth(), tubaGora.getHeight());
                dolneTubyRect[i] = new Rectangle(tubaX[i], Gdx.graphics.getHeight()/2-szczelina/2-tubaDol.getHeight() + przesuniecie[i], tubaDol.getWidth(), tubaDol.getHeight());
			}

			if (corgiY > 0 || predkosc < 0) {
				predkosc += grawitacja;
				corgiY -= predkosc;
			}

        }
	    else {
            if (Gdx.input.justTouched())
                stanGry = 1;
        }

        if (stanCorgisia == 0)     // Zeby rysowal na przemian dwie rozne tekstury
            stanCorgisia = 1;
        else
            stanCorgisia = 0;


        batch.draw(corgisie[stanCorgisia], Gdx.graphics.getWidth()/2 - corgisie[stanCorgisia].getWidth()/2, corgiY );

        font.draw(batch, String.valueOf(punkty), 100, 200);
        batch.end();

		corgiKolko.set(Gdx.graphics.getWidth()/2, corgiY + corgisie[stanCorgisia].getHeight()/2, corgisie[stanCorgisia].getWidth()/2);

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);      // Tu renderuje ksztalty zeby zobaczyc czy sie zgadzaja
        //shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(corgiKolko.x, corgiKolko.y, corgiKolko.radius);

        for (int i=0; i<liczbaTub; i++) {
            //shapeRenderer.rect(dolneTubyRect[i].x, dolneTubyRect[i].y, dolneTubyRect[i].width, dolneTubyRect[i].height);
			//shapeRenderer.rect(gorneTubyRect[i].x, gorneTubyRect[i].y, gorneTubyRect[i].width, gorneTubyRect[i].height);

			if (Intersector.overlaps(corgiKolko, dolneTubyRect[i]) || Intersector.overlaps(corgiKolko, gorneTubyRect[i])) {		//Kolizje
				Gdx.app.log("Kolizja", "Tak");
			}
        }
		//shapeRenderer.end();

	}

}
