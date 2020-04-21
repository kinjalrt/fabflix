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
function handleGenresResult(resultData) {
    console.log("handleStarResult: populating genres table from resultData");

    let charactersElement = jQuery("#list_characters");
    let all_chars = '*0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    let charsHTML = "";
    for (let i = 0; i < all_chars.length; i++){
        charsHTML += '<a href="search-list.html?char=' + all_chars[i] + '">' + all_chars[i] + '</a>';
        charsHTML += "&nbsp;&nbsp;&nbsp;";
    }
    charactersElement.append(charsHTML);


// Populate the star table
// Find the empty table body by id "star_table_body"
    let genresTableBodyElement = jQuery("#genres_table_body");

        // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

            // Concatenate the html tags with resultData jsonObject
            let rowHTML = "";
            rowHTML += "<tr>";
            // rowHTML += "<th>" + resultData[i]["title"] + "</th>";
            rowHTML += '<td>' +
            '<a href="search-list.html?gid=' + resultData[i]["genre_id"] + '">'
            + resultData[i]["genre_name"] +     // display star_name for the link text
            '</a>' + '</td>';

            rowHTML += "</tr>";

            // Append the row created to the table body, which will refresh the page
            genresTableBodyElement.append(rowHTML);
        }




}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
$.ajax({
    dataType: "json",
    url: "api/top20?genre=set",
    method: "GET",
    success: (resultData) => handleGenresResult(resultData)
});

