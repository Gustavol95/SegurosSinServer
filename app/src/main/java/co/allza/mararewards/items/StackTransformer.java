package co.allza.mararewards.items;

import android.view.View;

import co.allza.mararewards.interfaces.BaseTransformer;

/**
 * Created by Tavo on 02/08/2016.
 */
public class StackTransformer extends BaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        view.setTranslationX(position < 0 ? 0f : -view.getWidth() * position);
    }
}