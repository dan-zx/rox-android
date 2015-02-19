package com.grayfox.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

public final class Images {

    private static final String TAG = Images.class.getSimpleName();
    private static final String IMAGE_CACHE_DIR = "images";
    private static final LruCache<String, Bitmap> MEM_CACHE;
    private static DiskLruCache DISK_CACHE;

    static {
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);
        MEM_CACHE = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    private Images() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    private static void intDiskCacheIfNeeded(Context context) {
        if (DISK_CACHE == null || DISK_CACHE.isClosed()) {
            try {
                long size = 1024 * 1024 * 10;
                String cachePath =
                        Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.isExternalStorageRemovable()
                                ? context.getExternalCacheDir().getPath()
                                : context.getCacheDir().getPath();
                File file = new File(cachePath + File.separator + IMAGE_CACHE_DIR);
                DISK_CACHE = DiskLruCache.open(file, 1, 1, size);
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't init disk cache", ex);
            }
        }
    }

    public static Bitmap getFromUrl(Context context, String url) {
        String key = buildKey(url);
        Bitmap cachedBitmap = getFromCache(context, key);
        if (cachedBitmap != null) return cachedBitmap;

        try {
            InputStream stream = new URL(url).openConnection().getInputStream();
            Bitmap downloaded = BitmapFactory.decodeStream(stream);
            saveInCache(context, key, downloaded);
            return downloaded;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Invalid url " + url, ex);
        } catch (Exception ex) {
            // Useless for now
        }

        return null;
    }

    private static String buildKey(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(url.getBytes());
            byte[] data = digest.digest();
            return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't encode url", ex);
        }

        return null;
    }

    public static void clearCache() {
        MEM_CACHE.evictAll();
        if (DISK_CACHE != null) {
            try {
                DISK_CACHE.delete();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't clear disk cache", ex);
            }
        }
    }

    public static Bitmap getFromCache(Context context, String key) {
        Bitmap bitmap = MEM_CACHE.get(key);
        if (bitmap == null) {
            bitmap = getFromDiskCache(context, key);
            if (bitmap != null) MEM_CACHE.put(key, bitmap);
        }
        return bitmap;
    }

    public static void saveInCache(Context context, String key, Bitmap bitmap) {
        MEM_CACHE.put(key, bitmap);
        saveInDiskCache(context, key, bitmap);
    }

    private static Bitmap getFromDiskCache(Context context, String key) {
        intDiskCacheIfNeeded(context);
        DiskLruCache.Snapshot snapshot = null;

        try {
            snapshot = DISK_CACHE.get(key);
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't get image from disk cache", ex);
        }

        if (snapshot != null) {
            BufferedInputStream in = new BufferedInputStream(snapshot.getInputStream(0));
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            snapshot.close();
            return bitmap;
        }

        return null;
    }

    private static void saveInDiskCache(Context context, String key, Bitmap bitmap) {
        intDiskCacheIfNeeded(context);
        DiskLruCache.Editor editor = null;
        OutputStream out = null;
        try {
            editor = DISK_CACHE.edit(key);
            if (editor != null) { // Froyo fix
                out = new BufferedOutputStream(editor.newOutputStream(0));
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    DISK_CACHE.flush();
                    editor.commit();
                } else editor.abort();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't save image in disk cache", ex);
            if (editor != null) {
                try {
                    editor.abort();
                } catch (Exception ex2) {
                    Log.e(TAG, "Couldn't abort saving", ex2);
                }
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                    Log.e(TAG, "Couldn't close stream", ex);
                }
            }
        }
    }

    public static class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        private WeakReference<ImageView> imageViewReference;
        private Integer loadingResourceImageId;
        private Integer loadingColorId;
        private Context context;

        public ImageLoader(Context context) {
            this.context = context.getApplicationContext();
        }

        public ImageLoader setImageView(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
            return this;
        }

        public ImageLoader setLoadingResourceImageId(int loadingResourceImageId) {
            this.loadingResourceImageId = loadingResourceImageId;
            return this;
        }

        public ImageLoader setLoadingColorId(int loadingColorId) {
            this.loadingColorId = loadingColorId;
            return this;
        }

        @Override
        protected void onPreExecute() {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (loadingResourceImageId != null) imageView.setImageResource(loadingResourceImageId);
                if (loadingColorId != null) imageView.setBackgroundColor(context.getResources().getColor(loadingColorId));
            }
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            return getFromUrl(context, url[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) imageView.setImageBitmap(result);
            imageViewReference = null;
            loadingResourceImageId = null;
            loadingColorId = null;
            context = null;
        }
    }
}