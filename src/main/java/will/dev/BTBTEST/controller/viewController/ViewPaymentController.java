package will.dev.BTBTEST.controller.viewController;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import will.dev.BTBTEST.services.PaypalServices;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ViewPaymentController {
    private final PaypalServices paypalServices;

    @GetMapping("/page-payment")
    public String home(Model model){
        ViewProductController viewProductController = new ViewProductController();
        double amount = viewProductController.totalPrice();  // Valeur par défaut ou calculée dynamiquement
        model.addAttribute("amount", amount);
        return "/payment/page-payment";
    }

    @PostMapping("/payment/create")
    public RedirectView createPayment(
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description
    ){
        try{
            String cancelUrl = "http://localhost:8050/api/payment/cancel";
            String successUrl = "http://localhost:8050/api/payment/success";

            Payment payment = this.paypalServices.createPayment(
                    Double.valueOf(amount),
                    currency,
                    method,
                    "sale",
                    description,
                    cancelUrl,
                    successUrl
            );
            for (Links links: payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    log.info("links :::: "+ links.getHref());
                    return new RedirectView(links.getHref());
                }
            }

        } catch (PayPalRESTException e) {
            log.info("Error occurent ::{}", e.getMessage());
        }
        return new RedirectView("/api/payment/error");
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ){
        try {
            Payment payment = this.paypalServices.executePayment(paymentId,payerId);
            if (payment.getState().equals("approved")){
                return "/payment/paymentSuccess";
            }
        } catch (PayPalRESTException e) {
            log.info("Error occurent ::{}", e.getMessage());
        }
        return "/payment/paymentSuccess";
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel(){
        return "/payment/paymentCancel";
    }

    @GetMapping("/payment/error")
    public String paymentError(){
        return "/payment/paymentError";
    }
}
