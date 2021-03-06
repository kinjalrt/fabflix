let search_form = $("#search_form");

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
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");
    if(firstRecord == null || firstRecord == 0)
        document.getElementById("prevButton").disabled = true;

    sessionStorage.setItem("movieList",window.location.href);
    // Find the empty table body by id "movie_table_body"
    if(resultData[0]["result"] != "success") {
        movieTableBodyElement.append(resultData[0]["result"]);
    }
    else {
        // Iterate through resultData, no more than 10 entries
        for (let i = 0; i < resultData.length; i++) {
            // Concatenate the html tags with resultData jsonObject
            let rowHTML = "";
            rowHTML += "<tr>";
            // rowHTML += "<th>" + resultData[i]["title"] + "</th>";
            rowHTML +=
                '<td>' +
                '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
                + resultData[i]["title"] +     // display star_name for the link text
                '</a>' + '</td>';
            rowHTML += "<td>" + resultData[i]["year"] + "</td>";
            rowHTML += "<td>" + resultData[i]["dir"] + "</td>";

            if(resultData[i]["rating"]==="0" || resultData[i]["rating"]===0){
                rowHTML += "<td> N/A </td>" ;
            }else{
                rowHTML += "<td>" + resultData[i]["rating"] + "</td>" ;
            }
           // rowHTML += "<td>" + resultData[i]["rating"] + "</td>";

            rowHTML += "<td>";
            let count = 1;
            //y - added hyperlink
            while (resultData[i]["genre" + count] != undefined) {
                if (count == 1)
                    rowHTML += '<a href="search-list.html?gid=' + resultData[i]["gid" + count] + '">' + resultData[i]["genre" + count] + '</a>';
                else
                    rowHTML += '<br />' + '<a href="search-list.html?gid=' + resultData[i]["gid" + count] + '">' + resultData[i]["genre" + count] + '</a>';
                count += 1;
            }
            rowHTML += "</td>";
            rowHTML += '<td>';
            let index = 1;
            while (resultData[i]["starname" + index] != undefined) {
                if (index == 1)
                    rowHTML += '<a href="single-star.html?id=' + resultData[i]["starid" + index] + '">' + resultData[i]["starname" + index] + '</a>';
                else
                    rowHTML += '<br /> ' + '<a href="single-star.html?id=' + resultData[i]["starid" + index] + '">' + resultData[i]["starname" + index] + '</a>';
                index += 1;
            }
            rowHTML += "</td>";
           // rowHTML +=  '<td><button onclick=\"window.location.href = \'shopping-cart.html?action=add&item=\'+\'' + resultData[i]["title"] + '\';\">Add to CARF</button></td>';
            rowHTML += "<td>" + '<a href="shopping-cart.html?action=add&item=' + resultData[i]["title"] + '">' + "Add to cart" + '</a>' + "</td>";
            rowHTML += "</tr>";

            // Append the row created to the table body, which will refresh the page
            movieTableBodyElement.append(rowHTML);
        }
    }
}

function sortBy(type, sort) {
    var sp = new URL(window.location.href);
    if(type == 'Title' && sort == 'A'){
        sp.searchParams.set('sort', 'title');
    }
    else if(type == 'Title' && sort == 'D'){
        sp.searchParams.set('sort','title desc');
    }
    else if(type == 'Rating' && sort == 'A'){
        sp.searchParams.set('sort','rating');
    }
    else if(type == 'Rating' && sort == 'D'){
        sp.searchParams.set('sort','rating desc');
    }
    else {
        var s = sp.searchParams.get('sort');
        if(s != null) {
            if(type == 'Title2' && sort == 'A' && !s.startsWith('title')){
                sp.searchParams.set('sort', s+',title');
            }
            else if(type == 'Title2' && sort == 'D' && !s.startsWith('title')){
                sp.searchParams.set('sort', s+',title desc');
            }
            else if(type == 'Rating2' && sort == 'A' && !s.startsWith('rating')){
                sp.searchParams.set('sort', s+',rating');
            }
            else if(type == 'Rating2' && sort == 'D' && !s.startsWith('rating')){
                sp.searchParams.set('sort', s+',rating desc');
            }
        }
    }

    window.open(sp,"_self");
}

function displayedRecord(n) {
    var sp = new URL(window.location.href);
    sp.searchParams.set('num',n);
    window.open(sp,"_self");
}

function pagination(action) {
    var sp = new URL(window.location.href);
    var change = 0;
    var changeBy = 10;
    if(num != null)
        changeBy = parseInt(num);
    if(action == 'prev'){
        if(firstRecord != null && firstRecord != 0 && (firstRecord-changeBy) >= 0){
            change = parseInt(firstRecord) - changeBy;
        }
    }
    else if(action == 'next'){
        if(firstRecord != null){
            change = parseInt(firstRecord) + parseInt(changeBy);
        } else{
            change = changeBy;
        }

    }
    sp.searchParams.set('firstRecord', change);
    window.open(sp,"_self");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
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

let title = getParameterByName('title');
let year = getParameterByName('year');
let director = getParameterByName('director');
let star = getParameterByName('star');
let genre_id = getParameterByName('gid');
let char = getParameterByName('char');
let sort = getParameterByName('sort');
let num = getParameterByName('num');
let firstRecord = getParameterByName('firstRecord');

// Makes the HTTP GET request and registers on success callback function handleMovieResult
$.ajax({
    dataType: "json",
    url: "api/top20?title="+title+"&year="+year+"&director="+director+"&star="+star+"&gid="
        +genre_id+"&char="+char+"&sort="+sort+"&num="+num+"&firstRecord="+firstRecord,
    method: "GET",
    success: (resultData) => handleMovieResult(resultData),
    error: function() {
        if(firstRecord > 0) {
            jQuery("#movie_table_body").append("<p>No results found. You have reached the end of your search results!</p>");
            document.getElementById("nextButton").disabled = true;
        }
    }
});

