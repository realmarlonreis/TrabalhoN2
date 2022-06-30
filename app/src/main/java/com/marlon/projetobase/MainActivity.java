package com.marlon.projetobase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.projetobase.adapter.MyProductAdapter;
import com.marlon.projetobase.eventbus.MyUpdateCartEvent;
import com.marlon.projetobase.listener.ICartLoadListener;
import com.marlon.projetobase.listener.IProductLoadListener;
import com.marlon.projetobase.model.CartModel;
import com.marlon.projetobase.model.ProductModel;
import com.marlon.projetobase.utils.SpaceItemDecoration;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements IProductLoadListener, ICartLoadListener {
    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;

    IProductLoadListener productLoadListener;
    ICartLoadListener cartLoadListener;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdateCart (MyUpdateCartEvent event){
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        loadProductFromFirebase();
        countCartItem();
    }

    private void loadProductFromFirebase() {
        List<ProductModel> productModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Product")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                ProductModel productModel = productSnapshot.getValue(ProductModel.class);
                                productModel.setKey(productSnapshot.getKey());
                                productModels.add(productModel);
                            }
                            productLoadListener.onProductLoadSucess(productModels);
                        } else
                            productLoadListener.onProductLoadFailed("Não é possível encontrar o produto.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        productLoadListener.onProductLoadFailed(error.getMessage());
                    }
                });
    }

    private void init() {
        ButterKnife.bind(this);

        productLoadListener = this;
        cartLoadListener = this;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerProduct.setLayoutManager(gridLayoutManager);
        recyclerProduct.addItemDecoration(new SpaceItemDecoration());

        btnCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }

    @Override
    public void onProductLoadSucess(List<ProductModel> productModelList) {
        MyProductAdapter adapter = new MyProductAdapter(this, productModelList, cartLoadListener);
        recyclerProduct.setAdapter(adapter);
    }

    @Override
    public void onProductLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSucess(List<CartModel> cartModelList) {

        int cartSum = 0;
        for (CartModel cartModel : cartModelList)
            cartSum += cartModel.getQuantity();
        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()){
                            CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSucess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}