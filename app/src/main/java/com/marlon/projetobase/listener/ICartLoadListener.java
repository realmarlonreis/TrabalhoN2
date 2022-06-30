package com.marlon.projetobase.listener;

import com.marlon.projetobase.model.CartModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSucess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
