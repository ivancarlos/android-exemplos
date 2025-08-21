package org.andengine.examples;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.widget.Toast;

/**
 * Exemplo melhorado demonstrando reprodução de som no AndEngine
 *
 * Funcionalidades:
 * - Toque no tank para reproduzir som de explosão
 * - Tratamento de erros aprimorado
 * - Código mais organizado e documentado
 */
public class SoundExample extends SimpleBaseGameActivity {

    // ===========================================================
    // CONSTANTES
    // ===========================================================

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    // Configurações de assets
    private static final String GRAPHICS_PATH = "gfx/";
    private static final String SOUNDS_PATH = "mfx/";
    private static final String TANK_IMAGE = "tank.png";
    private static final String EXPLOSION_SOUND = "explosion.ogg";

    // Configurações de textura
    private static final int TEXTURE_ATLAS_WIDTH = 128;
    private static final int TEXTURE_ATLAS_HEIGHT = 256;

    // Cores (RGB normalizado)
    private static final float BACKGROUND_RED = 0.09804f;   // Azul céu
    private static final float BACKGROUND_GREEN = 0.6274f;
    private static final float BACKGROUND_BLUE = 0.8784f;

    // ===========================================================
    // CAMPOS DA CLASSE
    // ===========================================================

    private BitmapTextureAtlas mTextureAtlas;
    private ITextureRegion mTankTextureRegion;
    private Sound mExplosionSound;

    private Sprite mTankSprite;

    // ===========================================================
    // CONFIGURAÇÃO DO ENGINE
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {
        showInstructions();

        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        final EngineOptions engineOptions = new EngineOptions(
            true, // VSync habilitado
            ScreenOrientation.LANDSCAPE_FIXED,
            new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
            camera
        );

        // Habilitar sistema de áudio
        engineOptions.getAudioOptions().setNeedsSound(true);

        return engineOptions;
    }

    // ===========================================================
    // CARREGAMENTO DE RECURSOS
    // ===========================================================

    @Override
    public void onCreateResources() {
        loadGraphics();
        loadSounds();
    }

    /**
     * Carrega texturas e sprites
     */
    private void loadGraphics() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(GRAPHICS_PATH);

        this.mTextureAtlas = new BitmapTextureAtlas(
            this.getTextureManager(),
            TEXTURE_ATLAS_WIDTH,
            TEXTURE_ATLAS_HEIGHT,
            TextureOptions.BILINEAR
        );

        this.mTankTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
            this.mTextureAtlas,
            this,
            TANK_IMAGE,
            0, 0
        );

        this.mTextureAtlas.load();

        Debug.d("Gráficos carregados com sucesso");
    }

    /**
     * Carrega arquivos de áudio
     */
    private void loadSounds() {
        SoundFactory.setAssetBasePath(SOUNDS_PATH);

        try {
            this.mExplosionSound = SoundFactory.createSoundFromAsset(
                this.mEngine.getSoundManager(),
                this,
                EXPLOSION_SOUND
            );
            Debug.d("Sons carregados com sucesso");
        } catch (final IOException e) {
            Debug.e("Erro ao carregar som: " + EXPLOSION_SOUND, e);
            showError("Erro ao carregar áudio. Verifique se o arquivo existe.");
        }
    }

    // ===========================================================
    // CRIAÇÃO DA CENA
    // ===========================================================

    @Override
    public Scene onCreateScene() {
        // Logger de FPS para debug
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = createScene();
        setupTankSprite(scene);
        setupTouchHandling(scene);

        return scene;
    }

    /**
     * Cria e configura a cena principal
     */
    private Scene createScene() {
        final Scene scene = new Scene();

        // Fundo azul céu
        scene.setBackground(new Background(
            BACKGROUND_RED,
            BACKGROUND_GREEN,
            BACKGROUND_BLUE
        ));

        return scene;
    }

    /**
     * Configura e posiciona o sprite do tank
     */
    private void setupTankSprite(final Scene scene) {
        if (this.mTankTextureRegion == null) {
            Debug.e("Tank texture region não foi carregada!");
            return;
        }

        // Centralizar tank na tela
        final float centerX = (CAMERA_WIDTH - this.mTankTextureRegion.getWidth()) / 2f;
        final float centerY = (CAMERA_HEIGHT - this.mTankTextureRegion.getHeight()) / 2f;

        this.mTankSprite = new Sprite(
            centerX,
            centerY,
            this.mTankTextureRegion,
            this.getVertexBufferObjectManager()
        );

        scene.attachChild(this.mTankSprite);
        Debug.d("Tank sprite criado na posição: (" + centerX + ", " + centerY + ")");
    }

    /**
     * Configura o sistema de toque na tela
     */
    private void setupTouchHandling(final Scene scene) {
        if (this.mTankSprite == null) {
            Debug.e("Tank sprite não foi criado!");
            return;
        }

        scene.registerTouchArea(this.mTankSprite);
        scene.setOnAreaTouchListener(new IOnAreaTouchListener() {
            @Override
            public boolean onAreaTouched(
                final TouchEvent pSceneTouchEvent,
                final ITouchArea pTouchArea,
                final float pTouchAreaLocalX,
                final float pTouchAreaLocalY
            ) {
                if (pSceneTouchEvent.isActionDown()) {
                    playExplosionSound();
                }
                return true;
            }
        });

        Debug.d("Sistema de toque configurado");
    }

    // ===========================================================
    // MÉTODOS AUXILIARES
    // ===========================================================

    /**
     * Reproduz o som de explosão
     */
    private void playExplosionSound() {
        if (this.mExplosionSound != null) {
            this.mExplosionSound.play();
            Debug.d("Som de explosão reproduzido");
        } else {
            Debug.w("Som de explosão não está disponível");
            showError("Som não disponível");
        }
    }

    /**
     * Mostra instruções para o usuário
     */
    private void showInstructions() {
        Toast.makeText(
            this,
            "🎮 Toque no tank para ouvir uma explosão!",
            Toast.LENGTH_LONG
        ).show();
    }

    /**
     * Mostra mensagem de erro
     */
    private void showError(final String message) {
        Toast.makeText(this, "❌ " + message, Toast.LENGTH_SHORT).show();
    }

    // ===========================================================
    // LIMPEZA DE RECURSOS
    // ===========================================================

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Limpar recursos de áudio
        if (this.mExplosionSound != null) {
            if (!this.mExplosionSound.isReleased()) {
                this.mExplosionSound.release();
            }
        }

        Debug.d("Recursos liberados");
    }
}
