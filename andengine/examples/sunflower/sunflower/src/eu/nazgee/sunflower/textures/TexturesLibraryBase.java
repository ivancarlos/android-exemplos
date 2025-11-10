package eu.nazgee.sunflower.textures;

import org.andengine.engine.Engine;
import org.andengine.util.debug.Debug;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.exception.TexturePackParseException;

import eu.nazgee.sunflower.Consts;

import android.content.Context;

public class TexturesLibraryBase implements ITexturesLibrary {

	protected TexturePack mSpritesheet;

	public TexturesLibraryBase(final String pSpritesheetName, Engine e, Context c) {
		try {
			mSpritesheet = new TexturePackLoader(c.getAssets(), e.getTextureManager()).loadFromAsset(Consts.PATH_SPRITESHEETS + pSpritesheetName, Consts.PATH_SPRITESHEETS);
		} catch (final TexturePackParseException ex) {
			Debug.e(ex);
		}
	}

	@Override
	public void load(Engine e, Context c) {
		mSpritesheet.loadTexture();
	}

	@Override
	public void unload() {
		mSpritesheet.unloadTexture();
	}
}
