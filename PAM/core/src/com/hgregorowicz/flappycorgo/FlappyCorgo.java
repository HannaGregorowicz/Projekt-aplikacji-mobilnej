package com.hgregorowicz.flappycorgo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Random;

public class FlappyCorgo extends ApplicationAdapter {

    Interfejs in;

    public FlappyCorgo(Interfejs interfejs) {
        this.in = interfejs;
    }

	SpriteBatch batch;
	Texture tlo;
	//ShapeRenderer shapeRenderer;
    int punkty = 0;
    int tubaPunktujaca = 0;     // Tuba, ktorej przekroczenie skutkuje dodaniem punktu
	BitmapFont font;
	BitmapFont fontBlack;
	BitmapFont fontPurple;

	Texture[] corgisie;
	Texture tubaGora;
	Texture tubaDol;
	Texture koniecGry;
	Texture pomoc;
	Texture przyciskPomoc;
	Texture przyciskZmiany1;
	Texture przyciskZmiany2;

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

    int highscore = 0;
	Preferences pref;

	Stage stage;
    TextButton button;
    TextButton.TextButtonStyle textButtonStyle;
    boolean czyHelpKlikniety = false;

    TextButton buttonZmiany;
    boolean zmianaTla = false;
    boolean obecnetlo = false;
	
	@Override
	public void create () {
	    stage = new Stage();
        Gdx.input.setInputProcessor(stage);
		batch = new SpriteBatch();
		tlo = new Texture("tlo.png");
		//shapeRenderer = new	ShapeRenderer();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		fontBlack = new BitmapFont();
		fontBlack.setColor(Color.BLACK);
		fontBlack.getData().setScale(10);
		fontPurple = new BitmapFont();
		fontPurple.setColor(Color.PURPLE);
		fontPurple.getData().setScale(10);

		corgiKolko = new Circle();
		dolneTubyRect = new Rectangle[liczbaTub];
		gorneTubyRect = new Rectangle[liczbaTub];

		tubaGora = new Texture("toptube.png");
		tubaDol = new Texture("bottomtube.png");
		koniecGry = new Texture("over.png");
		pomoc = new Texture("howtoplay.png");
		przyciskPomoc = new Texture("help_btn.png");
		przyciskZmiany1 = new Texture("change_btn1.png");
		przyciskZmiany2 = new Texture("change_btn2.png");

		corgisie = new Texture[2];
		corgisie[0] = new Texture("corgi.png");
		corgisie[1] = new Texture("corgi1.png");

		corgiY = Gdx.graphics.getHeight()/2 - corgisie[0].getHeight()/2;
		maksPrzesuniecie = Gdx.graphics.getHeight()/2 - szczelina/2 - 100;
		losujemy = new Random();
		odlegloscMiedzyTubami = Gdx.graphics.getWidth() * 0.75f;

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        button = new TextButton("?", textButtonStyle);
        button.setX(Gdx.graphics.getWidth()-150);
        button.setY(Gdx.graphics.getHeight()-150);
        stage.addActor(button);

        buttonZmiany = new TextButton("ci", textButtonStyle);
        buttonZmiany.setX(0);
        buttonZmiany.setY(Gdx.graphics.getHeight()-150);
        stage.addActor(buttonZmiany);

		startGry();
		pref = Gdx.app.getPreferences("my-preferences");

		if (!pref.contains("Wynik")){
			pref.putInteger("Wynik", 0);
		}
		highscore = pref.getInteger("Wynik", 0);
	}

	public void startGry () {
        corgiY = Gdx.graphics.getHeight()/2 - corgisie[0].getHeight()/2;

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


		button.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				czyHelpKlikniety = true;
			}
		});
        buttonZmiany.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                zmianaTla = true;
            }
        });

	    if (stanGry == 1) {		// Stan 1 to poczatek gry

            if (tubaX[tubaPunktujaca] < Gdx.graphics.getWidth()/2) {      // Sprawdzenie czy nalezy dodać punkt
                punkty++;
                Gdx.app.log("Punkty: ", String.valueOf(punkty));
                if (tubaPunktujaca < liczbaTub-1)
                    tubaPunktujaca++;
                else
                    tubaPunktujaca = 0;
            }

			if (Gdx.input.justTouched() && !czyHelpKlikniety) {
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

			if (corgiY > 0) {
				predkosc += grawitacja;
				corgiY -= predkosc;
			}
			else
				stanGry = 2;

        }
	    else if (stanGry == 0) {
            if (Gdx.input.justTouched()  && !czyHelpKlikniety)
                stanGry = 1;
        }
	    else if (stanGry == 2) {
	    	batch.draw(koniecGry, Gdx.graphics.getWidth()/2 - koniecGry.getWidth()/2, Gdx.graphics.getHeight()/2 - koniecGry.getHeight()/2+250);

	    	if (punkty>highscore) {
	    		highscore = punkty;
				pref.putInteger("Wynik", highscore);
				pref.flush();
				in.toastHighscore();
			}
			String txt = "Highscore: " + String.valueOf(highscore);
			fontPurple.draw(batch, txt, 150, Gdx.graphics.getHeight()/2+600);

            if (Gdx.input.justTouched()  && !czyHelpKlikniety) {
                stanGry = 1;
                startGry();
                punkty = 0;
                tubaPunktujaca = 0;
                predkosc = 0;
            }

		}

        if (stanCorgisia == 0)     // Zeby rysowal na przemian dwie rozne tekstury
            stanCorgisia = 1;
        else
            stanCorgisia = 0;


        batch.draw(corgisie[stanCorgisia], Gdx.graphics.getWidth()/2 - corgisie[stanCorgisia].getWidth()/2, corgiY );

        fontBlack.draw(batch, String.valueOf(punkty), 20, 130);


		if (czyHelpKlikniety) {
			batch.draw(pomoc, Gdx.graphics.getWidth()/2 - pomoc.getWidth()/2, Gdx.graphics.getHeight()/2 - pomoc.getHeight()/2, pomoc.getWidth(), pomoc.getHeight());

			if (Gdx.input.justTouched()) {
				czyHelpKlikniety = false;
			}
		}

		if (zmianaTla) {
		    if (!obecnetlo) {
		        tlo = new Texture("tlo2.png");
		        obecnetlo = true;
            }
		    else {
		        tlo = new Texture("tlo.png");
		        obecnetlo = false;
            }
		    zmianaTla = false;
        }

        batch.end();

		stage.draw();
		batch.begin();
		batch.draw(przyciskPomoc, Gdx.graphics.getWidth()-150, Gdx.graphics.getHeight()-150);
		if (!obecnetlo)
			batch.draw(przyciskZmiany1, 0, Gdx.graphics.getHeight()-150);
		else
			batch.draw(przyciskZmiany2, 0, Gdx.graphics.getHeight()-150);

		batch.end();


		corgiKolko.set(Gdx.graphics.getWidth()/2, corgiY + corgisie[stanCorgisia].getHeight()/2, corgisie[stanCorgisia].getWidth()/2);

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);      // Tu renderuje ksztalty zeby zobaczyc czy sie zgadzaja
        //shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(corgiKolko.x, corgiKolko.y, corgiKolko.radius);

        for (int i=0; i<liczbaTub; i++) {
            //shapeRenderer.rect(dolneTubyRect[i].x, dolneTubyRect[i].y, dolneTubyRect[i].width, dolneTubyRect[i].height);
			//shapeRenderer.rect(gorneTubyRect[i].x, gorneTubyRect[i].y, gorneTubyRect[i].width, gorneTubyRect[i].height);

			if (Intersector.overlaps(corgiKolko, dolneTubyRect[i]) || Intersector.overlaps(corgiKolko, gorneTubyRect[i])) {		//Kolizje
				//if (punkty>5)
				    stanGry = 2;	// Koniec gry po kolizji
			}
        }
		//shapeRenderer.end();


	}


}
