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


$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});



function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
   // console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here
    var res = window.sessionStorage.getItem(query);

    if (res!==null){
        console.log("Retrieving cached results for query " + query);
        var resultData = $.parseJSON(res);
        handleLookupAjaxSuccess(resultData, query, doneCallback)

    } else {
        console.log("Sending ajax request to server for query "+ query);
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "movie-suggestions?query=" + escape(query),
            "success": function (data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function (errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }

}


function handleLookupAjaxSuccess(data, query, doneCallback) {
    //console.log("lookup ajax successful")

    // parse the string into JSON
   // var jsonData = JSON.parse(data);
    var res = window.sessionStorage.getItem(query);
    if (res!==null){
      //  console.log("PAIR EXIST "+ query + " " + res);
    }else{
        var resultData = JSON.stringify(data);
      //  console.log("STORING PAIR "+ query + " " + resultData);
        window.sessionStorage.setItem(query, resultData);
    }

    var printData = JSON.stringify(data);
    console.log("Suggestion list: "+ printData);

    // TODO: if you want to cache the result into a global variable you can do it here


    doneCallback( { suggestions: data } );


    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
}


function handleSelectSuggestion(suggestion) {

    // TODO: jump to the specific result page based on the selected suggestion
    window.location.replace("single-movie.html?id=" + suggestion["data"]["movieID"]);

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
}

$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})

function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    window.location.replace("search-list.html?title="+query)

}





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

