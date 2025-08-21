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
 * Exemplo: 3 imagens lado a lado, cada uma com som próprio.
 */
public class SoundExample extends SimpleBaseGameActivity {

    // ===========================================================
    // CONSTANTES
    // ===========================================================

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private static final String GRAPHICS_PATH = "gfx/";
    private static final String SOUNDS_PATH  = "mfx/";

    private static final float BG_R = 0.09804f;
    private static final float BG_G = 0.6274f;
    private static final float BG_B = 0.8784f;

    private static final float PADDING_PX = 20f; // espaço entre imagens

    // ===========================================================
    // ESTRUTURA DE ITEM
    // ===========================================================

    private static class ItemDef {
        final String imageFile;
        final String soundFile;

        BitmapTextureAtlas atlas;
        ITextureRegion texture;
        Sound sound;
        Sprite sprite;

        ItemDef(String imageFile, String soundFile) {
            this.imageFile = imageFile;
            this.soundFile = soundFile;
        }
    }

    // 3 itens (imagens + sons)
    private final List<ItemDef> ITEMS = new ArrayList<ItemDef>() {{
        add(new ItemDef("tank2.png",      "explosion.ogg"));
        add(new ItemDef("helicopter.png", "helicopter.ogg"));
        add(new ItemDef("jeep.png",       "horn.ogg"));
    }};

    // ===========================================================
    // ENGINE
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {
        showInstructions();

        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(
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

        for (ItemDef item : ITEMS) {
            item.atlas = new BitmapTextureAtlas(
                    getTextureManager(),
                    256, 256,
                    TextureOptions.BILINEAR
            );

            item.texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                    item.atlas, this, item.imageFile, 0, 0
            );

            item.atlas.load();
        }
        Debug.d("Gráficos carregados.");
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
        scene.setBackground(new Background(BG_R, BG_G, BG_B));

        // calcular largura total
        float totalWidth = 0f;
        for (ItemDef item : ITEMS) {
            if (item.texture != null) {
                totalWidth += item.texture.getWidth();
            }
        }
        if (ITEMS.size() > 1) {
            totalWidth += PADDING_PX * (ITEMS.size() - 1);
        }

        float startX = (CAMERA_WIDTH - totalWidth) / 2f;
        float currentX = startX;

        // criar sprites lado a lado
        for (ItemDef item : ITEMS) {
            if (item.texture == null) continue;

            float y = (CAMERA_HEIGHT - item.texture.getHeight()) / 2f;

            item.sprite = new Sprite(
                    currentX,
                    y,
                    item.texture,
                    getVertexBufferObjectManager()
            );
            item.sprite.setUserData(item);

            scene.attachChild(item.sprite);
            scene.registerTouchArea(item.sprite);

            currentX += item.texture.getWidth() + PADDING_PX;
        }

        // listener único
        scene.setOnAreaTouchListener(new IOnAreaTouchListener() {
            @Override
            public boolean onAreaTouched(TouchEvent event, ITouchArea area,
                                         float localX, float localY) {
                if (event.isActionDown() && area instanceof Sprite) {
                    Object data = ((Sprite) area).getUserData();
                    if (data instanceof ItemDef) {
                        playItemSound((ItemDef) data);
                    }
                    return true;
                }
                return false;
            }
        });

        return scene;
    }

    // ===========================================================
    // AUXILIARES
    // ===========================================================

    private void playItemSound(ItemDef item) {
        if (item.sound != null) {
            item.sound.play();
            Debug.d("Som reproduzido: " + item.soundFile);
        } else {
            showError("Som não disponível.");
        }
    }

    private void showInstructions() {
        Toast.makeText(this,
                "🎮 Toque nas imagens para ouvir seus sons!",
                Toast.LENGTH_LONG).show();
    }

    private void showError(String msg) {
        Toast.makeText(this, "❌ " + msg, Toast.LENGTH_SHORT).show();
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

