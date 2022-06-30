package com.marlon.projetobase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.projetobase.R;
import com.marlon.projetobase.eventbus.MyUpdateCartEvent;
import com.marlon.projetobase.listener.ICartLoadListener;
import com.marlon.projetobase.listener.IRecyclerViewClickListener;
import com.marlon.projetobase.model.CartModel;
import com.marlon.projetobase.model.ProductModel;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder> {

    private Context context;
    private List<ProductModel> productModelList;
    private ICartLoadListener iCartLoadListener;

    public MyProductAdapter(Context context, List<ProductModel> productModelList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.productModelList = productModelList;
        this.iCartLoadListener = iCartLoadListener;
    }

    @NonNull
    @Override
    public MyProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyProductViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyProductViewHolder holder, int position) {
        Glide.with(context)
                .load(productModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtPrice.setText(new StringBuilder("$").append(productModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(productModelList.get(position).getName()));

        holder.setListener((view, adapterPosition) -> {
            addToCart(productModelList.get(position));
        });
    }

    private void addToCart(ProductModel productModel) {
        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID");

        userCart.child(productModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity() + 1);
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("quantity", cartModel.getQuantity());
                            updateData.put("totalPrice", cartModel.getQuantity() * Float.parseFloat(cartModel.getPrice()));

                            userCart.child(productModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        iCartLoadListener.onCartLoadFailed("Adicionado ao carrinho com sucesso");
                                    })
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));

                        }
                        else
                        {
                            CartModel cartModel = new CartModel();
                            cartModel.setName(productModel.getName());
                            cartModel.setImage(productModel.getImage());
                            cartModel.setKey(productModel.getKey());
                            cartModel.setPrice(productModel.getPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(productModel.getPrice()));

                            userCart.child(productModel.getKey())
                                    .setValue(cartModel)
                                    .addOnSuccessListener(aVoid -> {
                                        iCartLoadListener.onCartLoadFailed("Adicionado ao carrinho com sucesso");
                                    })
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                        }
                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class MyProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtPrice)
        TextView txtPrice;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        private Unbinder unbinder;

        public MyProductViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecyclerClick(v, getAdapterPosition());
        }
    }
}
