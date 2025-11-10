package eu.nazgee.sunflower.textures;

import org.andengine.engine.Engine;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.content.Context;

public class TexturesLibraryProps extends TexturesLibraryBase {

	public TexturesLibraryProps(Engine e, Context c) {
		super("props.xml", e, c);
	}

	public ITextureRegion getSeed(int pSeedId) {
		return mSpritesheet.getTexturePackTextureRegionLibrary().get(Props.getSeedID(pSeedId));
	}

	public ITextureRegion getSun(int pSunId) {
		return mSpritesheet.getTexturePackTextureRegionLibrary().get(Props.getSunID(pSunId));
	}

	public ITextureRegion getSnowman() {
		return mSpritesheet.getTexturePackTextureRegionLibrary().get(Props.SNOWMAN_ID);
	}
}
