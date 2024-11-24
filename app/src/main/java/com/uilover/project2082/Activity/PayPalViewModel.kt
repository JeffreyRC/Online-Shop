package com.uilover.project2082.Activity

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PayPalViewModel : ViewModel() {
    private val _paymentStatus = MutableStateFlow<PaymentStatus>(PaymentStatus.NotStarted)
    val paymentStatus = _paymentStatus.asStateFlow()

    fun createOrder(amount: Double) {
        viewModelScope.launch {
            try {
                _paymentStatus.value = PaymentStatus.Processing

                _paymentStatus.value = PaymentStatus.Success
            } catch (e: Exception) {
                _paymentStatus.value = PaymentStatus.Failed(e.message ?: "Payment failed")
            }
        }
    }
}

// Función corregida para crear orden de PayPal
fun createPayPalOrder(amount: Double, context: Context, viewModel: PayPalViewModel) {
    val config = CheckoutConfig(
        application = context.applicationContext as Application,  // Esta es la corrección clave
        clientId = PayPalConfig.CLIENT_ID,
        environment = Environment.SANDBOX,
        currencyCode = CurrencyCode.USD,
        userAction = UserAction.PAY_NOW,
        returnUrl = PayPalConfig.RETURN_URL
    )

    PayPalCheckout.setConfig(config)
    viewModel.createOrder(amount)
}

sealed class PaymentStatus {
    object NotStarted : PaymentStatus()
    object Processing : PaymentStatus()
    object Success : PaymentStatus()
    data class Failed(val error: String) : PaymentStatus()
}