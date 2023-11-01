package com.example.paypalsample

import com.paypal.android.sdk.payments.PayPalConfiguration

class PayPalConfig {
    companion object{
        const val PAYPAL_CLIENT_ID = "AVCiu_eDdGA99zj9MBVgNvaEE7ksKZka6L7vmwOjlBU5vdkw0nNbQDRZxFZ7n6YodGDWbP4gnJ10uxMy"
        const val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX // or ENVIRONMENT_PRODUCTION for live
        const val returnUrl = "com.example.paypalsample://paypalpay"
    }
}