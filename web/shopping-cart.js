let cart = $("#cart");

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


/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleCartData(resultData) {
    console.log(resultData);

    let cartTableBody = $("#cart_table_body");
    cartTableBody.html("");
    // change it to html list

    let total = 0;

    for (let i = 0; i < resultData.length; i++) {
        // each item will be in a bullet point
        let rowHTML = "";
        rowHTML += "<tr>";
        // rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<td>" + resultData[i]["title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["quantity"] + "</td>";
        rowHTML += "<td>" + "$"+ 15*resultData[i]["quantity"] + "</td>";
        total += 15*resultData[i]["quantity"];
        let action = "";
        action = "add";
        rowHTML += "<td>" + '<button onclick="updateQuantity(\'' + resultData[i]["title"] + '\', \'' + action + '\')"> Add a copy </button>' + "</td>";
        action = "remove";
        rowHTML += "<td>" + '<button onclick="updateQuantity(\'' + resultData[i]["title"] + '\', \'' + action + '\')"> Remove a copy </button>' + "</td>";
        action = "del";
        rowHTML += "<td>" + '<button onclick="updateQuantity(\'' + resultData[i]["title"] + '\', \'' + action + '\')"> Delete from shopping cart </button>' + "</td>";
        rowHTML += "</tr>";
        cartTableBody.append(rowHTML);
    }

    let checkoutButtonElement = $("#checkout_button");
    checkoutButtonElement.html("");
    checkoutButtonElement.append('<a href="checkout.html?total=' + total + '"> Proceed to checkout </a>');

    // clear the old array and show the new array in the frontend
}

function updateQuantity(title, action){
    $.ajax({
        dataType: "json",
        url: "api/cart?item="+title+"&action="+action,
        method: "GET",
        success: (resultData) => handleCartData(resultData)
    });

}


let item = getParameterByName('item');

$.ajax({
    dataType: "json",
    url: "api/cart?item="+item,
    method: "GET",
    success: (resultData) => handleCartData(resultData)
});
