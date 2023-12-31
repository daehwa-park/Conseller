package com.conseller.conseller.store;

import com.conseller.conseller.entity.Store;
import com.conseller.conseller.store.dto.request.ModifyStoreRequest;
import com.conseller.conseller.store.dto.request.RegistStoreRequest;
import com.conseller.conseller.store.dto.request.StoreListRequest;
import com.conseller.conseller.store.dto.response.DetailStoreResponse;
import com.conseller.conseller.store.dto.response.StoreConfirmResponse;
import com.conseller.conseller.store.dto.response.StoreListResponse;
import com.conseller.conseller.store.dto.response.StoreTradeResponse;

import java.util.List;

public interface StoreService {
    public StoreListResponse getStoreList(StoreListRequest request);

    public Long registStore(RegistStoreRequest request);

    public DetailStoreResponse detailStore(Long storeIdx);

    public void modifyStore(Long storeIdx , ModifyStoreRequest storeRequest);

    public void deleteStore(Long storeIdx);

    public StoreTradeResponse tradeStore(Long storeIdx, Long consumerIdx);

    public void cancelStore(Long storeIdx);

    public void confirmStore(Long storeIdx);

    public StoreConfirmResponse getConfirmStore(Long storeIdx);

    public List<Store> getStoreConfirmList();

    public List<Store> getStoreExpiredList();

    public void rejectStore(Long storeIdx);

    public List<Integer> getMainCategory();

    public List<Integer> getSubCategory();

}
