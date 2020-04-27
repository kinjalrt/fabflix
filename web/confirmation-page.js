$(document).ready(function() {
    var res = window.localStorage.getItem('response');
// Clear storage
    console.log(res);
    console.log("confirmation page print")
    console.log(typeof res)

    var resultData = $.parseJSON(res);

    let orderTableBodyElement = jQuery("#order_table_body");
    let total_price = 0;

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr>';
        rowHTML += '<td>'+ resultData[i]["saleID"] +'</td>';
        rowHTML += '<td>'+ resultData[i]["title"] +'</td>';
        rowHTML += '<td>'+ resultData[i]["quantity"] +'</td>';
        rowHTML += '<td>'+ "$" + 15*resultData[i]["quantity"] +'</td>';
        total_price += 15*resultData[i]["quantity"];
        rowHTML += '</tr>';
        console.log(resultData[i]["saleID"]);
        orderTableBodyElement.append(rowHTML);
    }

    let totalPriceElement = jQuery("#total_price");
    totalPriceElement.append("Total: $"+total_price.toString());

    window.localStorage.clear();


});



