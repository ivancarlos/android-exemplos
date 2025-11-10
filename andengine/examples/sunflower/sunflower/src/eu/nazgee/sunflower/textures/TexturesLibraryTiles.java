package eu.nazgee.sunflower.textures;

import org.andengine.engine.Engine;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.content.Context;

public class TexturesLibraryTiles extends TexturesLibraryBase {

	public TexturesLibraryTiles(Engine e, Context c) {
		super("tiles.xml", e, c);
	}

	public ITextureRegion getGrass() {
		return mSpritesheet.getTexturePackTextureRegionLibrary().get(Tiles.GRASS_ID);
	}
}
