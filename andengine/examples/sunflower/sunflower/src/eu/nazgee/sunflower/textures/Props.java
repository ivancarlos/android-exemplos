package eu.nazgee.sunflower.textures;

import org.andengine.util.math.MathUtils;

public class Props implements IProps {
	public static final int SEEDS_NUMBER = 2;
	public static final int SEEDS_FIRST = SEEDS_SEED_01_ID;
	public static int getSeedID(int pSeedIndex) {
		return SEEDS_FIRST + MathUtils.bringToBounds(0, SEEDS_NUMBER - 1, pSeedIndex);
	}

	public static final int SUNS_NUMBER = 2;
	public static final int SUNS_FIRST = SUNS_SUN_01_ID;
	public static int getSunID(int pSunIndex) {
		return SUNS_FIRST + MathUtils.bringToBounds(0, SUNS_NUMBER - 1, pSunIndex);
	}
}
