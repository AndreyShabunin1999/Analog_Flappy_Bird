package com.zain.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture gameover;
    Texture play;

    Texture[] birds;
    int flapState = 0;
    float birdY = 0;
    float velocity = 0;
    Circle birdCircle;
    int score = 0;
    int scoringTube = 0;
    BitmapFont font;


    int gameState = 0;
    float gravity = 2;
    Texture topTube;
    Texture bottomTube;
    float gap = 500;
    float maxTubeOffset;
    Random randomGenerator;

    float tubeVelocity = 4;

    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTheTube;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    Music music;
    Music music_die;
    Music music_wing;
    Music music_point;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        play = new Texture("play.png");
        //shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();

        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTheTube = Gdx.graphics.getWidth() * 3 / 4;
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        music = Gdx.audio.newMusic(Gdx.files.internal("musicPhon.mp3"));
        music.setVolume(0.5f);// устанавливает громкость на половину максимального объема
        //music.setLooping(true);// повторное воспроизведение, пока не будет вызван music.stop()

        music_die = Gdx.audio.newMusic(Gdx.files.internal("die.mp3"));
        music_wing = Gdx.audio.newMusic(Gdx.files.internal("wing.mp3"));
        music_point = Gdx.audio.newMusic(Gdx.files.internal("point.mp3"));

        startGame();
    }

    public void startGame()
    {
        birdY = Gdx.graphics.getHeight() / 2 - birds[flapState].getHeight() / 2;
        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTheTube;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();

        }
    }


    @Override
    public void render() {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Птица жива
        if (gameState == 1) {

            //Прошла ли птица трубу (Для начисления очков)
            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
                Gdx.app.log("Score", String.valueOf(score));
                score++;
                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                    music_point.play();
                } else {
                    scoringTube = 0;
                }
            }

            //Когда игрок тыкает пальцем
            if (Gdx.input.justTouched()) {
                velocity = -30;
                music_wing.play();
            }
            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] < -topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTheTube;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                } else {

                    tubeX[i] = tubeX[i] - tubeVelocity;


                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }


            if (birdY > 0) {
                velocity = velocity + gravity;
                birdY -= velocity;
            }else{
                gameState = 2; //2 - конец игры
                music_die.play();
            }
        } else if(gameState==0){

            batch.draw(play ,Gdx.graphics.getWidth()/2 - play.getWidth()/2,Gdx.graphics.getHeight()/2 -play.getHeight());


            if (Gdx.input.justTouched())
            {
                gameState = 1;
                music.play();
            }

        }else if(gameState==2)
        {
            music.stop();

            //Вывод надписи "Game Over"
            batch.draw(gameover ,Gdx.graphics.getWidth()/2 - gameover.getWidth()/2,Gdx.graphics.getHeight()/2 -gameover.getHeight());

            if (Gdx.input.justTouched())
            {
                gameState = 1;
                startGame();
                score =0;
                scoringTube=0;
                velocity = 0;
                music.play();
            }

        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }


        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
        font.draw(batch , String.valueOf(score) , 100 , 200);


        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

        for (int i = 0; i < numberOfTubes; i++) {

            if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                gameState = 2;
            }
        }
    }

    //Очистка памяти
    @Override
    public void dispose() {
        super.dispose();
        music.dispose();
        music_die.dispose();
        music_wing.dispose();
    }
}
