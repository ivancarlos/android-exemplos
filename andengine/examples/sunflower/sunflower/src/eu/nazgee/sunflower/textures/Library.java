package eu.nazgee.sunflower.textures;

import java.util.LinkedList;

import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.util.adt.color.Color;

import android.content.Context;
import eu.nazgee.sunflower.Consts;

public class Library implements ITexturesLibrary {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private BitmapTextureAtlas mFontAtlas;
	private Font mFont;
	private LinkedList<ITexturesLibrary> mLibs = new LinkedList<ITexturesLibrary>();
	private final TexturesLibraryProps mProps;
	private final TexturesLibraryTiles mTiles;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Library(final Engine e, final Context c) {
		mProps = new TexturesLibraryProps(e, c);
		mTiles = new TexturesLibraryTiles(e, c);
		mLibs.add(getProps());
		mLibs.add(getTiles());
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public TexturesLibraryProps getProps() {
		return mProps;
	}

	public TexturesLibraryTiles getTiles() {
		return mTiles;
	}

	public Font getFont() {
		return mFont;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public void load(final Engine e, final Context c) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		FontFactory.setAssetBasePath("fonts/");

		// Load spritesheets
		for (ITexturesLibrary lib : this.mLibs) {
			lib.load(e, c);
		}

		// Load fonts
		final TextureManager textureManager = e.getTextureManager();
		final FontManager fontManager = e.getFontManager();

		mFontAtlas = new BitmapTextureAtlas(textureManager, 256, 256, TextureOptions.BILINEAR);
		mFont = FontFactory.createFromAsset(fontManager, mFontAtlas, c.getAssets(), Consts.FONT_NAME, Consts.FONT_SIZE, true, Color.WHITE.getARGBPackedInt());
		mFont.load();
	}

	public void unload() {
		// Load spritesheets
		for (ITexturesLibrary lib : this.mLibs) {
			lib.unload();
		}
		mFont.unload();
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}