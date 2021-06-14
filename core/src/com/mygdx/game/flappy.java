package com.mygdx.game;

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


public class flappy extends ApplicationAdapter {

	private SpriteBatch batch;
	//texturas
	private Texture[] _bird;
	private Texture background_;
	private Texture gameOver;
	private Texture fundo;
	private Texture ouro;
	private Texture prata;
	private Texture canoAlto;
	private Texture logo;
	private Texture canoBaixo;

	//passou pelo cano?
	private boolean passouCano = false;
	private Random random;
	private Random randomValue;

	//txt
	BitmapFont txtScore;
	BitmapFont txtHighScore;
	BitmapFont txtReset;

	//audio
	Sound somMoedas;
	Sound somPontuacao;
	Sound somColisao;
	Sound somVoando;

	Preferences preferencias;


	private int maxScore = 0;
	private int score = 0;
	//current score
	private float posicaoHorizontalPassaro = 0;
	private int estadojogo = 0;
	private int prataValue = 0;
	int valor = 1;
	//gravidade/peso
	private int gravidade = 0;
	//animação começa 0
	private float variacao = 0;
	private Rectangle collCanoCima;
	private Rectangle collCanoBaixo;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	//altura da tela
	private float alturadispositivo;
	//espaçamento sem colisão
	private float espacoEntreCanos;
	private float posicaoCanoVertical;
	private Circle collOuro;
	private Circle collPrata;
	//largura da tela
	private float larguradispositivo;
	int valortoque = 0;
	private int valorRadomTest = 3;
	private float posPrata;
	private float posOuro;
	private float posVertCoin;
	private ShapeRenderer shapeRenderer;
	private Circle collBird_;

	@Override
	public void create() {
		inicializaTexuras();
		inicializarObjetos();
	}

	private void inicializarObjetos() {
		//colisores
		shapeRenderer = new ShapeRenderer();
		collBird_ = new Circle();
		collCanoCima = new Rectangle();
		collCanoBaixo = new Rectangle();
		collOuro = new Circle();
		collPrata = new Circle();

		batch = new SpriteBatch();
		random = new Random();
		randomValue = new Random();
		//reconhecendo altura do dispositivo e imprimindo na variavel
		alturadispositivo = Gdx.graphics.getHeight();
		//reconhecendo largura do dispositivo e imprimindo na variavel
		larguradispositivo = Gdx.graphics.getWidth();
		posicaoInicialVerticalPassaro = alturadispositivo / 2;
		posVertCoin = alturadispositivo / 2;
		posicaoCanoHorizontal = larguradispositivo;
		posOuro = larguradispositivo;
		posPrata = larguradispositivo;
		espacoEntreCanos = 1350;

		//textos
		txtScore = new BitmapFont();
		txtScore.setColor(Color.WHITE);
		txtReset = new BitmapFont();
		txtScore.getData().setScale(10);
		txtReset.setColor(Color.GREEN);
		txtReset.getData().setScale(3.5f);
		txtHighScore = new BitmapFont();
		txtHighScore.setColor(Color.RED);
		txtHighScore.getData().setScale(4);

		//audios files
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somMoedas = Gdx.audio.newSound(Gdx.files.internal("retro_coin.wav"));
		preferencias = Gdx.app.getPreferences("flappyBird");

		//highscore
		maxScore = preferencias.getInteger("pontuacaoMaxima", 0);
	}

	private void inicializaTexuras() {
		//atribui textura para as variaveis
		background_ = new Texture("fundo.png");
		prata = new Texture("prata.png");
		ouro = new Texture("ouro.png");
		logo = new Texture("logoteste.png");
		fundo = new Texture("black.png");
		gameOver = new Texture("game_over.png");
		_bird = new Texture[3];
		_bird[0] = new Texture("passaro1.png");
		_bird[1] = new Texture("passaro2.png");
		_bird[2] = new Texture("passaro3.png");
		canoAlto = new Texture("cano_topo_maior.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
	}

	@Override
	public void render() {
		detectarColisao();
		validarPontos();
		desenharTexturas();
		verificaEstadojogo();
	}

	private void validarPontos() {
		//se passou pelo cano
		if (posicaoCanoHorizontal < 50 - _bird[0].getWidth()) {
			if (!passouCano) {
				//+1 score
				score++;
				//ja passou
				passouCano = true;
				somPontuacao.play();
				 }
		}
		//velocidade da animação do array do bird_
		variacao += Gdx.graphics.getDeltaTime() * 10;
		if (variacao > 2)
		{
			variacao = 0;
		}
	}

	private void verificaEstadojogo() {
		//clicou na tela?
		boolean toqueTela = Gdx.input.justTouched();
		if (estadojogo == 0) {
			//ao clicar
			if (Gdx.input.justTouched()) {
				gravidade = -15;
				estadojogo = 1;
				somVoando.play();
			}
		}
		else if (estadojogo == 1) {
			valor = 0;
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if (posicaoCanoHorizontal < -canoBaixo.getWidth()) {
				posicaoCanoHorizontal = larguradispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}
			if (prataValue >= valorRadomTest) {
				posOuro -= Gdx.graphics.getDeltaTime() * 150;
				if (posOuro < -ouro.getWidth()) {
					posOuro = larguradispositivo;
					posVertCoin = random.nextInt(300) - 200;
					prataValue = 0;
					valorRadomTest = randomValue.nextInt(10) +1;
				}
			}
			posPrata -= Gdx.graphics.getDeltaTime() * 150;
			if (posPrata < -prata.getWidth()) {
				posPrata = larguradispositivo;
				posVertCoin = random.nextInt(300) - 200;
			}
			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
				gravidade++;
		}
		else if (estadojogo == 2) {
			if (score > maxScore)
			{
				maxScore = score;
				preferencias.putInteger("pontuacaoMaxima", maxScore);
			}
			if (toqueTela && valortoque == 1)
			{
				//limpa estado do jogo
				estadojogo = 0;
				score = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturadispositivo / 2;
				//atribui largura da tela à posição do cano
				posicaoCanoHorizontal = larguradispositivo;
				posOuro = larguradispositivo;
				posPrata = larguradispositivo;
				prataValue = 0;
				valortoque = 0;
			posicaoInicialVerticalPassaro -= Gdx.graphics.getDeltaTime() * 500;
			if (toqueTela)
			{
				valortoque ++;
			}
			}
		}
	}

	private void desenharTexturas() {
		batch.begin();
		//renderiza background
		batch.draw(background_, 0, 0, larguradispositivo, alturadispositivo);
		if (prataValue <= valorRadomTest) {
			batch.draw(prata, posPrata, alturadispositivo /2 + posVertCoin + prata.getHeight() / 2f, prata.getWidth()*2, prata.getWidth()*2);
		}
		//renderiza player
		batch.draw(_bird[(int) variacao], 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro, 92*3, 60*3);
		//renderiza com tamanho ajustado o obstaculo na cena
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth()*2, canoBaixo.getHeight()*2);
		//renderiza com tamanho ajustado o obstaculo na cena
		batch.draw(canoAlto, posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoAlto.getWidth()*2, canoAlto.getHeight()*2);
		if (estadojogo == 2) {
			batch.draw(gameOver, larguradispositivo / 2 - gameOver.getWidth() / 3f, alturadispositivo / 2);
			txtReset.draw(batch, "Click to restart", larguradispositivo / 2 - 200, alturadispositivo / 2 - gameOver.getHeight() / 2f);
			txtHighScore.draw(batch, "HIGHSCORE: " + maxScore, larguradispositivo / 2 - 500, alturadispositivo / 2 - gameOver.getHeight() * 2);
		}
		if (prataValue >= valorRadomTest) {
			batch.draw(ouro, posOuro, alturadispositivo /2 + posVertCoin + ouro.getHeight() / 2f, ouro.getWidth()*2, ouro.getWidth()*2);
		}
		if(estadojogo == 1)
		{
			//atualiza score ao passar pelo cano
			txtScore.draw(batch, String.valueOf(score), larguradispositivo / 2, alturadispositivo - 100);
		}
		if (estadojogo == 0 && valor == 1 )
		{
			batch.draw(fundo,posicaoHorizontalPassaro,alturadispositivo/2 +200,1200,800);
			batch.draw(fundo,posicaoHorizontalPassaro,-350,1200,800);
			batch.draw(logo,posicaoHorizontalPassaro,alturadispositivo /6,1200,2153);
		}
		batch.end();
	}

	private void detectarColisao() {
		boolean prataCollision = Intersector.overlaps(collBird_, collPrata);
		boolean ouroCollision = Intersector.overlaps(collBird_, collOuro);
		boolean bateuCanoCima = Intersector.overlaps(collBird_, collCanoCima);
		boolean bateuCanoBaixo = Intersector.overlaps(collBird_, collCanoBaixo);
		if (bateuCanoBaixo || bateuCanoCima) {
			//se colidir com cano muda estado do jogo para estado 2
			if (estadojogo == 1) {
				somColisao.play();
				estadojogo = 2;
				variacao = 3;
			}
		}
		//colisão moeda prata
		if (prataCollision) {
			//se edtado = 1
			if (estadojogo == 1) {
				score += 5;
				somMoedas.play();
				posPrata = larguradispositivo;
				prataValue++;
			}
		}
		//colisão moeda ouro
		if (ouroCollision) {
			//se estado = 1
			if (estadojogo == 1) {
				score += 10;
				prataValue = 0;
				somMoedas.play();
				posOuro = larguradispositivo;
				valorRadomTest = randomValue.nextInt(10) +1;
			}
		}
		collPrata.set(posPrata, alturadispositivo /2 + posVertCoin + prata.getHeight() / 2f, prata.getWidth() / 2f);
		collOuro.set(posOuro, alturadispositivo /2 + posVertCoin + ouro.getHeight() / 2f, ouro.getWidth() / 2f);
		collBird_.set(50 + _bird[0].getWidth() / 2f, posicaoInicialVerticalPassaro + _bird[0].getHeight() / 2f, _bird[0].getWidth() / 2f);
		collCanoBaixo.set(posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth()*2, canoBaixo.getHeight()*2);
		collCanoCima.set(posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoAlto.getWidth()*2, canoAlto.getHeight()*2);
	}

	@Override
	public void dispose() {
	}

}