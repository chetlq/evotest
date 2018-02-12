package com.example.admin.evotest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.IChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEvent;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventProcessor;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;

public class MyDiscountService extends IntegrationService {
    private static final String TAG = "com.example.admin.evotest";

    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(ReceiptDiscountEvent.NAME_SELL_RECEIPT, new ReceiptDiscountEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull ReceiptDiscountEvent event, @NonNull Callback callback){
                List<IPositionChange> listOfChanges = new ArrayList<>();


                JSONObject object = new JSONObject();
                try {
                    object.put("someSuperKey", "AWESOME DISCOUNT");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SetExtra extra = new SetExtra(object);

                 @SuppressLint("LongLogTag") int w = Log.w( TAG, " Servece!");
                try {
                    callback.onResult(

                        new ReceiptDiscountEventResult(
                                new BigDecimal(55),
                                extra,
                                listOfChanges  )

                        );
                }
                catch (RemoteException exc) {
                    exc.printStackTrace();
                }

            }
        });
        return map;
    }
}

// class  qqw implements IPositionChange{
//
//     @org.jetbrains.annotations.Nullable
//     @Override
//     public String getPositionUuid() {
//         return "45d1de17-595d-4e5d-aae9-d3a4d6623c9b" ;
//     }
//
//     @NotNull
//     @Override
//     public Type getType() {
//         return null;
//     }
//
//     @NotNull
//     @Override
//     public Bundle toBundle() {
//         return null;
//     }
// }