package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class jogo extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;


    private ShapeRenderer shapeRenderer;
    private Circle circuloPassaro;
    private Rectangle retanguloCanoCima;
    private Rectangle retanguloCanoBaixo;

    private float variacao = 0;
    private float gravidade = 2;
    private float posicaoInicialVertical = 0;
    private float posicaoCanoHorizontal;
    private float posicaoCanoVertical;
    private float espacoEntreCanos;
    private int pontos = 0;
    private int pontuacaoMaxima = 0;
    private boolean passouCano;
    private int estadoJogo = 0;
    private float posicaoHorizontal = 0;
    private float larguraDispositivo;
    private float alturaDispositivo;

    private Random random;
    BitmapFont txtPontuacao;
    BitmapFont txtReiniciar;
    BitmapFont txtMelhorPontuacao;
    BitmapFont txtIniciar;

    Sound somVoando;
    Sound somColisao;
    Sound somPontuacao;

    Preferences preferences;



    @Override
    public void create() {
        inicializarTexturas();
        inicializarObjetos();

    }

    @Override
    public void render() {
        verificarEstadoJogo();
        validarPontos();
        desenharObjetos();
        detectarColisoes();
    }



    private void verificarEstadoJogo() {

        boolean toqueTela = Gdx.input.justTouched();
        if (estadoJogo == 0) {
            if (toqueTela) {
                gravidade = -15;
                estadoJogo = 1;
                somVoando.play();
            }
        } else if (estadoJogo == 1) {
            if (toqueTela) {
                gravidade = -15;
            }

            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
            if (posicaoCanoHorizontal < -canoTopo.getWidth()) {
                posicaoCanoHorizontal = larguraDispositivo;
                posicaoCanoVertical = random.nextInt(400) - 200;
                passouCano = false;
            }
            if (posicaoInicialVertical > 0 || toqueTela)
                posicaoInicialVertical = posicaoInicialVertical - gravidade;
            gravidade++;

        } else if (estadoJogo == 2) {

            if(pontos > pontuacaoMaxima){
                pontuacaoMaxima = pontos;
                preferences.putInteger("pontuacaoMaxima", pontuacaoMaxima);
            }
            posicaoHorizontal -= Gdx.graphics.getDeltaTime() * 500;

            if (toqueTela) {
                estadoJogo = 0;
                pontos = 0;
                gravidade = 0;
                posicaoInicialVertical = alturaDispositivo / 2;
                posicaoCanoHorizontal = larguraDispositivo;

            }
        }

    }

    private void detectarColisoes() {
        circuloPassaro.set(50 + posicaoHorizontal + passaros[0].getWidth() / 2, posicaoInicialVertical
                + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);

        retanguloCanoBaixo.set(posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight()
                - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());

        retanguloCanoCima.set(posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
                canoTopo.getWidth(), canoTopo.getHeight());

        boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
        boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
        if (colidiuCanoCima || colidiuCanoBaixo) {
            Gdx.app.log("Log", "Colidiu com o Cano cima");
            if (estadoJogo == 1) {
                somColisao.play();
                estadoJogo = 2;
            }
        }

        //Visualizar as formas que acompanham o pássaro
        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(50 + passaros[0].getWidth() / 2, posicaoInicialVertical, passaros[0].getHeight() / 2);
        shapeRenderer.setColor(Color.RED);

        shapeRenderer.rect(posicaoCanoHorizontal, alturaDispositivo / 2
                + espacoEntreCanos / 2 + posicaoCanoVertical, canoTopo.getWidth(), canoTopo.getHeight());

        shapeRenderer.rect(posicaoCanoHorizontal, alturaDispositivo / 2
                - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());

        shapeRenderer.end();*/
    }

    private void desenharObjetos() {

        batch.begin();
        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 50 + posicaoHorizontal, posicaoInicialVertical);

        batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo / 2
                - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
        batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2
                + espacoEntreCanos / 2 + posicaoCanoVertical);

        txtPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 110);


        if (estadoJogo == 2) {
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            txtReiniciar.draw(batch, "Toque para reiniciar!", larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight() / 2);
            txtMelhorPontuacao.draw(batch, "Seu record é: " + pontuacaoMaxima + "pontos", larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight());
        }

        batch.end();
    }

    private void inicializarTexturas() {

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");
        gameOver = new Texture("game_over.png");

    }

    private void inicializarObjetos() {
        batch = new SpriteBatch();
        random = new Random();


        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 350;

        txtPontuacao = new BitmapFont();
        txtPontuacao.setColor(Color.WHITE);
        txtPontuacao.getData().setScale(10);

        //txtIniciar = new BitmapFont();
        //txtIniciar.setColor(Color.GREEN);
        //txtIniciar.getData().setScale(10);

        txtReiniciar = new BitmapFont();
        txtReiniciar.setColor(Color.GREEN);
        txtReiniciar.getData().setScale(2);

        txtMelhorPontuacao = new BitmapFont();
        txtMelhorPontuacao.setColor(Color.RED);
        txtMelhorPontuacao.getData().setScale(2);

        shapeRenderer = new ShapeRenderer();
        circuloPassaro = new Circle();
        retanguloCanoBaixo = new Rectangle();
        retanguloCanoCima = new Rectangle();

        somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_colisao.wav"));
        somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

        preferences = Gdx.app.getPreferences("flappyBird");
        pontuacaoMaxima = preferences.getInteger("pontuacaoMaxima", 0);
    }

    public void validarPontos() {

        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()) {
            if (!passouCano) {
                pontos++;
                passouCano = true;
                somPontuacao.play();
            }
        }
        variacao += Gdx.graphics.getDeltaTime() * 10;
        if (variacao > 3)
            variacao = 0;
    }

    @Override
    public void dispose() {
        //Gdx.app.log("dispose", "descarte de conteudos");

    }
}
