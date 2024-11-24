package com.uilover.project2082.Activity

import android.app.Application
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar PayPal
        val config = CheckoutConfig(
            application = this,  // Aquí es donde necesitamos pasar la application
            clientId = PayPalConfig.CLIENT_ID,
            environment = Environment.SANDBOX,
            currencyCode = CurrencyCode.USD,
            userAction = UserAction.PAY_NOW,
            returnUrl = PayPalConfig.RETURN_URL
        )

        PayPalCheckout.setConfig(config)
    }
}