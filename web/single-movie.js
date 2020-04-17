/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "movie_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Title: " + resultData[0]["movie_title"] + "</p>");



    let yearInfoElement = jQuery("#year");

    // append two html <p> created to the h3 body, which will refresh the page
    yearInfoElement.append("Year: " + resultData[0]["movie_year"]);



    console.log("handleResult: populating movie info table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        rowHTML += "<th>";
        for(var k = 1; k <= parseInt(resultData[i]["genre_count"]); k++)  {
            if(k == 1){
                rowHTML += resultData[i]["movie_genre" + k];
            }
            else {
                rowHTML += ", " + resultData[i]["movie_genre" + k];
            }

        }
        rowHTML += "</th>";

        rowHTML += "<th>";
        for(var l = 1; l <= parseInt(resultData[i]["stars_count"]); l++)  {
            if(l == 1){
                rowHTML += resultData[i]["movie_stars" + l];
            }
            else {
                rowHTML += ", " + resultData[i]["movie_stars" + l];
            }

        }
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>" ;

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});