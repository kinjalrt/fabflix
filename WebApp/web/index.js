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
    console.log("handleGenreResult: populating genres table from resultData");

    let charactersElement = jQuery("#list_characters");
    let all_chars = '*0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    let charsHTML = "<p class='card-text'>";
    for (let i = 0; i < all_chars.length; i++){
        charsHTML += '<a href="search-list.html?char=' + all_chars[i] + '">' + all_chars[i] + '</a>';
        charsHTML += "&nbsp;&nbsp;&nbsp;";
    }
    charsHTML += "</p>"
    charactersElement.append(charsHTML);


// Populate the star table
// Find the empty table body by id "star_table_body"
    let genresTableBodyElement = jQuery("#genres_table_body");

        // Iterate through resultData, no more than 10 entries
    let rowHTML = "<p class='card-text'>";
    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        rowHTML += '<a href="search-list.html?gid=' + resultData[i]["genre_id"] + '">'
        + resultData[i]["genre_name"] +  '</a>';
        rowHTML += "&nbsp;&nbsp;&nbsp;";
    }
    rowHTML += "</p>"
    genresTableBodyElement.append(rowHTML);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
$.ajax({
    dataType: "json",
    url: "api/index?genre=set",
    method: "GET",
    success: (resultData) => handleGenresResult(resultData)
});

