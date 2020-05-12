let add_star_form = $("#add_star_form");
/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {

    console.log("handle result")
    let feedback = $("#status");
    feedback.html("");
    feedback.append(resultData[0]["status"]);
    let dashboardTableBodyElement = jQuery("#dashboard_table_body");
    for (let i = 1; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        //add table name
        rowHTML += "<td>" + resultData[i]["table_name"] + "</td>";

        //add Atttributes
        rowHTML += "<td>";
        let colCount = 0;
        while(resultData[i]["col_count"] != colCount){
            if(colCount === 0)
                rowHTML+= resultData[i]["attr_name"+(++colCount)];
            else
                rowHTML += "<br>" + resultData[i]["attr_name"+(++colCount)];
        }
        rowHTML += "</td>";

        //add Type
        rowHTML += "<td>";
        colCount = 0;
        while(resultData[i]["col_count"] != colCount){
            if(colCount === 0)
                rowHTML+= resultData[i]["attr_type"+(++colCount)];
            else
                rowHTML += "<br>" + resultData[i]["attr_type"+(++colCount)];
        }
        rowHTML += "</td>";

        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        dashboardTableBodyElement.append(rowHTML);
    }
}

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
 * Once this .js is loaded, following scripts will be executed by the browser
 */

let star = getParameterByName("star");
let birthYear = getParameterByName("birth_year");

// let star = getParameterByName("star");
// Makes the HTTP GET request and registers on success callback function handleStarResult
$.ajax({
    dataType: "json",
    url: "api/dashboard?star="+star+"&birthYear="+birthYear,
    method: "GET",
    success: (resultData) => handleResult(resultData)
});

