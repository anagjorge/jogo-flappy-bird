package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import org.w3c.dom.Text;

import java.util.Random;

public class jogo extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;

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
    private boolean passouCano;


    private Random random;
    BitmapFont pontuacao;


    private float larguraDispositivo;
    private float alturaDispositivo;

    @Override
    public void create() {
        inicializarTexturas();
        inicializarObjetos();

    }

    @Override
    public void render() {
        verificarEstadoJogo();
        validarPontos();
        desenharTexturas();
        detectarColisoes();
    }

    private void detectarColisoes() {
        circuloPassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVertical, passaros[0].getHeight() / 2);

        retanguloCanoBaixo.set(posicaoCanoHorizontal, alturaDispositivo / 2
                        - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
                canoBaixo.getWidth(), canoBaixo.getHeight());

        retanguloCanoCima.set(posicaoCanoHorizontal, alturaDispositivo / 2
                + espacoEntreCanos / 2 + posicaoCanoVertical, canoTopo.getWidth(), canoTopo.getHeight());

        boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
        boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
        if (colidiuCanoCima || colidiuCanoBaixo) {
            Gdx.app.log("Log", "Colidiu com o Cano cima");
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

    private void verificarEstadoJogo() {

        posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
        if (posicaoCanoHorizontal < -canoTopo.getWidth()) {
            posicaoCanoHorizontal = larguraDispositivo;
            posicaoCanoVertical = random.nextInt(400) - 200;
            passouCano = false;
        }

        boolean toqueTela = Gdx.input.justTouched();
        if (toqueTela) {
            gravidade = -15;
        }

        if (posicaoInicialVertical > 0 || toqueTela)
            posicaoInicialVertical = posicaoInicialVertical - gravidade;

        variacao += Gdx.graphics.getDeltaTime() * 10;

        if (variacao > 3)
            variacao = 0;
        gravidade++;
    }

    private void desenharTexturas() {

        batch.begin();
        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 50, posicaoInicialVertical);

        batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo / 2
                - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
        batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2
                + espacoEntreCanos / 2 + posicaoCanoVertical);

        pontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 110);
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

    }

    private void inicializarObjetos() {
        batch = new SpriteBatch();
        random = new Random();


        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 150;

        pontuacao = new BitmapFont();
        pontuacao.setColor(Color.WHITE);
        pontuacao.getData().setScale(10);

        shapeRenderer = new ShapeRenderer();
        circuloPassaro = new Circle();
        retanguloCanoBaixo = new Rectangle();
        retanguloCanoCima = new Rectangle();
    }

    public void validarPontos() {

        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()) {
            if (!passouCano) {
                pontos++;
                passouCano = true;
            }
        }
    }

    @Override
    public void dispose() {
        //Gdx.app.log("dispose", "descarte de conteudos");

    }
}
