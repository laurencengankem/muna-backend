package com.example.kulvida.payment.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeService {

    @Value("${stripe.private.key:sk_test_51LzTYfAltJ5ptHM4v3FcN8ToLr5j0cpqK2izEqyZ5SdYSY3YRtpMomtyPgU9TfJA9lwMAkULr5AgeCwrA4TvL4n800ywH0jpSX}")
    private String stripePrivateKey;

    @Value("${payment.success.url:http://localhost:9000/payment/stripe/success?orderId=}")
    private String successUrl;

    @Value("${payment.cancel.url:http://localhost:4200/payment/cancel}")
    private String cancelUrl;

    public String createPaymentSession(List<SessionCreateParams.LineItem> listItems,String orderId) throws StripeException {
        Stripe.apiKey =stripePrivateKey;
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl+orderId)
                        .setCancelUrl(cancelUrl)
                        .addAllLineItem(listItems)
                        .build();
        Session session = Session.create(params);
        return session.getUrl();
    }

    public SessionCreateParams.LineItem createLineItem(Long quantity,String currency,Long unitAmount, String name){
        return SessionCreateParams.LineItem.builder()
                .setQuantity(quantity)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency)
                        .setUnitAmount(unitAmount)
                        .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(name)
                                        .build()
                        ).build()
                ).build();
    }
}
