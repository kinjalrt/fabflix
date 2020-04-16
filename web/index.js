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
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        // rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML +=
                '<th>'+
                '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
                + resultData[i]["title"] +     // display star_name for the link text
                '</a>' + '</th>';
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["dir"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genre1"] + "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-star.html?id=' + resultData[i]["starid1"] + '">'
            + resultData[i]["starname1"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "</tr>";

        rowHTML += "<tr>";
        rowHTML += "<th></th>";
        rowHTML += "<th></th>";
        rowHTML += "<th></th>";
        rowHTML += "<th></th>";
        if (resultData[i]["genre2"] != undefined)
            rowHTML += "<th>" + resultData[i]["genre2"] + "</th>";
        else
            rowHTML += "<th></th>";
        if (resultData[i]["starname2"] != undefined)
            rowHTML +=
                "<th>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="single-star.html?id=' + resultData[i]["starid2"] + '">'
                + resultData[i]["starname2"] +     // display star_name for the link text
                '</a>' +
                "</th>";
        else
            rowHTML += "<th></th>";
        rowHTML += "</tr>";

        rowHTML += "<tr>";
        rowHTML += "<th></th>";
        rowHTML += "<th></th>";
        rowHTML += "<th></th>";
        rowHTML += "<th></th>";
        if (resultData[i]["genre3"] != undefined)
            rowHTML += "<th>" + resultData[i]["genre3"] + "</th>";
        else
            rowHTML += "<th></th>";
        if (resultData[i]["starname3"] != undefined)
            rowHTML +=
                "<th>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="single-star.html?id=' + resultData[i]["starid3"] + '">'
                + resultData[i]["starname3"] +     // display star_name for the link text
                '</a>' +
                "</th>";
        else
            rowHTML += "<th></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/top20", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});