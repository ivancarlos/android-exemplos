package org.andengine.examples;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
//import org.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class WallPaper extends SimpleBaseGameActivity {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;
    private static final String GRAPHICS_PATH = "gfx/";

    private final List<String> IMAGE_FILES = new ArrayList<String>() {{
        add("wall1.png");
        add("wall2.jpg");
        add("wall3.png");
    }};

    private final List<ITextureRegion> TEXTURE_REGIONS = new ArrayList<ITextureRegion>();
    private final List<BitmapTextureAtlas> ATLASES = new ArrayList<BitmapTextureAtlas>();

    private int currentIndex = 0;
    private Scene scene;
    private Sprite backgroundSprite;

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(
                true,
                ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
                camera
        );
    }

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(GRAPHICS_PATH);

        for (String file : IMAGE_FILES) {
            BitmapTextureAtlas atlas = new BitmapTextureAtlas(getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT, TextureOptions.BILINEAR);
            ITextureRegion texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, this, file, 0, 0);
            atlas.load();
            ATLASES.add(atlas);
            TEXTURE_REGIONS.add(texture);
        }
    }

    @Override
    public Scene onCreateScene() {
        scene = new Scene();
        showNextWallpaper();

        scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene scene, TouchEvent event) {
                if (event.isActionDown()) {
                    showNextWallpaper();
                    return true;
                }
                return false;
            }
        });

        return scene;
    }

    private void showNextWallpaper() {
        if (backgroundSprite != null) {
            scene.detachChild(backgroundSprite);
        }

        ITextureRegion texture = TEXTURE_REGIONS.get(currentIndex);

        backgroundSprite = new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, texture, getVertexBufferObjectManager());
        scene.attachChild(backgroundSprite);

        currentIndex = (currentIndex + 1) % TEXTURE_REGIONS.size();
    }
}

