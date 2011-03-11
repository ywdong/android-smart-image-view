package com.loopj.android.image;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.util.AttributeSet;

public class SmartImageView extends ImageView {
    private static final int LOADING_THREADS = 4;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(LOADING_THREADS);

    private SmartImageTask currentTask;

    public SmartImageView(Context context) {
        super(context);
    }

    public SmartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(String url) {
        setImage(new WebImage(getContext(), url));
    }

    public void setImageContact(int contactId) {
        setImage(new ContactImage(getContext(), contactId));
    }

    public void setImage(final SmartImage image) {
        setImage(image, null, null);
    }

    public void setImage(final SmartImage image, final Integer fallbackResource) {
        setImage(image, fallbackResource, null);
    }

    public void setImage(final SmartImage image, final Integer fallbackResource, final Integer loadingResource) {
        // Set a loading resource
        if(loadingResource != null && getDrawable() != null){
            setImageResource(loadingResource);
        }

        // Cancel any existing tasks for this image view
        if(currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        // Set up the new task
        currentTask = new SmartImageTask(image);
        currentTask.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if(bitmap != null) {
                    setImageBitmap(bitmap);
                } else {
                    // Set fallback resource
                    if(fallbackResource != null) {
                        setImageResource(fallbackResource);
                    }
                }
            }
        });

        // Run the task in a threadpool
        threadPool.execute(currentTask);
    }
}