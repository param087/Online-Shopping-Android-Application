package com.example.param.green;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.param.green.staticData.CreateOrder;

import java.util.List;

/**
 * Created by Param on 21-09-2017.
 */

public class CartAdaptor extends RecyclerView.Adapter<CartAdaptor.ItemViewHolder>  {

    public interface ListItemListener{
        void onItemClicked(int clickItemIndex);
    }

    private int mNoOFItem = 0;

    private static String TAG = CartAdaptor.class.getSimpleName();
    final private ListItemListener mOnClickedListener;

    List<CreateOrder> mListOfOrder;

    public CartAdaptor(int mNoOFItem, List<CreateOrder> mListOfOrder, ListItemListener listItemListener){
        this.mListOfOrder = mListOfOrder;
        this.mNoOFItem = mNoOFItem;
        this.mOnClickedListener = listItemListener;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.cartitem;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttackToParentImediatly = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttackToParentImediatly);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Log.d(TAG,"#position#"+position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNoOFItem;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private ImageView mImageViewCart;
        private TextView mTextViewName;
        private TextView mTextViewPrice;
        private TextView mTextViewQnt;
        private TextView mTextViewStatus;

        public ItemViewHolder(View itemView) {
            super(itemView);

            mImageViewCart = (ImageView) itemView.findViewById(R.id.im_cartimage);
            mTextViewName = (TextView) itemView.findViewById(R.id.tv_card_productname);
            mTextViewPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
            mTextViewQnt = (TextView) itemView.findViewById(R.id.tv_cart_qnt);
            mTextViewStatus = (TextView) itemView.findViewById(R.id.tv_cart_status);

            itemView.setOnClickListener(this);
        }

        void bind(int onBindIndex){
            CreateOrder createOrder = mListOfOrder.get(onBindIndex);

            Glide.with(mImageViewCart.getContext())
                    .load(createOrder.getUrl())
                    .into(mImageViewCart);

            mTextViewName.setText(createOrder.getProductName());
            mTextViewStatus.setText(createOrder.getStatus());
            String qnt = String.valueOf(createOrder.getAmount());
            String price = String.valueOf(createOrder.getTotalCost());
            mTextViewQnt.setText(qnt);
            mTextViewPrice.setText(price);
        }

        @Override
        public void onClick(View v) {
            int itemposition = getAdapterPosition();
            mOnClickedListener.onItemClicked(itemposition);
        }
    }
}
