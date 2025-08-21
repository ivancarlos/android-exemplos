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
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.widget.Toast;

/**
 * 3 imagens clicáveis, cada uma com seu som.
 * Mantém o estilo do seu código original.
 */
public class SoundExample extends SimpleBaseGameActivity {

    // ===========================================================
    // CONSTANTES
    // ===========================================================

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private static final String GRAPHICS_PATH = "gfx/";
    private static final String SOUNDS_PATH  = "mfx/";

    // Cores de fundo
    private static final float BACKGROUND_RED   = 0.09804f;
    private static final float BACKGROUND_GREEN = 0.6274f;
    private static final float BACKGROUND_BLUE  = 0.8784f;

    // ===========================================================
    // ESTRUTURA PARA ITENS
    // ===========================================================

    private static class ItemDef {
        final String imageFile;
        final String soundFile;
        final float x, y;

        // preenchidos em runtime
        BitmapTextureAtlas atlas;
        ITextureRegion texture;
        Sound sound;
        Sprite sprite;

        ItemDef(String imageFile, String soundFile, float x, float y) {
            this.imageFile = imageFile;
            this.soundFile = soundFile;
            this.x = x;
            this.y = y;
        }
    }

    // Sua lista pedida: 3 imagens clicáveis
    private final List<ItemDef> ITEMS = new ArrayList<ItemDef>() {{
        add(new ItemDef("tank.png",       "explosion.ogg",   100, 200));
        add(new ItemDef("helicopter.png", "helicopter.ogg",  300, 120));
        add(new ItemDef("jeep.png",       "horn.ogg",        520, 260));
    }};

    // ===========================================================
    // CAMPOS
    // ===========================================================

    // (removemos os campos únicos antigos e passamos a usar a lista ITEMS)

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

        // Para evitar BlackPawn builder, criamos 1 atlas por imagem.
        // Tamanho 256x256 costuma servir para ícones médios; aumente se necessário.
        for (ItemDef item : ITEMS) {
            item.atlas = new BitmapTextureAtlas(
                    getTextureManager(),
                    256, 256,
                    TextureOptions.BILINEAR
            );

            // Posiciona a textura dentro do atlas no canto (0,0) de cada atlas
            item.texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                    item.atlas, this, item.imageFile, 0, 0
            );

            item.atlas.load();
        }

        Debug.d("Gráficos carregados para " + ITEMS.size() + " itens.");
    }

    private void loadSounds() {
        SoundFactory.setAssetBasePath(SOUNDS_PATH);

        for (ItemDef item : ITEMS) {
            try {
                item.sound = SoundFactory.createSoundFromAsset(
                        mEngine.getSoundManager(), this, item.soundFile
                );
            } catch (IOException e) {
                Debug.e("Erro ao carregar som: " + item.soundFile, e);
                showError("Falha ao carregar " + item.soundFile);
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
        scene.setBackground(new Background(BACKGROUND_RED, BACKGROUND_GREEN, BACKGROUND_BLUE));

        // Cria e posiciona os sprites
        for (ItemDef item : ITEMS) {
            if (item.texture == null) continue;

            item.sprite = new Sprite(
                    item.x,
                    item.y,
                    item.texture,
                    getVertexBufferObjectManager()
            );
            // Guardamos o próprio item como userData para identificar no toque
            item.sprite.setUserData(item);

            scene.attachChild(item.sprite);
            scene.registerTouchArea(item.sprite);
        }

        // Um único listener para todos
        scene.setOnAreaTouchListener(new IOnAreaTouchListener() {
            @Override
            public boolean onAreaTouched(
                    final TouchEvent event,
                    final ITouchArea area,
                    final float localX,
                    final float localY
            ) {
                if (event.isActionDown() && area instanceof Sprite) {
                    final Object data = ((Sprite) area).getUserData();
                    if (data instanceof ItemDef) {
                        playItemSound((ItemDef) data);
                    }
                    return true;
                }
                return false;
            }
        });

        Debug.d("Cena criada com " + ITEMS.size() + " sprites clicáveis.");
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
            showError("Som não disponível para este item.");
        }
    }

    private void showInstructions() {
        Toast.makeText(this, "🎮 Toque nas imagens para ouvir seus sons!", Toast.LENGTH_LONG).show();
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

        // Libera sons
        for (ItemDef item : ITEMS) {
            if (item.sound != null && !item.sound.isReleased()) {
                item.sound.release();
            }
        }
        Debug.d("Recursos liberados");
    }
}

