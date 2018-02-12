package com.example.admin.evotest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import retrofit2.Retrofit;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;

public class MainActivity extends AppCompatActivity implements MyResultReceiver.Receiver{
    private static final String TAG = "com.example.admin.evotest";
    public MyResultReceiver mReceiver;
    private Button conButton;
    private Button servButton;

    public static final String BROADCAST_FILTER = "com.example.admin.evotest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, MyIntentService.class);

        servButton = (Button) findViewById(R.id.service_button);
        servButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("LongLogTag") int w = Log.w(TAG,new StringBuilder(">>>>>>>servButton: ").toString() );
                startService(intent);

               // Toast.makeText(MainActivity.this, "Lol chto", Toast.LENGTH_SHORT).show();

            }
        });

        conButton = (Button) findViewById(R.id.con_button);
        conButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("LongLogTag") int w = Log.w(TAG,new StringBuilder("UUID: ").append(UUID.randomUUID().toString()).toString() );
                startService(intent);
                openReceipt();
                // Toast.makeText(MainActivity.this, "Lol chto", Toast.LENGTH_SHORT).show();

            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
                Log.w( TAG, intent.getStringExtra( "Hello" ) );
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter( BROADCAST_FILTER );
        registerReceiver( broadcastReceiver, intentFilter );
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver( broadcastReceiver );
    }

    public void openReceipt() {
        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Зубочистки"));
        //Создание списка товаров чека
        List<PositionAdd> positionAddList = new ArrayList<>();
        positionAddList.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                //UUID позиции
                                "45d1de17-595d-4e5d-aae9-d3a4d6623c9b",
                                //UUID товара
                                null,
                                //Наименование
                                "Зубочистки",
                                //Наименование единицы измерения
                                "кг",
                                //Точность единицы измерения
                                0,
                                //Цена без скидок
                                new BigDecimal(200),
                                //Количество
                                new BigDecimal(1)
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        ).setPrice( new BigDecimal(100) ).build()
                        // .setPriceWithDiscountPosition(new BigDecimal(100)).setExtraKeys(set).build()

                )
        );

        //Дополнительные поля в чеке для использования в приложении
        JSONObject object = new JSONObject();
        try {
            object.put("someSuperKey", "AWESOME RECEIPT OPEN");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SetExtra extra = new SetExtra(object);

        //Открытие чека продажи. Передаются: список наименований, дополнительные поля для приложения
        new OpenSellReceiptCommand(positionAddList, null).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        startActivity(new Intent("evotor.intent.action.payment.SELL"));
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
         Log.w(TAG,new StringBuilder(">>>>>>>>>>onReceiveResult ").toString() );


        switch (resultCode) {
            case 0:
                 Log.w(TAG,new StringBuilder(">>>>>>>>>>onReceiveResult0 ").toString() );
                String results2 = resultData.getString( "Hello" );
                Toast toast = Toast.makeText(getApplicationContext(),
                        results2, Toast.LENGTH_SHORT);
                toast.show();
                break;
            case 1:
                Log.w(TAG,new StringBuilder(">>>>>>>>>>onReceiveResult1 ").toString() );

//                Book book1 = resultData.getParcelable("book");
//                String results3 = resultData.getString( "Hello" );
//                Toast toast2 = Toast.makeText(getApplicationContext(),
//                        (CharSequence) book1.title, Toast.LENGTH_SHORT);
//                toast2.show();
                // do something interesting
                // hide progress
                break;
            case 2:
                Log.w(TAG,new StringBuilder(">>>>>>>>>>onReceiveResult2 ").toString() );
                // handle the error;
                break;
        }
    }
}
