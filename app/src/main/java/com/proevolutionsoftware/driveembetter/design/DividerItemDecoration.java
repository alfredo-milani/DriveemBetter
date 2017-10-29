package com.proevolutionsoftware.driveembetter.design;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.proevolutionsoftware.driveembetter.R;

/**
 * Created by alfredo on 08/09/17.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    // Widgets
    private Drawable mDivider;

    public DividerItemDecoration(Context context) {
        this.mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + this.mDivider.getIntrinsicHeight();

            this.mDivider.setBounds(left, top, right - 100, bottom);
            this.mDivider.draw(c);
        }
    }
}