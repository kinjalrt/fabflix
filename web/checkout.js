let total = getParameterByName('total');
let total_element = jQuery("#total");
total_element.append("Total: $"+total.toString());
let checkout_form = $("#checkout_form");


function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handlePaymentInfo(checkoutEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    checkoutEvent.preventDefault();

    $.ajax("api/checkout", {
        dataType: "json",
        method: "POST",
        data: checkout_form.serialize(),
        success: (resultData) => handlePaymentResult(resultData)
    });
}

function handlePaymentResult(resultData) {
    //displays error msg if transaction info invalid or redirects to confirmation page if transaction valid
   console.log("checkout complete");
    let paymentOutcome = jQuery("#payment_outcome");
    paymentOutcome.html("");
    if(resultData[0]["result"] === "invalid"){
        paymentOutcome.append("Invalid payment information. Please re-enter payment information.");
    }
    else{
        console.log("valid transaction here");
        var res = JSON.stringify(resultData);
        window.localStorage.setItem('response', res);
        window.location = 'confirmation-page.html';

        for (let i = 0; i < resultData.length; i++){
            sessionStorage.setItem('key', 'value');
            console.log(resultData[i]["saleID"]);

        }
        window.location.replace("confirmation-page.html");
    }

}

checkout_form.submit(handlePaymentInfo);