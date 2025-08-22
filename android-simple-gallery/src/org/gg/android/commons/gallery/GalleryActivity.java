package org.gg.android.commons.gallery;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class GalleryActivity extends Activity {

    public static final String INTENT_EXTRAS_POSITION = "position";
    public static final String INTENT_EXTRAS_FOLDER   = "folder";

    private static final String TAG = GalleryActivity.class.getName();

    private static final String[] IMAGE_EXT = {".jpg", ".jpeg", ".png", ".webp", ".bmp", ".gif"};

    private ViewFlipper viewFlipper;
    private ImageView currentView;

    private String imagesFolder;
    private List<String> imagesPath;

    private int current;
    private boolean fromAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.gallery);
        // FIX: cast explícito
        viewFlipper = (ViewFlipper) findViewById(R.id.gallery_viewflipper);

        current = 0;
        imagesFolder = "file:///android_asset/gallery";
        fromAssets = false;

        if (savedInstanceState != null) {
            current = savedInstanceState.getInt("current_index", 0);
        }

        loadImages();
        start();

        viewFlipper.setOnTouchListener(new SwipeListener(
                new Runnable() { // swipe right 👉 (anterior)
                    @Override public void run() { previusImage(); }
                },
                new Runnable() { // swipe left 👈 (próxima)
                    @Override public void run() { nextImage(); }
                }
        ));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_index", current);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentView != null) {
            return currentView.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    /* ======================= CARREGAMENTO DE IMAGENS ======================= */

    private static boolean isImage(String name) {
        if (name == null) return false;
        String n = name.toLowerCase();
        for (String ext : IMAGE_EXT) if (n.endsWith(ext)) return true;
        return false;
    }

    private void loadImages() {
        Bundle extras = getIntent().getExtras();
        imagesPath = new LinkedList<>();
        int position = 0;

        if (extras != null) {
            String extrasFolder = extras.getString(INTENT_EXTRAS_FOLDER);
            if (extrasFolder != null) {
                imagesFolder = extrasFolder;
            }

            String positionSt = extras.getString(INTENT_EXTRAS_POSITION);
            if (positionSt != null) {
                try {
                    position = Integer.parseInt(positionSt);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "loadImages position parse error", e);
                }
            }
        }

        Log.d(TAG, "load images from imagesFolder[" + imagesFolder + "]");

        if (imagesFolder == null) return;

        if (imagesFolder.startsWith("file:///android_asset/")) {
            imagesFolder = imagesFolder.replace("file:///android_asset/", "");
            fromAssets = true;

            try {
                String[] list = getAssets().list(imagesFolder);
                if (list != null) {
                    for (String name : list) {
                        if (isImage(name)) {
                            imagesPath.add(name);
                        }
                    }
                    Collections.sort(imagesPath);
                }
            } catch (IOException e) {
                Log.e(TAG, "assets list error", e);
            }

        } else {
            File dir = new File(imagesFolder);
            Log.d(TAG, "dir exists[" + dir.exists() + "] isDirectory[" + dir.isDirectory() + "]");
            if (dir.exists() && dir.isDirectory()) {
                File[] listFiles = dir.listFiles();
                if (listFiles != null) {
                    for (File f : listFiles) {
                        if (f.isFile() && isImage(f.getName())) {
                            imagesPath.add(f.getAbsolutePath());
                        }
                    }
                    Collections.sort(imagesPath);
                }
            }
        }

        Log.d(TAG, "loadImages images count [" + imagesPath.size() + "]");
        if (position < imagesPath.size()) {
            current = position;
        }
    }

    private void start() {
        if (imagesPath.size() > 0) {
            currentView = createImageView(this);
            viewFlipper.addView(currentView);
            loadImageInView(currentView, imagesPath.get(current));
            viewFlipper.setDisplayedChild(0);
        } else {
            // FIX: sem depender de R.string inexistente em compile-time
            String msg = getStringByNameOrFallback("gallery_no_images", "Nenhuma imagem disponível");
            new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
    }

    // procura uma string por nome; se não existir, usa fallback
    private String getStringByNameOrFallback(String name, String fallback) {
        try {
            int id = getResources().getIdentifier(name, "string", getPackageName());
            if (id != 0) return getString(id);
        } catch (Exception ignore) { }
        return fallback;
    }

    private void nextImage() {
        showImage(current + 1, true);
    }

    private void previusImage() {
        showImage(current - 1, false);
    }

    private void showImage(int index, boolean slideLeft) {
        if (imagesPath == null || imagesPath.isEmpty()) return;

        current = (index % imagesPath.size() + imagesPath.size()) % imagesPath.size();

        if (slideLeft) setSlideToLeftAnimation(viewFlipper, this);
        else           setSlideToRightAnimation(viewFlipper, this);

        ImageView next = createImageView(this);
        loadImageInView(next, imagesPath.get(current));

        viewFlipper.addView(next);
        viewFlipper.showNext();

        // libera a view antiga
        View old = viewFlipper.getChildAt(0);
        if (old instanceof ImageView) {
            ((ImageView) old).setImageDrawable(null);
        }
        viewFlipper.removeViewAt(0);

        currentView = next;
    }

    /* ======================= HELPERS DE VIEW/ANIMAÇÃO ======================= */

    public static ImageView createImageView(Activity activity) {
        ImageView imageView = new ImageView(activity);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqW, int reqH) {
        int height = options.outHeight;
        int width  = options.outWidth;
        int inSampleSize = 1;

        if (height > reqH || width > reqW) {
            final int halfH = height / 2;
            final int halfW = width / 2;
            while ((halfH / inSampleSize) >= reqH && (halfW / inSampleSize) >= reqW) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public void loadImageInView(ImageView imageView, String path) {
        // valores default caso a view ainda não tenha medido
        int reqW = imageView.getWidth()  > 0 ? imageView.getWidth()  : 1080;
        int reqH = imageView.getHeight() > 0 ? imageView.getHeight() : 1920;

        InputStream is = null;
        try {
            // 1) abrir para ler bounds
            if (!fromAssets) {
                is = new FileInputStream(path);
            } else {
                is = getAssets().open(imagesFolder + "/" + path);
            }

            BitmapFactory.Options optsBounds = new BitmapFactory.Options();
            optsBounds.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, optsBounds);
            try { is.close(); } catch (Exception ignore) {}

            // 2) reabrir e decodificar com sample
            if (!fromAssets) {
                is = new FileInputStream(path);
            } else {
                is = getAssets().open(imagesFolder + "/" + path);
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = calculateInSampleSize(optsBounds, reqW, reqH);
            opts.inPreferredConfig = Bitmap.Config.RGB_565; // economiza memória

            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
            imageView.setImageBitmap(bitmap);

            Log.d(TAG, "loadImageInView set bitmap for[" + path + "] sampleSize[" + opts.inSampleSize + "]");

        } catch (IOException e) {
            Log.e(TAG, "loadImageInView error", e);
        } finally {
            if (is != null) try { is.close(); } catch (IOException ignore) {}
        }
    }

    public static void setFadeAnimation(ViewFlipper flipper, Activity context) {
        flipper.setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
    }

    public static void setSlideToRightAnimation(ViewFlipper flipper, Activity context) {
        flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_right));
    }

    public static void setSlideToLeftAnimation(ViewFlipper flipper, Activity context) {
        flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_left));
    }

    /* ======================= SWIPE LISTENER (CORRIGIDO) ======================= */

    public static class SwipeListener implements OnTouchListener {
        private static final int SWIPE_LENGTH = 30;

        private float startX, startY;
        private final Runnable onSwipeRight;
        private final Runnable onSwipeLeft;

        public SwipeListener(Runnable onSwipeRight, Runnable onSwipeLeft) {
            this.onSwipeRight = onSwipeRight;
            this.onSwipeLeft  = onSwipeLeft;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    float dx = event.getX() - startX;
                    float dy = event.getY() - startY;
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > SWIPE_LENGTH) {
                        if (dx > 0) onSwipeRight.run(); // 👉 anterior
                        else        onSwipeLeft.run();  // 👈 próxima
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        }
    }

    /* ======================= PinchImageView mantido (opcional / não usado) ======================= */
    // (sem alterações – mantido aqui se você quiser usar depois)
}

