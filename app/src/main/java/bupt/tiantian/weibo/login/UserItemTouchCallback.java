package bupt.tiantian.weibo.login;

import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import bupt.tiantian.weibo.R;

/**
 * Created by tiantian on 16-12-21.
 */
public abstract class UserItemTouchCallback extends ItemTouchHelper.Callback {
    /**
     * 这个方法是用来设置我们拖动的方向以及侧滑的方向的
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //如果是ListView样式的RecyclerView
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            //设置拖拽方向为上下
            // final int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
            final int dragFlags = 0;
            //设置侧滑方向
            final int swipeFlags;
            if (viewHolder.getAdapterPosition() == recyclerView.getAdapter().getItemCount() - 1) {
                swipeFlags = ItemTouchHelper.END;//最后一项只许右划
            }else{
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;//设置侧滑方向为从左到右和从右到左都可以
            }
            //将方向参数设置进去
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {//如果是GridView样式的RecyclerView
            return 0;
        }
    }

    /**
     * 拖动item时会回调此方法
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;//不处理拖动事件
    }


    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        // 释放View时回调，清除背景颜色，隐藏图标
        // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
        getDefaultUIUtil().clearView(((UserAdapter.ViewHolder) viewHolder).userCard);
        ((UserAdapter.ViewHolder) viewHolder).rlItem.setBackgroundResource(R.drawable.card_background);
        ((UserAdapter.ViewHolder) viewHolder).ivLogin.setVisibility(View.GONE);
        ((UserAdapter.ViewHolder) viewHolder).ivLogout.setVisibility(View.GONE);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState,
                            boolean isCurrentlyActive) {
        // ItemTouchHelper的onDraw方法会调用该方法，可以使用Canvas对象进行绘制，绘制的图案会在RecyclerView的下方
        // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
        getDefaultUIUtil().onDraw(c, recyclerView,
                ((UserAdapter.ViewHolder) viewHolder).userCard,
                dX, dY, actionState, isCurrentlyActive);
        if (dX > 0) { // 向右滑动时的提示
            ((UserAdapter.ViewHolder) viewHolder).rlItem.setBackgroundResource(R.drawable.card_background_green);
            ((UserAdapter.ViewHolder) viewHolder).ivLogin.setVisibility(View.VISIBLE);
            ((UserAdapter.ViewHolder) viewHolder).ivLogout.setVisibility(View.GONE);
        }
        if (dX < 0) { // 向左滑动时的提示
            ((UserAdapter.ViewHolder) viewHolder).rlItem.setBackgroundResource(R.drawable.card_background_red);
            ((UserAdapter.ViewHolder) viewHolder).ivLogout.setVisibility(View.VISIBLE);
            ((UserAdapter.ViewHolder) viewHolder).ivLogin.setVisibility(View.GONE);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState,
                                boolean isCurrentlyActive) {
//        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        getDefaultUIUtil().onDrawOver(c, recyclerView,
                ((UserAdapter.ViewHolder) viewHolder).userCard,
                dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // 当viewHolder的滑动或拖拽状态改变时回调
        if (viewHolder != null) {
            // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
            getDefaultUIUtil().onSelected(((UserAdapter.ViewHolder) viewHolder).userCard);
        }
    }
}
