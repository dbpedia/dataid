/**
 * Created by Chile on 1/12/2016.
 */
function tabulate(columns, catalogUrl) {
    var table = d3.select("#canvas")
            .append("table")
            .attr("border","1")
            .attr("id","table")
            .attr("class","display dataTable")
            .attr("width","100%")
            .attr("cellspacing","0"),
        thead = table.append("thead"),
        tfoot = table.append("tfoot"),
        tbody = table.append("tbody")
            .attr("overflow-y", "auto")
            .attr("overflow-x", "hidden");

    //columns.unshift("language");
    var ids = getDataIDsFromCatalog(catalogUrl);
    var response = sendRequest(ids["en"], "GET", null, false, function () {}, function () {});
    var jsonld = JSON.parse(prettify(response, "JSON-LD"));
    var datasets = getDatasetsOfId(jsonld);
    var distributions = getDistributionsOfId(jsonld);
    //append the header row
    if(columns == null)
    {
        columns = [];
        for (var property in data[0]) {
            columns.push(property);
        };
    }
    thead.append("tr")
        .selectAll("th")
        .data(columns)
        .enter()
        .append("th")
        .attr("bgcolor","#FFFFFF")
        .text(function(g) { return g; });

    // create a row for each object in the data
    var rows = tbody.selectAll("tr")
        .data(data)
        .enter()
        .append("tr");

    // create a cell in each row for each columnstatObj[lang]
    var cells = rows.selectAll("td")
        .data(function(row) {
            return columns.map(function(column) {
                return { lang:row['language'], column: column, value: row[column]};
            });
        })
        .enter()
        .append("td")
        .text(function(d) {  return d.value; });


    return table;
}

function getDataIDsFromCatalog(catalogUrl)
{
    var response = sendRequest(catalogUrl, "GET", null, false, function () {}, function () {});
    var jsonld = JSON.parse(prettify(response, "JSON-LD"));
    var ret = {};
    for(var i = 0; i < jsonld["@graph"].length; i++)
    {
        if(jsonld["@graph"][i]["dcat:record"] != null)
        {
            for(var j = 0; j < jsonld["@graph"][i]["dcat:record"].length; j++)
            {
                var id = jsonld["@graph"][i]["dcat:record"][j]["@id"];

                ret[id.substring(id.lastIndexOf("_")+1, id.indexOf(".", id.lastIndexOf("_")))] = jsonld["@graph"][i]["dcat:record"][j]["@id"];
            }
        }
    }
    return ret;
}

function getTableFor(langList)
{
    for(lang in langList)
    {

    }
}

function getDatasetsOfId(id){
    var ret = {};
    for(var i = 0; i < id["@graph"].length; i++) {
        if (id["@graph"][i]["@type"] == "dataid:Dataset") {
            ret[id["@graph"][i]["@id"]] = id["@graph"][i]["dcat:distribution"];
        }
    }
    return ret;
}

function getDistributionsOfId(id){
    var ret = {};
    for(var i = 0; i < id["@graph"].length; i++) {
        if (id["@graph"][i]["@type"] == "dataid:SingleFile") {
            ret[id["@graph"][i]["@id"]] = id["@graph"][i]["dcat:downloadURL"];
        }
    }
    return ret;
}

function getLanguageId(id) {
    if(inp == null)
        inp = JSON.parse(document.getElementById( 'data' ).textContent);
    var ret = [];
    for(var i = 0; i < inp["@graph"].length; i++)
    {
        if(inp["@graph"][i]["level"] != null)
        {
            ret.push(inp["@graph"][i]);
        }
    }
    return ret;
}