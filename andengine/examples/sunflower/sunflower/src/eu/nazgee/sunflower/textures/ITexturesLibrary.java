package eu.nazgee.sunflower.textures;

import org.andengine.engine.Engine;

import android.content.Context;

public interface ITexturesLibrary {
	public void load(final Engine e, final Context c);
	public void unload();
}
