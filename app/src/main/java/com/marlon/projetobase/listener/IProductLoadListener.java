package com.marlon.projetobase.listener;

import com.marlon.projetobase.model.ProductModel;

import java.util.List;

public interface IProductLoadListener {
    void onProductLoadSucess(List<ProductModel> productModelList);
    void onProductLoadFailed(String message);
}
