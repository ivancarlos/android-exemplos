package org.andengine.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.widget.Toast;

/**
 * Várias imagens clicáveis, cada uma com um som diferente.
 * - Defina seus itens na lista ITEMS (arquivo de imagem, arquivo de som, posição X/Y).
 * - Não cria imagens nem áudios; apenas organiza o código.
 */
public class SoundExample extends SimpleBaseGameActivity {

    // ===========================================================
    // CONSTANTES
    // ===========================================================

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    // Pastas de assets
    private static final String GRAPHICS_PATH = "gfx/";
    private static final String SOUNDS_PATH  = "mfx/";

    // Tamanho do atlas (aumente se tiver muitas/maiores imagens)
    private static final int ATLAS_WIDTH  = 1024;
    private static final int ATLAS_HEIGHT = 1024;

    // Cor de fundo
    private static final float BG_R = 0.09804f;
    private static final float BG_G = 0.6274f;
    private static final float BG_B = 0.8784f;

    // ===========================================================
    // TIPOS/ESTRUTURAS
    // ===========================================================

    /** Define um item clicável (imagem + som + posição) */
    private static class ItemDef {
        final String imageFile;
        final String soundFile;
        final float x;
        final float y;

        // preenchidos em tempo de execução:
        ITextureRegion textureRegion;
        Sound sound;
        Sprite sprite;

        ItemDef(String imageFile, String soundFile, float x, float y) {
            this.imageFile = imageFile;
            this.soundFile = soundFile;
            this.x = x;
            this.y = y;
        }
    }

    // Configure aqui os itens (exemplos; ajuste os nomes/posições aos seus arquivos)
    private final List<ItemDef> ITEMS = new ArrayList<ItemDef>() {{
        add(new ItemDef("tank.png",       "explosion.ogg",   100, 200));
        add(new ItemDef("helicopter.png", "helicopter.ogg",  300, 120));
        add(new ItemDef("jeep.png",       "horn.ogg",        520, 260));
    }};

    // ===========================================================
    // CAMPOS
    // ===========================================================

    private BuildableBitmapTextureAtlas mTextureAtlas;

    // ===========================================================
    // ENGINE
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {
        showInstructions();

        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        final EngineOptions engineOptions = new EngineOptions(
                true,
                ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
                camera
        );

        engineOptions.getAudioOptions().setNeedsSound(true);
        return engineOptions;
    }

    // ===========================================================
    // RECURSOS
    // ===========================================================

    @Override
    public void onCreateResources() {
        loadGraphics();
        loadSounds();
    }

    private void loadGraphics() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(GRAPHICS_PATH);

        mTextureAtlas = new BuildableBitmapTextureAtlas(
                getTextureManager(),
                ATLAS_WIDTH,
                ATLAS_HEIGHT,
                TextureOptions.BILINEAR
        );

        // cria uma região de textura para cada imagem de item
        for (ItemDef item : ITEMS) {
            item.textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                    mTextureAtlas, this, item.imageFile);
        }

        try {
            // empacota as imagens no atlas automaticamente
            mTextureAtlas.build(new BlackPawnTextureAtlasBuilder<BuildableBitmapTextureAtlas>(0, 1, 1));
            mTextureAtlas.load();
            Debug.d("Texturas carregadas/empacotadas.");
        } catch (TextureAtlasBuilderException e) {
            Debug.e("Falha ao construir atlas de texturas.", e);
            showError("Erro ao carregar gráficos. Verifique os arquivos em /assets/gfx.");
        }
    }

    private void loadSounds() {
        SoundFactory.setAssetBasePath(SOUNDS_PATH);

        for (ItemDef item : ITEMS) {
            try {
                item.sound = SoundFactory.createSoundFromAsset(
                        getSoundManager(), this, item.soundFile);
            } catch (IOException e) {
                Debug.e("Erro ao carregar som: " + item.soundFile, e);
                showError("Não foi possível carregar: " + item.soundFile);
            }
        }
        Debug.d("Sons carregados.");
    }

    // ===========================================================
    // CENA
    // ===========================================================

    @Override
    public Scene onCreateScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new Background(BG_R, BG_G, BG_B));

        // cria sprites para cada item
        for (ItemDef item : ITEMS) {
            if (item.textureRegion == null) continue;

            item.sprite = new Sprite(
                    item.x,
                    item.y,
                    item.textureRegion,
                    getVertexBufferObjectManager()
            );
            // guarda referência do próprio item para recuperar no toque
            item.sprite.setUserData(item);

            scene.attachChild(item.sprite);
            scene.registerTouchArea(item.sprite);
        }

        // listener único para todos os sprites
        scene.setOnAreaTouchListener(new IOnAreaTouchListener() {
            @Override
            public boolean onAreaTouched(final TouchEvent event,
                                         final ITouchArea area,
                                         final float localX, final float localY) {
                if (event.isActionDown() && area instanceof Sprite) {
                    final Object data = ((Sprite) area).getUserData();
                    if (data instanceof ItemDef) {
                        playItemSound(((ItemDef) data));
                    }
                    return true;
                }
                return false;
            }
        });

        Debug.d("Cena criada com " + ITEMS.size() + " itens clicáveis.");
        return scene;
    }

    // ===========================================================
    // AUXILIARES
    // ===========================================================

    private void playItemSound(final ItemDef item) {
        if (item.sound != null) {
            item.sound.play();
            Debug.d("Som reproduzido: " + item.soundFile);
        } else {
            Debug.w("Som indisponível para " + item.imageFile);
            showError("Som indisponível para este item.");
        }
    }

    private void showInstructions() {
        Toast.makeText(
                this,
                "🎮 Toque em qualquer imagem para ouvir seu som.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void showError(final String message) {
        Toast.makeText(this, "❌ " + message, Toast.LENGTH_SHORT).show();
    }

    // ===========================================================
    // LIMPEZA
    // ===========================================================

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ItemDef item : ITEMS) {
            if (item.sound != null && !item.sound.isReleased()) {
                item.sound.release();
            }
        }
        Debug.d("Recursos liberados.");
    }
}

