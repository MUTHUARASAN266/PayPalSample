package com.example.paypalsample.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.paypalsample.PayPalConfig
import com.example.paypalsample.R
import com.example.paypalsample.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.CaptureOrderResult
import com.paypal.checkout.order.OrderRequest
import com.paypal.checkout.order.PurchaseUnit
import org.json.JSONException
import java.math.BigDecimal

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val config = PayPalConfiguration()
        .environment(PayPalConfig.CONFIG_ENVIRONMENT)
        .clientId(PayPalConfig.PAYPAL_CLIENT_ID)
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e(TAG, "onCreate: ")
        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)

        binding.apply {
            validation(binding.edAmount.text.toString())
            payment.setOnClickListener {
                Snackbar.make(it, edAmount.text.toString(), 1000).show()
                makePayment(binding.edAmount.text.toString())
            }
            paymentButtonContainer.setup(createOrder =
            CreateOrder { createOrderActions ->
                val order =
                    OrderRequest(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList =
                        listOf(
                            PurchaseUnit(
                                amount =
                                Amount(
                                    currencyCode = CurrencyCode.USD,
                                    value = binding.edAmount.text.toString()
                                )
                            )
                        )
                    )
                createOrderActions.create(order)
            },
                onApprove = OnApprove { approval ->
                    approval.orderActions.capture { captureOrderResult ->
                        Log.e("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                        val captureOrderResult: CaptureOrderResult.Success =
                            captureOrderResult as CaptureOrderResult.Success
                        val status = captureOrderResult.orderResponse!!.status.name
                        Snackbar.make(binding.root, status, 1000).show()
                        Log.e(TAG, status)
                    }
                }, onCancel = OnCancel {
                    Log.e("OnCancel", "Buyer canceled the PayPal experience.")
                    Snackbar.make(binding.root, "Payment canceled", 1000).show()
                }, onError = OnError { errorInfo ->
                    Log.e("OnError", "Error: $errorInfo")
                    Snackbar.make(binding.root, "Payment Error", 1000).show()

                }
            )
        }
    }

    private fun validation(amount: String) {
        if (amount.isEmpty()) {
            Snackbar.make(binding.root, "please enter amount", 1000).show()
        }
    }

    private fun makePayment(payment: String) {
        validation(payment)

        val payment = PayPalPayment(
            BigDecimal(payment),
            "USD",
            "Payment Description",
            PayPalPayment.PAYMENT_INTENT_SALE
        )
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 1) {
                if (result.resultCode == Activity.RESULT_OK) {
                    val confirm =
                        result.data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                    if (confirm != null) {
                        try {
                            val paymentDetails = confirm.toJSONObject().toString(4)
                            Log.e("${TAG} PayPal", "Payment Confirmation: $paymentDetails")
                            Snackbar.make(this, binding.root, "Payment Confirmation", 1500).show()

                            // Handle the successful payment here
                        } catch (e: JSONException) {
                            Log.e("${TAG} PayPal", "Error parsing payment confirmation data.", e)
                            Snackbar.make(this, binding.root, "Payment Error", 1500).show()
                        }
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    Log.e("${TAG} PayPal", "Payment canceled")
                    Snackbar.make(this, binding.root, "Payment canceled", 1500).show()

                } else if (result.resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.e("${TAG} PayPal", "Invalid PayPal configuration")
                    Snackbar.make(this, binding.root, "Payment Invalid", 1500).show()

                }
            }
        }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")

    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart: ")

    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause: ")

    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop: ")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: ")
    }
}