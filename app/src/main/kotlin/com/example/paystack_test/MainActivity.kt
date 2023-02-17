package com.example.paystack_test

import android.app.Activity
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import androidx.activity.ComponentActivity

import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson

class MainActivity: FlutterActivity() {

    private val CHANNEL = "flutterwave.irecharge/payments";

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method.equals("processTransactions")) {
                val flutterwave = processTransactions();

                if (flutterwave != null) {
                    result.success(flutterwave);
                } else {
                    result.error("UNAVAILABLE", "Flutterwave not available.", null);
                }
            } else {
                result.notImplemented();
            }
        }
    }

    private fun processTransactions() {

         val startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                val result = activityResult?.data
                val transactionData = result?.getStringExtra("TRANSACTION_EXTRA")

            }

        val intent = Intent("com.flutterwave.pos.TRANSACTION")
        val paymentRequest = FlutterwaveTransaction(
            amount = "2.00",
            shouldPrint = true,
            transactionType = listOf("CARD")
        )
        val jsonRequest = Gson().toJson(paymentRequest)
        intent.putExtra("PAY", jsonRequest)
        startActivityForResult.launch(intent)

    }

    private fun <I, O> Activity.registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ) = (this as ComponentActivity).registerForActivityResult(contract, callback)
}
